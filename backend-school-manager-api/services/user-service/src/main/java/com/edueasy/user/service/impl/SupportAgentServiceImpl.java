package com.edueasy.user.service.impl;

import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.SupportAgentRequestDTO;
import com.edueasy.common.dto.SupportAgentResponseDTO;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.exception.DuplicateResourceException;
import com.edueasy.common.exception.ResourceNotFoundException;
import com.edueasy.common.exception.UnauthorizedAccessException;
import com.edueasy.common.model.Admin;
import com.edueasy.common.model.SupportAgent;
import com.edueasy.user.mapper.SupportMapper;
import com.edueasy.user.repository.AdminRepository;
import com.edueasy.user.repository.SupportAgentRepository;
import com.edueasy.user.service.SupportAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SupportAgentServiceImpl implements SupportAgentService {

    private static final Logger log = LoggerFactory.getLogger(SupportAgentServiceImpl.class);

    private final SupportAgentRepository supportAgentRepository;
    private final SupportMapper supportMapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    private static final String DEFAULT_PASSWORD = "Temp@123456";

    public SupportAgentServiceImpl(SupportAgentRepository supportAgentRepository,
                                   SupportMapper supportMapper,
                                   PasswordEncoder passwordEncoder,
                                   AdminRepository adminRepository) {
        this.supportAgentRepository = supportAgentRepository;
        this.supportMapper = supportMapper;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }


    @Override
    @Transactional
    public SupportAgentResponseDTO createSupportAgent(String adminUuid, SupportAgentRequestDTO requestDTO) {
        log.info("Admin {} is creating support agent with email: {}", adminUuid, requestDTO.getEmail());

        Admin admin = adminRepository.findByUuid(adminUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("User is not an admin"));

        if (admin.getAccessLevel() < 5) {
            throw new UnauthorizedAccessException("Only super admin (access level 5+) can create support agents");
        }

        if (supportAgentRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("SupportAgent", "email", requestDTO.getEmail());
        }

        SupportAgent agent = supportMapper.toSupportAgentEntity(requestDTO);

        // 🔥 GÉNÉRER LE USERNAME SI NÉCESSAIRE
        if (agent.getUsername() == null || agent.getUsername().isEmpty()) {
            String baseUsername = requestDTO.getEmail().split("@")[0];
            baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

            if (baseUsername.length() > 30) {
                baseUsername = baseUsername.substring(0, 30);
            }

            String username = baseUsername;
            int counter = 1;

            while (supportAgentRepository.existsByUsername(username)) {
                username = baseUsername + counter;
                counter++;
                if (counter > 100) {
                    username = baseUsername + UUID.randomUUID().toString().substring(0, 6);
                    break;
                }
            }
            agent.setUsername(username);
            log.info("Generated username: {} for email: {}", username, requestDTO.getEmail());
        }

        // 🔥 GÉRER LE MOT DE PASSE
        String rawPassword = requestDTO.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            // Si aucun mot de passe fourni, utiliser le mot de passe par défaut
            rawPassword = DEFAULT_PASSWORD;
            log.info("Using default password for support agent: {}", requestDTO.getEmail());
        } else {
            log.info("Custom password provided for support agent: {}", requestDTO.getEmail());
        }

        // Encoder le mot de passe
        agent.setPasswordHash(passwordEncoder.encode(rawPassword));

        // Générer le staffNumber si nécessaire
        if (agent.getStaffNumber() == null || agent.getStaffNumber().isEmpty()) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String randomCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            agent.setStaffNumber("SA-" + timestamp + "-" + randomCode);
        }

        agent.setRole(com.edueasy.common.enums.UserRole.SUPPORT_AGENT);
        agent.setStatus(UserStatus.ACTIVE);

        // Initialiser les champs d'audit
        agent.initializeAuditFields();

        agent = supportAgentRepository.save(agent);

        log.info("Support agent created successfully with uuid: {} by admin: {}", agent.getUuid(), adminUuid);
        return supportMapper.toSupportAgentResponseDTO(agent);
    }

    @Override
    public SupportAgentResponseDTO getSupportAgentByUuid(String adminUuid, String uuid) {
        log.info("Admin {} is fetching support agent by uuid: {}", adminUuid, uuid);

        Admin admin = adminRepository.findByUuid(adminUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("User is not an admin"));

        if (admin.getAccessLevel() < 5) {
            throw new UnauthorizedAccessException("Only super admin can view support agents");
        }

        SupportAgent agent = supportAgentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportAgent", "uuid", uuid));

        return supportMapper.toSupportAgentResponseDTO(agent);
    }

    @Override
    public SupportAgentResponseDTO getSupportAgentByEmail(String email) {
        log.info("Fetching support agent by email: {}", email);

        SupportAgent agent = supportAgentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("SupportAgent", "email", email));

        return supportMapper.toSupportAgentResponseDTO(agent);
    }

    @Override
    @Transactional
    public SupportAgentResponseDTO modifierSupportAgent(String uuid, SupportAgentRequestDTO requestDTO) {
        log.info("Updating support agent with uuid: {}", uuid);

        SupportAgent agent = supportAgentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportAgent", "uuid", uuid));

        if (requestDTO.getEmail() != null &&
                !agent.getEmail().equals(requestDTO.getEmail()) &&
                supportAgentRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("SupportAgent", "email", requestDTO.getEmail());
        }

        supportMapper.updateSupportAgentEntity(agent, requestDTO);
        agent = supportAgentRepository.save(agent);

        log.info("Support agent updated successfully with uuid: {}", uuid);
        return supportMapper.toSupportAgentResponseDTO(agent);
    }

    @Override
    @Transactional
    public void deleteSupportAgent(String uuid) {
        log.info("Soft deleting support agent with uuid: {}", uuid);

        SupportAgent agent = supportAgentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportAgent", "uuid", uuid));

        agent.setStatus(UserStatus.DELETED);
        supportAgentRepository.save(agent);

        log.info("Support agent soft deleted successfully with uuid: {}", uuid);
    }

    @Override
    @Transactional
    public void permanentDeleteSupportAgent(String uuid) {
        log.warn("Permanently deleting support agent with uuid: {}", uuid);

        SupportAgent agent = supportAgentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportAgent", "uuid", uuid));

        supportAgentRepository.delete(agent);
        log.warn("Support agent permanently deleted successfully with uuid: {}", uuid);
    }

    @Override
    @Transactional
    public void updateSupportAgentStatus(String uuid, UserStatus status) {
        log.info("Updating support agent status for uuid: {} to {}", uuid, status);

        SupportAgent agent = supportAgentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportAgent", "uuid", uuid));

        agent.setStatus(status);
        supportAgentRepository.save(agent);

        log.info("Support agent status updated successfully for uuid: {}", uuid);
    }

    @Override
    public Page<SupportAgentResponseDTO> getAllSupportAgents(Pageable pageable, String adminUuid) {
        log.info("Fetching all support agents with pagination");

        Admin admin = adminRepository.findByUuid(adminUuid)
                .orElseThrow(() -> new UnauthorizedAccessException("User is not an admin"));

        if (!admin.isSuperAdmin()) {
            throw new UnauthorizedAccessException("Only super admin can view support agents");
        }

        return supportAgentRepository.findAll(pageable)
                .map(supportMapper::toSupportAgentResponseDTO);
    }

    @Override
    public Page<SupportAgentResponseDTO> searchSupportAgents(String keyword, Pageable pageable, String adminUuid) {
        log.info("Searching support agents with keyword: {}", keyword);

        Admin admin = adminRepository.findByUuid(adminUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "uuid", adminUuid));

        if (!admin.isSuperAdmin()) {
            throw new UnauthorizedAccessException("Only super admin can view support agents");
        }

        return supportAgentRepository.search(keyword, pageable)
                .map(supportMapper::toSupportAgentResponseDTO);
    }

    @Override
    @Transactional
    public void updateLastLogin(String uuid) {
        supportAgentRepository.updateLastLogin(uuid);
    }

    @Override
    public long getActiveSupportAgentsCount(String adminUuid) {
        Admin admin = adminRepository.findByUuid(adminUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "uuid", adminUuid));

        if (!admin.isSuperAdmin()) {
            throw new UnauthorizedAccessException("Only super admin can view support agents");
        }

        return supportAgentRepository.countByStatus(UserStatus.ACTIVE);
    }

    @Override
    public SupportAgentResponseDTO getMyProfile(String supportAgentUuid) {
        log.info("Support agent fetching own profile: {}", supportAgentUuid);

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportAgent", "uuid", supportAgentUuid));

        return supportMapper.toSupportAgentResponseDTO(agent);
    }

    @Override
    @Transactional
    public void changeMyPassword(String supportAgentUuid, ChangePasswordRequestDTO passwordRequest) {
        log.info("Support agent changing password: {}", supportAgentUuid);

        SupportAgent agent = supportAgentRepository.findByUuid(supportAgentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("SupportAgent", "uuid", supportAgentUuid));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), agent.getPasswordHash())) {
            throw new UnauthorizedAccessException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
        agent.setPasswordHash(encodedNewPassword);
        supportAgentRepository.save(agent);

        log.info("Support agent password changed successfully: {}", supportAgentUuid);
    }
}