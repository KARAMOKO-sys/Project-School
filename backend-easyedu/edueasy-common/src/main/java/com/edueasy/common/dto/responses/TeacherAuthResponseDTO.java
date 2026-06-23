package com.edueasy.common.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAuthResponseDTO {
    private String id;
    private String uuid;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String teacherNumber;
    private String token;
    private String tokenType;
    private String role;
    private Long expiresIn;
}
