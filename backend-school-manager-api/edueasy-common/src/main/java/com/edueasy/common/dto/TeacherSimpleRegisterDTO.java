package com.edueasy.common.dto;

import com.edueasy.common.enums.LevelTeacher;
import com.edueasy.common.enums.StatutUserSimple;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSimpleRegisterDTO {
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
    private StatutUserSimple statutUserSimple;
    private LevelTeacher levelTeacher;
}
