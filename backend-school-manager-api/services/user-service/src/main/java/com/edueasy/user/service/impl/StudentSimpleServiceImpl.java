package com.edueasy.user.service.impl;

import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.StudentRequestDTO;
import com.edueasy.common.dto.StudentResponseDTO;
import com.edueasy.common.dto.StudentSimpleRegisterDTO;
import com.edueasy.common.dto.UserStatusUpdateDTO;
import com.edueasy.common.enums.LevelStudent;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.exception.DuplicateResourceException;
import com.edueasy.common.exception.ResourceNotFoundException;
import com.edueasy.common.model.StudentSimple;
import com.edueasy.common.security.JwtUtil;
import com.edueasy.user.mapper.StudentSimpleMapper;
import com.edueasy.user.repository.StudentSimpleRepository;
import com.edueasy.user.service.StudentSimpleService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class StudentSimpleServiceImpl implements StudentSimpleService {

    private static final Logger log = LoggerFactory.getLogger(StudentSimpleServiceImpl.class);

    private final StudentSimpleRepository studentSimpleRepository;
    private final StudentSimpleMapper studentSimpleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public StudentSimpleServiceImpl(StudentSimpleRepository studentSimpleRepository,
                                    StudentSimpleMapper studentSimpleMapper,
                                    PasswordEncoder passwordEncoder,
                                    JwtUtil jwtUtil) {
        this.studentSimpleRepository = studentSimpleRepository;
        this.studentSimpleMapper = studentSimpleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public StudentResponseDTO registerStudent(StudentRequestDTO requestDTO) {
        log.info("Registering new student with email: {}", requestDTO.getEmail());

        if (studentSimpleRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Student", "email", requestDTO.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        StudentSimple student = studentSimpleMapper.toEntity(requestDTO, encodedPassword);
        student.setStatus(UserStatus.ACTIVE);
        student.setUuid(UUID.randomUUID().toString());
        student = studentSimpleRepository.save(student);

        log.info("Student registered successfully with id: {}", student.getId());
        return studentSimpleMapper.toResponseDTO(student);
    }

    @Transactional
    public StudentResponseDTO registerStudentSimple(StudentSimpleRegisterDTO requestDTO) {
        log.info("=== DÉBUT registerStudentSimple ===");
        log.info("firstName: {}", requestDTO.getFirstName());
        log.info("lastName: {}", requestDTO.getLastName());
        log.info("levelStudent: {}", requestDTO.getLevelStudent());

        try {
            // Générer l'email
            String baseEmail = requestDTO.getFirstName().toLowerCase().replaceAll("\\s+", "")
                    + "." + requestDTO.getLastName().toLowerCase().replaceAll("\\s+", "")
                    + "@edueasy.temp";
            String email = baseEmail;
            int counter = 1;
            while (studentSimpleRepository.existsByEmail(email)) {
                email = baseEmail + counter;
                counter++;
            }
            log.info("Generated email: {}", email);

            // Générer le mot de passe par défaut
            String defaultPassword = "Temp@123456";
            String encodedPassword = passwordEncoder.encode(defaultPassword);

            // Générer l'UUID
            String uuid = UUID.randomUUID().toString();

            // Créer l'entité
            StudentSimple studentSimple = studentSimpleMapper.toEntitySimple(requestDTO);
            log.info("Student entity created: {}", studentSimple);

            // Définir les propriétés
            studentSimple.setUuid(uuid);
            studentSimple.setEmail(email);
            studentSimple.setPasswordHash(encodedPassword);
            studentSimple.setStatus(UserStatus.ACTIVE);
            studentSimple.setLocale("fr");
            studentSimple.setTimezone("Europe/Paris");

            if (requestDTO.getLevelStudent() != null) {
                studentSimple.setLevelStudent(requestDTO.getLevelStudent());
                log.info("✅ LevelStudent set to: {}", requestDTO.getLevelStudent());
            } else {
                log.warn("⚠️ LevelStudent is NULL!");
            }

            // Sauvegarder
            studentSimple = studentSimpleRepository.save(studentSimple);
            log.info("✅ Student saved with id: {}, email: {}, level: {}",
                    studentSimple.getId(), studentSimple.getEmail(), studentSimple.getLevelStudent());

            // Générer le token
            String token = jwtUtil.generateIndefiniteTokenUuid(
                    studentSimple.getUuid(),
                    studentSimple.getStatutUserSimple() != null ?
                            studentSimple.getStatutUserSimple().name() :
                            StatutUserSimple.EN_ATTENTE.name()
            );

            // Créer la réponse
            StudentResponseDTO responseDTO = studentSimpleMapper.toResponseSimpleDTO(studentSimple);
            responseDTO.setToken(token);
            responseDTO.setTokenType("Bearer");
            responseDTO.setExpiresIn(null);

            log.info("✅ Simple student registered successfully with uuid: {}", studentSimple.getUuid());
            log.info("=== FIN registerStudentSimple ===");
            return responseDTO;

        } catch (Exception e) {
            log.error("❌ ERROR in registerStudentSimple: {}", e.getMessage(), e);
            throw e;
        }
    }

    public StudentResponseDTO getStudentByUuid(String studentUuid) {
        log.info("Fetching student by uuid: {}", studentUuid);
        StudentSimple student = studentSimpleRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "uuid", studentUuid));
        return studentSimpleMapper.toResponseDTO(student);
    }

    public StudentResponseDTO getStudentByEmail(String email) {
        log.info("Fetching student by email: {}", email);
        StudentSimple student = studentSimpleRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "email", email));
        return studentSimpleMapper.toResponseDTO(student);
    }

    public StudentResponseDTO updateStudent(String studentUuid, StudentRequestDTO requestDTO) {
        log.info("Updating student with uuid: {}", studentUuid);

        StudentSimple student = studentSimpleRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "uuid", studentUuid));

        // Vérifier si l'email est déjà utilisé
        if (!student.getEmail().equals(requestDTO.getEmail()) &&
                studentSimpleRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Student", "email", requestDTO.getEmail());
        }

        String encodedPassword = null;
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        }

        studentSimpleMapper.updateEntity(student, requestDTO, encodedPassword);
        student = studentSimpleRepository.save(student);

        log.info("Student updated successfully with uuid: {}", studentUuid);
        return studentSimpleMapper.toResponseDTO(student);
    }

    public StudentResponseDTO updateProfilStudentSimple(String studentSimpleUuid, StudentRequestDTO requestDTO) {
        log.info("Updating student profile with uuid: {}", studentSimpleUuid);

        StudentSimple studentSimple = studentSimpleRepository.findByUuid(studentSimpleUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student simple", "uuid", studentSimpleUuid));

        // Mettre à jour les champs
        if (requestDTO.getFirstName() != null && !requestDTO.getFirstName().isEmpty()) {
            studentSimple.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null && !requestDTO.getLastName().isEmpty()) {
            studentSimple.setLastName(requestDTO.getLastName());
        }

        if (requestDTO.getEmail() != null && !requestDTO.getEmail().isEmpty()) {
            if (!studentSimple.getEmail().equals(requestDTO.getEmail()) &&
                    studentSimpleRepository.existsByEmail(requestDTO.getEmail())) {
                throw new DuplicateResourceException("Student", "email", requestDTO.getEmail());
            }
            studentSimple.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getPhoneNumber() != null && !requestDTO.getPhoneNumber().isEmpty()) {
            studentSimple.setPhoneNumber(requestDTO.getPhoneNumber());
        }

        if (requestDTO.getBirthDate() != null) {
            studentSimple.setBirthDate(requestDTO.getBirthDate());
        }

        if (requestDTO.getUsername() != null && !requestDTO.getUsername().isEmpty()) {
            studentSimple.setUsername(requestDTO.getUsername());
        }

        if (requestDTO.getPasswordHash() != null && !requestDTO.getPasswordHash().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(requestDTO.getPasswordHash());
            studentSimple.setPasswordHash(encodedPassword);
        }

        if (requestDTO.getProfilePictureUrl() != null && !requestDTO.getProfilePictureUrl().isEmpty()) {
            studentSimple.setProfilePictureUrl(requestDTO.getProfilePictureUrl());
        }

        if (requestDTO.getLevelStudent() != null) {
            studentSimple.setLevelStudent(requestDTO.getLevelStudent());
        }

        studentSimple = studentSimpleRepository.save(studentSimple);
        log.info("Student updated successfully with uuid: {}", studentSimpleUuid);
        return studentSimpleMapper.toResponseDTO(studentSimple);
    }

    public void deleteStudent(String studentUuid) {
        log.info("Soft deleting student with uuid: {}", studentUuid);

        StudentSimple student = studentSimpleRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "uuid", studentUuid));

        student.setStatus(UserStatus.DELETED);
        studentSimpleRepository.save(student);

        log.info("Student soft deleted successfully with uuid: {}", studentUuid);
    }

    @Transactional
    public void permanentDeleteStudent(String studentUuid) {
        log.warn("Permanently deleting student with uuid: {}", studentUuid);

        StudentSimple student = studentSimpleRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "uuid", studentUuid));

        studentSimpleRepository.delete(student);
        log.warn("Student permanently deleted successfully with uuid: {}", studentUuid);
    }

    @Transactional
    public void updateStudentStatus(String studentUuid, UserStatusUpdateDTO statusUpdate) {
        log.info("Updating student status for uuid: {}, new status: {}", studentUuid, statusUpdate.getStatus());

        StudentSimple student = studentSimpleRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "uuid", studentUuid));

        student.setStatus(statusUpdate.getStatus());
        studentSimpleRepository.save(student);

        log.info("Student status updated successfully for uuid: {}", studentUuid);
    }

    @Transactional
    public void changePassword(String studentUuid, ChangePasswordRequestDTO passwordRequest) {
        log.info("Changing password for student with uuid: {}", studentUuid);

        StudentSimple student = studentSimpleRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "uuid", studentUuid));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), student.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
        student.setPasswordHash(encodedNewPassword);
        studentSimpleRepository.save(student);

        log.info("Password changed successfully for student with uuid: {}", studentUuid);
    }

    public Page<StudentResponseDTO> getAllStudents(Pageable pageable) {
        log.info("Fetching all students with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return studentSimpleRepository.findAll(pageable)
                .map(studentSimpleMapper::toResponseDTO);
    }

    public Page<StudentResponseDTO> searchStudents(String keyword, Pageable pageable) {
        log.info("Searching students with keyword: {}", keyword);

        return studentSimpleRepository.findByFirstNameContainingOrLastNameContainingOrEmailContaining(
                        keyword, keyword, keyword, pageable)
                .map(studentSimpleMapper::toResponseDTO);
    }

    @Transactional
    public void updateLastLogin(String studentUuid) {
        studentSimpleRepository.updateLastLogin(studentUuid);
    }

    public boolean existsByEmail(String email) {
        return studentSimpleRepository.existsByEmail(email);
    }

    public long getActiveStudentsCount() {
        return studentSimpleRepository.countByStatus(UserStatus.ACTIVE);
    }

    public long getTodayRegistrationsCount() {
        return studentSimpleRepository.countTodayRegistrations();
    }

    /**
     * Vérifie si un étudiant existe par UUID
     */
    public boolean existsByUuid(String uuid) {
        return studentSimpleRepository.existsByEmail(uuid);
    }

    /**
     * Récupère un étudiant par son UUID (avec gestion des erreurs)
     */
    public StudentSimple getStudentEntityByUuid(String studentUuid) {
        return studentSimpleRepository.findByUuid(studentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "uuid", studentUuid));
    }

    /**
     * Active un étudiant
     */
    @Transactional
    public void activateStudent(String studentUuid) {
        log.info("Activating student with uuid: {}", studentUuid);
        StudentSimple student = getStudentEntityByUuid(studentUuid);
        student.setStatus(UserStatus.ACTIVE);
        studentSimpleRepository.save(student);
        log.info("Student activated successfully with uuid: {}", studentUuid);
    }

    /**
     * Suspend un étudiant
     */
    @Transactional
    public void suspendStudent(String studentUuid, String reason) {
        log.info("Suspending student with uuid: {}", studentUuid);
        StudentSimple student = getStudentEntityByUuid(studentUuid);
        student.setStatus(UserStatus.SUSPENDED);
        studentSimpleRepository.save(student);
        log.info("Student suspended successfully with uuid: {}", studentUuid);
    }

    /**
     * Bannit un étudiant
     */
    @Transactional
    public void banStudent(String studentUuid, String reason) {
        log.info("Banning student with uuid: {}", studentUuid);
        StudentSimple student = getStudentEntityByUuid(studentUuid);
        student.setStatus(UserStatus.BANNED);
        studentSimpleRepository.save(student);
        log.info("Student banned successfully with uuid: {}", studentUuid);
    }

    /**
     * Archive un étudiant
     */
    @Transactional
    public void archiveStudent(String studentUuid) {
        log.info("Archiving student with uuid: {}", studentUuid);
        StudentSimple student = getStudentEntityByUuid(studentUuid);
        student.setStatus(UserStatus.ARCHIVED);
        studentSimpleRepository.save(student);
        log.info("Student archived successfully with uuid: {}", studentUuid);
    }

    /**
     * Restaure un étudiant
     */
    @Transactional
    public void restoreStudent(String studentUuid) {
        log.info("Restoring student with uuid: {}", studentUuid);
        StudentSimple student = getStudentEntityByUuid(studentUuid);
        student.setStatus(UserStatus.ACTIVE);
        studentSimpleRepository.save(student);
        log.info("Student restored successfully with uuid: {}", studentUuid);
    }

    /**
     * Compte le nombre total d'étudiants
     */
    public long countAllStudents() {
        return studentSimpleRepository.count();
    }

    /**
     * Compte les étudiants par statut
     */
    public long countStudentsByStatus(UserStatus status) {
        return studentSimpleRepository.countByStatus(status);
    }

    /**
     * Récupère les étudiants par niveau
     */
    public Page<StudentResponseDTO> getStudentsByLevel(LevelStudent level, Pageable pageable) {
        log.info("Fetching students by level: {}", level);
        return studentSimpleRepository.findByLevelStudent(level, pageable)
                .map(studentSimpleMapper::toResponseDTO);
    }

    /**
     * Récupère les étudiants par statut simple
     */
    public Page<StudentResponseDTO> getStudentsBySimpleStatus(StatutUserSimple statut, Pageable pageable) {
        log.info("Fetching students by simple status: {}", statut);
        return studentSimpleRepository.findByStatutUserSimple(statut, pageable)
                .map(studentSimpleMapper::toResponseDTO);
    }
}