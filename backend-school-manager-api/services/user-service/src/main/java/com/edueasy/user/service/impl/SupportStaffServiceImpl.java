package com.edueasy.user.service.impl;

import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.SupportStaffRequestDTO;
import com.edueasy.common.dto.SupportStaffResponseDTO;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.exception.DuplicateResourceException;
import com.edueasy.common.exception.ResourceNotFoundException;
import com.edueasy.common.exception.UnauthorizedAccessException;
import com.edueasy.common.model.SupportAgent;
import com.edueasy.common.model.SupportStaff;
import com.edueasy.user.mapper.SupportMapper;
import com.edueasy.user.repository.SupportAgentRepository;
import com.edueasy.user.repository.SupportStaffRepository;
import com.edueasy.user.service.SupportStaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SupportStaffServiceImpl implements SupportStaffService {

    private static final Logger log = LoggerFactory.getLogger(SupportStaffServiceImpl.class);

    private final SupportStaffRepository supportStaffRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final SupportMapper supportMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "Temp@123456";

    public SupportStaffServiceImpl(SupportStaffRepository supportStaffRepository,
                                   SupportAgentRepository supportAgentRepository,
                                   SupportMapper supportMapper,
                                   PasswordEncoder passwordEncoder) {
        this.supportStaffRepository = supportStaffRepository;
        this.supportAgentRepository = supportAgentRepository;
        this.supportMapper = supportMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public SupportStaffResponseDTO createSupportStaff(String supportAgentUuid, SupportStaffRequestDTO requestDTO) {
        log.info("Support Agent {} is creating support staff with email: {}", supportAgentUuid, requestDTO.getEmail());

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        if (supportStaffRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("SupportStaff", "email", requestDTO.getEmail());
        }

        SupportStaff staff = supportMapper.toSupportStaffEntity(requestDTO);
        staff.setSupportAgentUuid(supportAgentUuid);

        // 🔥 GÉRER LE MOT DE PASSE
        String rawPassword = requestDTO.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            // Si aucun mot de passe fourni, utiliser le mot de passe par défaut
            rawPassword = DEFAULT_PASSWORD;
            log.info("Using default password for support staff: {}", requestDTO.getEmail());
        } else {
            log.info("Custom password provided for support staff: {}", requestDTO.getEmail());
        }

        // Encoder le mot de passe
        staff.setPasswordHash(passwordEncoder.encode(rawPassword));

        // 🔥 GÉNÉRER LE USERNAME SI NÉCESSAIRE
        if (staff.getUsername() == null || staff.getUsername().isEmpty()) {
            String baseUsername = requestDTO.getEmail().split("@")[0];
            baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

            if (baseUsername.length() > 30) {
                baseUsername = baseUsername.substring(0, 30);
            }

            String username = baseUsername;
            int counter = 1;

            while (supportStaffRepository.existsByUsername(username)) {
                username = baseUsername + counter;
                counter++;
                if (counter > 100) {
                    username = baseUsername + UUID.randomUUID().toString().substring(0, 6);
                    break;
                }
            }
            staff.setUsername(username);
            log.info("Generated username: {} for email: {}", username, requestDTO.getEmail());
        }

        staff.setRole(com.edueasy.common.enums.UserRole.SUPPORT_STAFF);
        staff.setStatus(UserStatus.ACTIVE);

        // Initialiser les champs d'audit
        staff.initializeAuditFields();

        staff = supportStaffRepository.save(staff);

        log.info("Support staff created successfully with uuid: {} by support agent: {}", staff.getUuid(), supportAgentUuid);
        return supportMapper.toSupportStaffResponseDTO(staff);
    }

    @Override
    public SupportStaffResponseDTO getSupportStaffByUuid(String uuid, String supportAgentUuid) {
        log.info("Fetching support staff by uuid: {}", uuid);

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        SupportStaff staff = supportStaffRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", uuid));

        return supportMapper.toSupportStaffResponseDTO(staff);
    }

    @Override
    public SupportStaffResponseDTO getSupportStaffByEmail(String email) {
        log.info("Fetching support staff by email: {}", email);

        SupportStaff staff = supportStaffRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "email", email));

        return supportMapper.toSupportStaffResponseDTO(staff);
    }

    @Override
    @Transactional
    public SupportStaffResponseDTO updateSupportStaff(String supportAgentUuid, String uuid, SupportStaffRequestDTO requestDTO) {
        log.info("Updating support staff with uuid: {}", uuid);

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        SupportStaff staff = supportStaffRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", uuid));

        if (requestDTO.getEmail() != null &&
                !staff.getEmail().equals(requestDTO.getEmail()) &&
                supportStaffRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("SupportStaff", "email", requestDTO.getEmail());
        }

        supportMapper.updateSupportStaffEntity(staff, requestDTO);
        staff = supportStaffRepository.save(staff);

        log.info("Support staff updated successfully with uuid: {}", uuid);
        return supportMapper.toSupportStaffResponseDTO(staff);
    }

    @Override
    @Transactional
    public void deleteSupportStaff(String supportAgentUuid, String uuid) {
        log.info("Soft deleting support staff with uuid: {}", uuid);

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        SupportStaff staff = supportStaffRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", uuid));

        staff.setStatus(UserStatus.DELETED);
        supportStaffRepository.save(staff);

        log.info("Support staff soft deleted successfully with uuid: {}", uuid);
    }

    @Override
    @Transactional
    public void permanentDeleteSupportStaff(String supportAgentUuid, String uuid) {
        log.warn("Permanently deleting support staff with uuid: {}", uuid);

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        SupportStaff staff = supportStaffRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", uuid));

        supportStaffRepository.delete(staff);
        log.warn("Support staff permanently deleted successfully with uuid: {}", uuid);
    }

    @Override
    @Transactional
    public void updateSupportStaffStatus(String supportAgentUuid, String uuid, UserStatus status) {
        log.info("Updating support staff status for uuid: {} to {}", uuid, status);

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        SupportStaff staff = supportStaffRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", uuid));

        staff.setStatus(status);
        supportStaffRepository.save(staff);

        log.info("Support staff status updated successfully for uuid: {}", uuid);
    }

    @Override
    public Page<SupportStaffResponseDTO> getAllSupportStaff(String supportAgentUuid, Pageable pageable) {
        log.info("Fetching all support staff with pagination");

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        return supportStaffRepository.findAll(pageable)
                .map(supportMapper::toSupportStaffResponseDTO);
    }

    @Override
    public Page<SupportStaffResponseDTO> searchSupportStaff(String supportAgentUuid, String keyword, Pageable pageable) {
        log.info("Searching support staff with keyword: {}", keyword);

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        return supportStaffRepository.search(keyword, pageable)
                .map(supportMapper::toSupportStaffResponseDTO);
    }

    @Override
    @Transactional
    public void updateLastLogin(String uuid) {
        supportStaffRepository.updateLastLogin(uuid);
    }

    @Override
    public long getActiveSupportStaffCount(String supportAgentUuid) {
        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("Support Agent not found or inactive"));

        if (agent.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Support Agent account is not active");
        }

        return supportStaffRepository.countByStatus(UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void assignTicket(String staffUuid, String ticketId) {
        log.info("Assigning ticket {} to staff {}", ticketId, staffUuid);

        SupportStaff staff = supportStaffRepository.findByUuid(staffUuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", staffUuid));

        staff.assignTicket(ticketId);
        supportStaffRepository.save(staff);

        log.info("Ticket assigned successfully");
    }

    @Override
    @Transactional
    public void resolveTicket(String staffUuid, String ticketId) {
        log.info("Resolving ticket {} for staff {}", ticketId, staffUuid);

        SupportStaff staff = supportStaffRepository.findByUuid(staffUuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", staffUuid));

        staff.resolveTicket(ticketId);
        supportStaffRepository.save(staff);

        log.info("Ticket resolved successfully");
    }

    @Override
    public SupportStaffResponseDTO getMyProfile(String supportStaffUuid) {
        log.info("Support staff fetching own profile: {}", supportStaffUuid);

        SupportStaff supportStaff = supportStaffRepository.findByUuid(supportStaffUuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", supportStaffUuid));

        return supportMapper.toSupportStaffResponseDTO(supportStaff);
    }

    @Override
    @Transactional
    public void changeMyPassword(String supportStaffUuid, ChangePasswordRequestDTO passwordRequest) {
        log.info("Support staff changing password: {}", supportStaffUuid);

        SupportStaff supportStaff = supportStaffRepository.findByUuid(supportStaffUuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", supportStaffUuid));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), supportStaff.getPasswordHash())) {
            throw new UnauthorizedAccessException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
        supportStaff.setPasswordHash(encodedNewPassword);
        supportStaffRepository.save(supportStaff);

        log.info("Support staff password changed successfully: {}", supportStaffUuid);
    }

    @Override
    @Transactional
    public SupportStaffResponseDTO modifierSupportStaff(String uuid, SupportStaffRequestDTO requestDTO) {
        log.info("Updating support staff with uuid: {}", uuid);

        SupportStaff supportStaff = supportStaffRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportStaff", "uuid", uuid));

        if (requestDTO.getEmail() != null &&
                !supportStaff.getEmail().equals(requestDTO.getEmail()) &&
                supportStaffRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("SupportStaff", "email", requestDTO.getEmail());
        }

        supportMapper.updateSupportStaffEntity(supportStaff, requestDTO);
        supportStaff = supportStaffRepository.save(supportStaff);

        log.info("Support staff updated successfully with uuid: {}", uuid);
        return supportMapper.toSupportStaffResponseDTO(supportStaff);
    }
}