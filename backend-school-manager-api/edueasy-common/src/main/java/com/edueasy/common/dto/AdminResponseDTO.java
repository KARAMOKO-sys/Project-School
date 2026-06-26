package com.edueasy.common.dto;

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
    private String employeeNumber;
    private String department;
    private String position;
    private LocalDate hireDate;
    private Integer accessLevel;
    private List<AdminPermission> permissions;
    private List<String> managedSchoolIds;
    private UserStatus status;
    private String locale;
    private String timezone;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String token;
    private String tokenType;
    private Long expiresIn;
}
