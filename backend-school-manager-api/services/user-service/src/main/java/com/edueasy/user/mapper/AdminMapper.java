package com.edueasy.user.mapper;

import com.edueasy.common.dto.AdminRequestDTO;
import com.edueasy.common.dto.AdminResponseDTO;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.Admin;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
public class AdminMapper {

    public AdminMapper() {
    }

    public Admin toEntity(AdminRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Admin admin = new Admin();
        admin.setUuid(UUID.randomUUID().toString());
        admin.setEmail(requestDTO.getEmail());
        admin.setFirstName(requestDTO.getFirstName());
        admin.setLastName(requestDTO.getLastName());
        admin.setPhoneNumber(requestDTO.getPhoneNumber());
        admin.setBirthDate(requestDTO.getBirthDate());
        admin.setUsername(requestDTO.getUsername() != null ? requestDTO.getUsername() : requestDTO.getEmail());
        admin.setAddress(requestDTO.getAddress());
        admin.setDepartment(requestDTO.getDepartment());
        admin.setPosition(requestDTO.getPosition());
        admin.setHireDate(requestDTO.getHireDate());
        admin.setAccessLevel(requestDTO.getAccessLevel() != null ? requestDTO.getAccessLevel() : 1);
        admin.setPermissions(requestDTO.getPermissions() != null ? requestDTO.getPermissions() : new ArrayList<>());
        admin.setStatus(UserStatus.ACTIVE);
        admin.setLocale("fr");
        admin.setTimezone("Europe/Paris");
        admin.setManagedSchoolIds(new ArrayList<>());

        // Générer le numéro d'employé
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        admin.setEmployeeNumber("ADM-" + timestamp + "-" + randomCode);

        return admin;
    }

    public AdminResponseDTO toResponseDTO(Admin admin) {
        if (admin == null) {
            return null;
        }

        return AdminResponseDTO.builder()
                .uuid(admin.getUuid())
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .phoneNumber(admin.getPhoneNumber())
                .birthDate(admin.getBirthDate())
                .username(admin.getUsername())
                .profilePictureUrl(admin.getProfilePictureUrl())
                .employeeNumber(admin.getEmployeeNumber())
                .department(admin.getDepartment())
                .position(admin.getPosition())
                .hireDate(admin.getHireDate())
                .accessLevel(admin.getAccessLevel())
                .permissions(admin.getPermissions())
                .managedSchoolIds(admin.getManagedSchools())
                .status(admin.getStatus())
                .locale(admin.getLocale())
                .timezone(admin.getTimezone())
                .lastLoginAt(admin.getLastLoginAt())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }

    public void updateEntity(Admin admin, AdminRequestDTO requestDTO) {
        if (requestDTO == null) {
            return;
        }

        if (requestDTO.getEmail() != null) {
            admin.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getFirstName() != null) {
            admin.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null) {
            admin.setLastName(requestDTO.getLastName());
        }

        if (requestDTO.getPhoneNumber() != null) {
            admin.setPhoneNumber(requestDTO.getPhoneNumber());
        }

        if (requestDTO.getBirthDate() != null) {
            admin.setBirthDate(requestDTO.getBirthDate());
        }

        if (requestDTO.getUsername() != null) {
            admin.setUsername(requestDTO.getUsername());
        }

        if (requestDTO.getDepartment() != null) {
            admin.setDepartment(requestDTO.getDepartment());
        }

        if (requestDTO.getPosition() != null) {
            admin.setPosition(requestDTO.getPosition());
        }

        if (requestDTO.getHireDate() != null) {
            admin.setHireDate(requestDTO.getHireDate());
        }

        if (requestDTO.getAccessLevel() != null) {
            admin.setAccessLevel(requestDTO.getAccessLevel());
        }

        if (requestDTO.getPermissions() != null) {
            admin.setPermissions(requestDTO.getPermissions());
        }

        if (requestDTO.getAddress() != null) {
            admin.setAddress(requestDTO.getAddress());
        }

        // NOTE: updatedAt sera automatiquement mis à jour par AuditTimestamps
    }

    public AdminResponseDTO toAuthResponseDTO(Admin admin, String token, Long expiresIn) {
        AdminResponseDTO responseDTO = this.toResponseDTO(admin);
        if (responseDTO != null) {
            responseDTO.setToken(token);
            responseDTO.setTokenType("Bearer");
            responseDTO.setExpiresIn(expiresIn);
        }
        return responseDTO;
    }
}