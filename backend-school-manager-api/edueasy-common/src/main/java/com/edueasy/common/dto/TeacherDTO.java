package com.edueasy.common.dto;

import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.Address;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    private String id;
    private String uuid;
    @Schema(
            description = "Teacher email address",
            example = "john.doe@school.com"
    )
    private @NotBlank(
            message = "Email is required"
    ) @Email(
            message = "Invalid email format"
    ) String email;
    @Schema(
            description = "Teacher first name",
            example = "John"
    )
    private @NotBlank(
            message = "First name is required"
    ) @Size(
            min = 2,
            max = 50,
            message = "First name must be between 2 and 50 characters"
    ) String firstName;
    @Schema(
            description = "Teacher last name",
            example = "Doe"
    )
    private @NotBlank(
            message = "Last name is required"
    ) @Size(
            min = 2,
            max = 50,
            message = "Last name must be between 2 and 50 characters"
    ) String lastName;
    private String fullName;
    @JsonIgnore
    private String password;
    private String teacherNumber;
    private LocalDate birthDate;
    private String phoneNumber;
    private Address address;
    private UserStatus status;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
