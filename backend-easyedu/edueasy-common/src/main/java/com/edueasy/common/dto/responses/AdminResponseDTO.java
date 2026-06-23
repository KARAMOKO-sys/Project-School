package com.edueasy.common.dto.responses;

import com.edueasy.common.enums.AdminPermission;
import com.edueasy.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
    private String uuid;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String username;
    private String profilePictureUrl;

    // Informations professionnelles
    private String employeeNumber;
    private String department;
    private String position;
    private LocalDate hireDate;
    private Integer accessLevel;
    private List<AdminPermission> permissions;
    private List<String> managedSchoolIds;

    // Statut
    private UserStatus status;
    private String locale;
    private String timezone;
    private LocalDateTime lastLoginAt;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Token (pour la réponse d'authentification)
    private String token;
    private String tokenType;
    private Long expiresIn;
}