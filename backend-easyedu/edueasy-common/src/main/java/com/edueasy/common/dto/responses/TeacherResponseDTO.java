package com.edueasy.common.dto.responses;

import com.edueasy.common.model.Address;
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
public class TeacherResponseDTO {
    private Long id;
    private String uuid;
    private String email;
    private String firstName;
    private String lastName;
    //@Schema(description = "Teacher unique number", example = "TCH-2024-001")
    private String teacherNumber;  // Ajouter ce champ
    private String fullName;
    private LocalDate birthDate;
    private String phoneNumber;
    private Address address;
    private UserStatus status;
    private String profilePictureUrl;
    private String locale;
    private String timezone;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> specialties;
}
