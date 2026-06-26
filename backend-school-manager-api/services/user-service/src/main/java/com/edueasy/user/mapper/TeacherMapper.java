package com.edueasy.user.mapper;

import com.edueasy.common.dto.TeacherDTO;
import com.edueasy.common.model.TeacherSimple;
import org.springframework.stereotype.Component;

@Component
public class TeacherMapper {
    public TeacherMapper() {
    }

    public TeacherDTO toDTO(TeacherSimple teacher) {
        return teacher == null ? null : TeacherDTO.builder().email(teacher.getEmail()).firstName(teacher.getFirstName()).lastName(teacher.getLastName()).fullName(teacher.getFullName()).birthDate(teacher.getBirthDate()).phoneNumber(teacher.getPhoneNumber()).address(teacher.getAddress()).status(teacher.getStatus()).profilePictureUrl(teacher.getProfilePictureUrl()).build();
    }

    public TeacherSimple toEntity(TeacherDTO teacherDTO, String encodedPassword) {
        if (teacherDTO == null) {
            return null;
        } else {
            TeacherSimple teacher = new TeacherSimple();
            teacher.setEmail(teacherDTO.getEmail());
            teacher.setFirstName(teacherDTO.getFirstName());
            teacher.setLastName(teacherDTO.getLastName());
            teacher.setPasswordHash(encodedPassword);
            teacher.setBirthDate(teacherDTO.getBirthDate());
            teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
            teacher.setAddress(teacherDTO.getAddress());
            teacher.setStatus(teacherDTO.getStatus());
            teacher.setProfilePictureUrl(teacherDTO.getProfilePictureUrl());
            return teacher;
        }
    }
}

