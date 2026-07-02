package com.edueasy.user.service.impl;

import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.TeacherAuthResponseDTO;
import com.edueasy.common.dto.TeacherRequestDTO;
import com.edueasy.common.dto.TeacherResponseDTO;
import com.edueasy.common.dto.TeacherSimpleRegisterDTO;
import com.edueasy.common.dto.TeacherSimpleRequestDTO;
import com.edueasy.common.dto.UserStatusUpdateDTO;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.enums.UserRole;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.exception.DuplicateResourceException;
import com.edueasy.common.exception.ResourceNotFoundException;
import com.edueasy.common.model.TeacherSimple;
import com.edueasy.common.security.JwtUtil;
import com.edueasy.user.mapper.TeacherSimpleMapper;
import com.edueasy.user.repository.TeacherSimpleRepository;
import com.edueasy.user.service.TeacherSimpleService;
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
public class TeacherSimpleServiceImpl implements TeacherSimpleService {

    private static final Logger log = LoggerFactory.getLogger(TeacherSimpleServiceImpl.class);

    private final TeacherSimpleRepository teacherRepository;
    private final TeacherSimpleMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public TeacherSimpleServiceImpl(TeacherSimpleRepository teacherRepository,
                                    TeacherSimpleMapper teacherMapper,
                                    PasswordEncoder passwordEncoder,
                                    JwtUtil jwtUtil) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public TeacherResponseDTO registerTeacher(TeacherRequestDTO requestDTO) {
        log.info("Registering new teacher with email: {}", requestDTO.getEmail());

        if (teacherRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Teacher", "email", requestDTO.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        TeacherSimple teacher = teacherMapper.toEntity(requestDTO, encodedPassword);
        teacher.setStatus(UserStatus.ACTIVE);
        teacher = teacherRepository.save(teacher);

        log.info("Teacher registered successfully with uuid: {}", teacher.getUuid());
        return teacherMapper.toResponseDTO(teacher);
    }

    @Override
    @Transactional
    public TeacherAuthResponseDTO registerTeacherSimple(TeacherSimpleRegisterDTO requestDTO) {
        log.info("Registering simple teacher with firstName: {}, lastName: {}",
                requestDTO.getFirstName(), requestDTO.getLastName());

        // Générer l'email
        String baseEmail = requestDTO.getFirstName().toLowerCase().replaceAll("\\s+", "")
                + "." + requestDTO.getLastName().toLowerCase().replaceAll("\\s+", "")
                + "@edueasy.temp";
        String email = baseEmail;
        int counter = 1;
        while (teacherRepository.existsByEmail(email)) {
            email = baseEmail + counter;
            counter++;
        }
        log.info("Generated email: {}", email);

        // Générer le mot de passe par défaut
        String defaultPassword = "Temp@123456";
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        // Générer l'UUID
        String uuid = UUID.randomUUID().toString();

        // Générer le numéro d'enseignant
        String teacherNumber = "TCH-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        // Créer l'entité
        TeacherSimple teacherSimple = teacherMapper.toEntitySimple(requestDTO);
        teacherSimple.setUuid(uuid);
        teacherSimple.setEmail(email);
        teacherSimple.setPasswordHash(encodedPassword);
        teacherSimple.setTeacherNumber(teacherNumber);
        teacherSimple.setStatus(UserStatus.ACTIVE);
        teacherSimple.setLocale("fr");
        teacherSimple.setTimezone("Europe/Paris");

        // 🔥 AJOUTER LE RÔLE - OBLIGATOIRE
        teacherSimple.setRole(UserRole.TEACHER_SIMPLE);

        // 🔥 S'ASSURER QUE LE USERNAME EST DÉFINI
        if (teacherSimple.getUsername() == null || teacherSimple.getUsername().isEmpty()) {
            String username = (requestDTO.getFirstName() + "." + requestDTO.getLastName())
                    .toLowerCase()
                    .replaceAll("[^a-zA-Z0-9.]", "");
            if (username.length() > 30) {
                username = username.substring(0, 30);
            }
            // Vérifier si le username existe déjà
            String finalUsername = username;
            int userCounter = 1;
            while (teacherRepository.existsByUsername(finalUsername)) {
                finalUsername = username + userCounter;
                userCounter++;
            }
            teacherSimple.setUsername(finalUsername);
            log.info("Generated username: {} for teacher", finalUsername);
        }

        // Sauvegarder
        teacherSimple = teacherRepository.save(teacherSimple);
        log.info("Teacher saved with id: {}, email: {}, username: {}, role: {}",
                teacherSimple.getId(), teacherSimple.getEmail(), teacherSimple.getUsername(), teacherSimple.getRole());

        // Générer le token
        String token = jwtUtil.generateIndefiniteTokenUuid(
                teacherSimple.getUuid(),
                teacherSimple.getStatutUserSimple() != null ?
                        teacherSimple.getStatutUserSimple().name() :
                        StatutUserSimple.ACTIF.name()
        );

        TeacherAuthResponseDTO responseDTO = teacherMapper.toResponseSimpleDTO(teacherSimple);
        responseDTO.setToken(token);
        responseDTO.setTokenType("Bearer");
        responseDTO.setExpiresIn(null);

        log.info("Simple teacher registered successfully with uuid: {}", teacherSimple.getUuid());
        return responseDTO;
    }



    @Override
    public TeacherResponseDTO getTeacherById(String teacherId) {
        log.info("Fetching teacher by id: {}", teacherId);

        TeacherSimple teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

        return teacherMapper.toResponseDTO(teacher);
    }

    @Override
    public TeacherResponseDTO getTeacherByEmail(String email) {
        log.info("Fetching teacher by email: {}", email);

        TeacherSimple teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "email", email));

