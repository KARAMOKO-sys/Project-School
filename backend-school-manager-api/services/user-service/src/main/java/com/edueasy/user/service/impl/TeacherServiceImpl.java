package com.edueasy.user.service.impl;

import com.edueasy.common.dto.TeacherDTO;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.exception.DuplicateResourceException;
import com.edueasy.common.exception.ResourceNotFoundException;
import com.edueasy.common.model.TeacherSimple;
import com.edueasy.user.mapper.TeacherMapper;
import com.edueasy.user.repository.TeacherRepository;
import com.edueasy.user.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TeacherServiceImpl implements TeacherService {

    private static final Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;

    public TeacherServiceImpl(TeacherRepository teacherRepository,
                              TeacherMapper teacherMapper,
                              PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {
        log.info("Creating new teacher with email: {}", teacherDTO.getEmail());

        if (teacherRepository.existsByEmail(teacherDTO.getEmail())) {
            throw new DuplicateResourceException("Teacher", "email", teacherDTO.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(teacherDTO.getPassword());
        TeacherSimple teacher = teacherMapper.toEntity(teacherDTO, encodedPassword);
        teacher.setStatus(UserStatus.ACTIVE);
        teacher = teacherRepository.save(teacher);

        log.info("Teacher created successfully with id: {}", teacher.getId());
        return teacherMapper.toDTO(teacher);
    }

    @Override
    public TeacherDTO getTeacherById(String id) {
        log.info("Fetching teacher by id: {}", id);

        TeacherSimple teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));

        return teacherMapper.toDTO(teacher);
    }


    @Override
    public Optional<TeacherDTO> getTeacherByNumber(String teacherNumber) {
        log.info("Fetching teacher by number: {}", teacherNumber);

        return teacherRepository.findByTeacherNumber(teacherNumber)
                .map(teacherMapper::toDTO);
    }


    @Override
    public List<TeacherDTO> getAllTeachers() {
        log.info("Fetching all teachers");

        return teacherRepository.findAll().stream()
                .map(teacherMapper::toDTO)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public TeacherDTO updateTeacher(String id, TeacherDTO teacherDTO) {
        log.info("Updating teacher with id: {}", id);

        TeacherSimple teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));

        // Mettre à jour les champs
        if (teacherDTO.getFirstName() != null) {
            teacher.setFirstName(teacherDTO.getFirstName());
        }

        if (teacherDTO.getLastName() != null) {
            teacher.setLastName(teacherDTO.getLastName());
        }

        if (teacherDTO.getPhoneNumber() != null) {
            teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        }

        if (teacherDTO.getBirthDate() != null) {
            teacher.setBirthDate(teacherDTO.getBirthDate());
        }

        if (teacherDTO.getEmail() != null && !teacher.getEmail().equals(teacherDTO.getEmail())) {
            if (teacherRepository.existsByEmail(teacherDTO.getEmail())) {
                throw new DuplicateResourceException("Teacher", "email", teacherDTO.getEmail());
            }
            teacher.setEmail(teacherDTO.getEmail());
        }

        if (teacherDTO.getPassword() != null && !teacherDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(teacherDTO.getPassword());
            teacher.setPasswordHash(encodedPassword);
        }

        if (teacherDTO.getProfilePictureUrl() != null) {
            teacher.setProfilePictureUrl(teacherDTO.getProfilePictureUrl());
        }

        if (teacherDTO.getAddress() != null) {
            teacher.setAddress(teacherDTO.getAddress());
        }

        if (teacherDTO.getStatus() != null) {
            teacher.setStatus(teacherDTO.getStatus());
        }

        teacher = teacherRepository.save(teacher);

        log.info("Teacher updated successfully with id: {}", id);
        return teacherMapper.toDTO(teacher);
    }

    @Override
    @Transactional
    public void deleteTeacher(String id) {
        log.info("Soft deleting teacher with id: {}", id);

        TeacherSimple teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));

        teacher.setStatus(UserStatus.DELETED);
        teacherRepository.save(teacher);

        log.info("Teacher soft deleted successfully with id: {}", id);
    }



}