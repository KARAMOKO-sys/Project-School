package com.edueasy.common.dto;

import com.edueasy.common.enums.LevelTeacher;
import com.edueasy.common.enums.StatutUserSimple;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRequestDTO {
    private @NotBlank(
            message = "Email is required"
    ) @Email(
            message = "Invalid email format"
    ) String email;
    private @NotBlank(
            message = "First name is required"
    ) @Size(
            min = 2,
            max = 50,
            message = "First name must be between 2 and 50 characters"
    ) String firstName;
    private @NotBlank(
            message = "Last name is required"
    ) @Size(
            min = 2,
            max = 50,
            message = "Last name must be between 2 and 50 characters"
    ) String lastName;
    private String teacherNumber;
    private StatutUserSimple statutUserSimple;
    private LevelTeacher levelTeacher;
    private @NotBlank(
            message = "Password is required"
    ) @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least 8 characters, one digit, one lowercase, one uppercase, and one special character"
    ) String password;
    private LocalDate birthDate;
    private @Pattern(
            regexp = "^[+][0-9]{10,15}$",
            message = "Phone number must start with + and contain 10-15 digits"
    ) String phoneNumber;
    private String locale;
    private String timezone;
    private List<String> specialties;
}
