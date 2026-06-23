package com.edueasy.common.dto.request;

import com.edueasy.common.enums.LevelTeacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSimpleRequestDTO {
    private   String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String username;
    private String passwordHash;
    private String profilePictureUrl;
    private LevelTeacher levelTeacher;

}
