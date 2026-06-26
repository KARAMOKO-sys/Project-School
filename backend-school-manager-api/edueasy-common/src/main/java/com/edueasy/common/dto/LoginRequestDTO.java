package com.edueasy.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    private @NotBlank(
            message = "Email is required"
    ) @Email(
            message = "Invalid email format"
    ) String email;
    private @NotBlank(
            message = "Password is required"
    ) String password;
}