        return teacherMapper.toResponseDTO(teacher);
    }

    @Override
    @Transactional
    public TeacherResponseDTO updateTeacherSimple(String teacherUuid, TeacherSimpleRegisterDTO requestDTO) {
        log.info("Updating teacher with uuid: {}", teacherUuid);

        TeacherSimple teacher = teacherRepository.findByUuid(teacherUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "uuid", teacherUuid));

        if (requestDTO.getFirstName() != null) {
            teacher.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null) {
            teacher.setLastName(requestDTO.getLastName());
        }

        if (requestDTO.getLevelTeacher() != null) {
            teacher.setLevelTeacher(requestDTO.getLevelTeacher());
        }

        if (requestDTO.getStatutUserSimple() != null) {
            teacher.setStatutUserSimple(requestDTO.getStatutUserSimple());
        }

        teacher = teacherRepository.save(teacher);

        log.info("Teacher updated successfully with uuid: {}", teacherUuid);
        return teacherMapper.toResponseDTO(teacher);
    }

    @Override
    @Transactional
    public TeacherResponseDTO updateProfilTeacherSimple(String teacherUuid, TeacherSimpleRequestDTO requestDTO) {
        log.info("Updating teacher profile with uuid: {}", teacherUuid);

        TeacherSimple teacher = teacherRepository.findByUuid(teacherUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "uuid", teacherUuid));

        if (requestDTO.getFirstName() != null && !requestDTO.getFirstName().isEmpty()) {
            teacher.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null && !requestDTO.getLastName().isEmpty()) {
            teacher.setLastName(requestDTO.getLastName());
        }

        if (requestDTO.getEmail() != null && !requestDTO.getEmail().isEmpty()) {
            if (!teacher.getEmail().equals(requestDTO.getEmail()) &&
                    teacherRepository.existsByEmail(requestDTO.getEmail())) {
                throw new DuplicateResourceException("Teacher", "email", requestDTO.getEmail());
            }
            teacher.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getPhoneNumber() != null && !requestDTO.getPhoneNumber().isEmpty()) {
            teacher.setPhoneNumber(requestDTO.getPhoneNumber());
        }

        if (requestDTO.getBirthDate() != null) {
            teacher.setBirthDate(requestDTO.getBirthDate());
        }

        if (requestDTO.getUsername() != null && !requestDTO.getUsername().isEmpty()) {
            teacher.setUsername(requestDTO.getUsername());
        }

        if (requestDTO.getPasswordHash() != null && !requestDTO.getPasswordHash().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(requestDTO.getPasswordHash());
            teacher.setPasswordHash(encodedPassword);
        }

        if (requestDTO.getProfilePictureUrl() != null && !requestDTO.getProfilePictureUrl().isEmpty()) {
            teacher.setProfilePictureUrl(requestDTO.getProfilePictureUrl());
        }

        if (requestDTO.getLevelTeacher() != null) {
            teacher.setLevelTeacher(requestDTO.getLevelTeacher());
        }

        teacher = teacherRepository.save(teacher);

        log.info("Teacher profile updated successfully with uuid: {}", teacherUuid);
        return teacherMapper.toResponseDTO(teacher);
    }

    @Override
    @Transactional
    public void deleteTeacher(String teacherUuid) {
        log.info("Soft deleting teacher with uuid: {}", teacherUuid);

        TeacherSimple teacher = teacherRepository.findByUuid(teacherUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "uuid", teacherUuid));

        teacher.setStatus(UserStatus.DELETED);
        teacherRepository.save(teacher);

        log.info("Teacher soft deleted successfully with uuid: {}", teacherUuid);
    }

    @Override
    @Transactional
    public void permanentDeleteTeacher(String teacherUuid) {
        log.warn("Permanently deleting teacher with uuid: {}", teacherUuid);

        TeacherSimple teacher = teacherRepository.findByUuid(teacherUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "uuid", teacherUuid));

        teacherRepository.delete(teacher);

        log.warn("Teacher permanently deleted successfully with uuid: {}", teacherUuid);
    }

    @Override
    @Transactional
    public void updateTeacherStatus(String teacherUuid, UserStatusUpdateDTO statusUpdate) {
        log.info("Updating teacher status for uuid: {}, new status: {}", teacherUuid, statusUpdate.getStatus());

        TeacherSimple teacher = teacherRepository.findByUuid(teacherUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "uuid", teacherUuid));

        teacher.setStatus(statusUpdate.getStatus());
        teacherRepository.save(teacher);

        log.info("Teacher status updated successfully for uuid: {}", teacherUuid);
    }



    @Override
    @Transactional
    public void changePassword(String teacherUuid, ChangePasswordRequestDTO passwordRequest) {
        log.info("Changing password for teacher with uuid: {}", teacherUuid);

        TeacherSimple teacher = teacherRepository.findByUuid(teacherUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "uuid", teacherUuid));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), teacher.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
        teacher.setPasswordHash(encodedNewPassword);
        teacherRepository.save(teacher);

        log.info("Password changed successfully for teacher with uuid: {}", teacherUuid);
    }

    @Override
    public Page<TeacherResponseDTO> getAllTeachers(Pageable pageable) {
        log.info("Fetching all teachers with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return teacherRepository.findAll(pageable)
                .map(teacherMapper::toResponseDTO);
    }

    @Override
    public Page<TeacherResponseDTO> searchTeachers(String keyword, Pageable pageable) {
        log.info("Searching teachers with keyword: {}", keyword);

        return teacherRepository.search(keyword, pageable)
                .map(teacherMapper::toResponseDTO);
    }

    @Override
    public TeacherResponseDTO getTeacherByUuid(String teacherUuid) {
        log.info("Fetching teacher by uuid: {}", teacherUuid);

        TeacherSimple teacher = teacherRepository.findByUuid(teacherUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "uuid", teacherUuid));

        return teacherMapper.toResponseDTO(teacher);
    }

    @Override
    @Transactional
    public void updateLastLogin(String teacherUuid) {
        teacherRepository.updateLastLoginByUuid(teacherUuid);
    }

    @Override
    public boolean existsByEmail(String email) {
        return teacherRepository.existsByEmail(email);
    }

    @Override
    public long getActiveTeachersCount() {
        return teacherRepository.countByStatus(UserStatus.ACTIVE);
    }

}