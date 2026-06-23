package com.edueasy.common.dto.responses;

import com.edueasy.common.enums.LevelStudent;
import com.edueasy.common.model.Address;
import com.edueasy.common.enums.UserStatus;
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
public class StudentResponseDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate birthDate;
    private String phoneNumber;
    private Address address;
    private UserStatus status;
    private String profilePictureUrl;
    private String locale;
    private String timezone;
    private LevelStudent levelStudent;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String token;
    private String tokenType;
    private String role;
    private Long expiresIn;
}
