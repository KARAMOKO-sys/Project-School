package com.edueasy.user.mapper;

import com.edueasy.common.dto.TeacherAuthResponseDTO;
import com.edueasy.common.dto.TeacherRequestDTO;
import com.edueasy.common.dto.TeacherResponseDTO;
import com.edueasy.common.dto.TeacherSimpleRegisterDTO;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.model.TeacherSimple;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TeacherSimpleMapper {

    public TeacherSimpleMapper() {
    }

    public TeacherResponseDTO toResponseDTO(TeacherSimple teacher) {
        if (teacher == null) {
            return null;
        }

        return TeacherResponseDTO.builder()
                .id(teacher.getId())
                .uuid(teacher.getUuid())
                .email(teacher.getEmail())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .fullName(teacher.getFullName())
                .teacherNumber(teacher.getTeacherNumber())
                .birthDate(teacher.getBirthDate())
                .phoneNumber(teacher.getPhoneNumber())
                .address(teacher.getAddress())
                .status(teacher.getStatus())
                .statutUserSimple(teacher.getStatutUserSimple())
                .levelTeacher(teacher.getLevelTeacher())
                .profilePictureUrl(teacher.getProfilePictureUrl())
                .locale(teacher.getLocale())
                .timezone(teacher.getTimezone())
                .lastLoginAt(teacher.getLastLoginAt())
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
    }

    public TeacherSimple toEntity(TeacherRequestDTO requestDTO, String encodedPassword) {
        if (requestDTO == null) {
            return null;
        }

        TeacherSimple teacher = new TeacherSimple();
        teacher.setUuid(UUID.randomUUID().toString());
        teacher.setEmail(requestDTO.getEmail());
        teacher.setFirstName(requestDTO.getFirstName());
        teacher.setLastName(requestDTO.getLastName());
        teacher.setPasswordHash(encodedPassword);
        teacher.setBirthDate(requestDTO.getBirthDate());
        teacher.setPhoneNumber(requestDTO.getPhoneNumber());
        teacher.setLocale(requestDTO.getLocale() != null ? requestDTO.getLocale() : "fr");
        teacher.setTimezone(requestDTO.getTimezone() != null ? requestDTO.getTimezone() : "Europe/Paris");
        teacher.setStatus(com.edueasy.common.enums.UserStatus.PENDING);

        // Générer le numéro d'enseignant si non fourni
        if (requestDTO.getTeacherNumber() == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String randomCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            teacher.setTeacherNumber("TCH-" + timestamp + "-" + randomCode);
        } else {
            teacher.setTeacherNumber(requestDTO.getTeacherNumber());
        }

        return teacher;
    }

    public TeacherAuthResponseDTO toResponseSimpleDTO(TeacherSimple teacher) {
        if (teacher == null) {
            return null;
        }

        return TeacherAuthResponseDTO.builder()
                .uuid(teacher.getUuid())
                .email(teacher.getEmail())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .fullName(teacher.getFullName())
                .teacherNumber(teacher.getTeacherNumber())
                .statutUserSimple(teacher.getStatutUserSimple())
                .levelTeacher(teacher.getLevelTeacher())
                .build();
    }

    public TeacherSimple toEntitySimple(TeacherSimpleRegisterDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        TeacherSimple teacher = new TeacherSimple();
        teacher.setUuid(UUID.randomUUID().toString());
        teacher.setFirstName(requestDTO.getFirstName());
        teacher.setLastName(requestDTO.getLastName());
        teacher.setStatutUserSimple(StatutUserSimple.EN_ATTENTE);
        teacher.setLevelTeacher(requestDTO.getLevelTeacher());
        teacher.setStatus(com.edueasy.common.enums.UserStatus.PENDING);

        return teacher;
    }

    public void updateEntity(TeacherSimple teacher, TeacherRequestDTO requestDTO, String encodedPassword) {
        if (requestDTO == null) {
            return;
        }

        if (requestDTO.getEmail() != null) {
            teacher.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getFirstName() != null) {
            teacher.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null) {
            teacher.setLastName(requestDTO.getLastName());
        }

        if (encodedPassword != null) {
            teacher.setPasswordHash(encodedPassword);
        }

        if (requestDTO.getBirthDate() != null) {
            teacher.setBirthDate(requestDTO.getBirthDate());
        }

        if (requestDTO.getPhoneNumber() != null) {
            teacher.setPhoneNumber(requestDTO.getPhoneNumber());
        }

        if (requestDTO.getLocale() != null) {
            teacher.setLocale(requestDTO.getLocale());
        }

        if (requestDTO.getTimezone() != null) {
            teacher.setTimezone(requestDTO.getTimezone());
        }
    }

    public void updateTeacherSimpleEntity(TeacherSimple teacher, TeacherSimpleRegisterDTO requestDTO) {
        if (requestDTO == null) {
            return;
        }

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
    }
}