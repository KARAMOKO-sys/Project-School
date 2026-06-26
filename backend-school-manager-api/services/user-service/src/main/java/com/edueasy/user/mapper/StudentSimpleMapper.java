package com.edueasy.user.mapper;

import com.edueasy.common.dto.StudentRequestDTO;
import com.edueasy.common.dto.StudentResponseDTO;
import com.edueasy.common.dto.StudentSimpleRegisterDTO;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.model.StudentSimple;
import org.springframework.stereotype.Component;

@Component
public class StudentSimpleMapper {

    public StudentSimpleMapper() {
    }

    public StudentResponseDTO toResponseDTO(StudentSimple student) {
        if (student == null) {
            return null;
        }

        return StudentResponseDTO.builder()
                .uuid(student.getUuid())
                .email(student.getEmail())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .fullName(student.getFullName())
                .birthDate(student.getBirthDate())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .status(student.getStatus())
                .statutUserSimple(student.getStatutUserSimple())
                .levelStudent(student.getLevelStudent())
                .profilePictureUrl(student.getProfilePictureUrl())
                .locale(student.getLocale())
                .timezone(student.getTimezone())
                .lastLoginAt(student.getLastLoginAt())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    public StudentSimple toEntity(StudentRequestDTO requestDTO, String encodedPassword) {
        if (requestDTO == null) {
            return null;
        }

        StudentSimple student = new StudentSimple();
        student.setUuid(java.util.UUID.randomUUID().toString());
        student.setEmail(requestDTO.getEmail());
        student.setFirstName(requestDTO.getFirstName());
        student.setLastName(requestDTO.getLastName());
        student.setPasswordHash(encodedPassword);
        student.setBirthDate(requestDTO.getBirthDate());
        student.setPhoneNumber(requestDTO.getPhoneNumber());
        student.setLocale(requestDTO.getLocale() != null ? requestDTO.getLocale() : "fr");
        student.setTimezone(requestDTO.getTimezone() != null ? requestDTO.getTimezone() : "Europe/Paris");
        student.setStatutUserSimple(StatutUserSimple.EN_ATTENTE);

        return student;
    }

    public void updateEntity(StudentSimple student, StudentRequestDTO requestDTO, String encodedPassword) {
        if (requestDTO == null) {
            return;
        }

        if (requestDTO.getEmail() != null) {
            student.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getFirstName() != null) {
            student.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null) {
            student.setLastName(requestDTO.getLastName());
        }

        if (encodedPassword != null) {
            student.setPasswordHash(encodedPassword);
        }

        if (requestDTO.getBirthDate() != null) {
            student.setBirthDate(requestDTO.getBirthDate());
        }

        if (requestDTO.getPhoneNumber() != null) {
            student.setPhoneNumber(requestDTO.getPhoneNumber());
        }

        if (requestDTO.getLocale() != null) {
            student.setLocale(requestDTO.getLocale());
        }

        if (requestDTO.getTimezone() != null) {
            student.setTimezone(requestDTO.getTimezone());
        }
    }

    public StudentSimple toEntitySimple(StudentSimpleRegisterDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        StudentSimple studentSimple = new StudentSimple();
        studentSimple.setUuid(java.util.UUID.randomUUID().toString());
        studentSimple.setFirstName(requestDTO.getFirstName());
        studentSimple.setLastName(requestDTO.getLastName());
        studentSimple.setStatutUserSimple(StatutUserSimple.EN_ATTENTE);
        studentSimple.setLevelStudent(requestDTO.getLevelStudent());

        return studentSimple;
    }

    public StudentResponseDTO toResponseSimpleDTO(StudentSimple studentSimple) {
        if (studentSimple == null) {
            return null;
        }

        return StudentResponseDTO.builder()
                .uuid(studentSimple.getUuid())
                .email(studentSimple.getEmail())
                .firstName(studentSimple.getFirstName())
                .lastName(studentSimple.getLastName())
                .fullName(studentSimple.getFullName())
                .statutUserSimple(studentSimple.getStatutUserSimple())
                .levelStudent(studentSimple.getLevelStudent())
                .createdAt(studentSimple.getCreatedAt())
                .updatedAt(studentSimple.getUpdatedAt())
                .build();
    }
}