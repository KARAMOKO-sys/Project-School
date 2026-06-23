package com.edueasy.common.dto.request;

import com.edueasy.common.enums.AdminPermission;
import com.edueasy.common.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phoneNumber;
    private LocalDate birthDate;
    private String username;

    // Informations professionnelles
    private String department;
    private String position;
    private LocalDate hireDate;
    private Integer accessLevel;

    // Permissions
    private List<AdminPermission> permissions;

    // Adresse
    private Address address;
}