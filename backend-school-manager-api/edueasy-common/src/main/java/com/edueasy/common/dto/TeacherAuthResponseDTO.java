package com.edueasy.common.dto;

import com.edueasy.common.enums.LevelTeacher;
import com.edueasy.common.enums.StatutUserSimple;
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
    private LevelTeacher levelTeacher;
    private StatutUserSimple statutUserSimple;
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
