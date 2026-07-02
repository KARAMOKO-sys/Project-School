package com.edueasy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportStaffRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String department;
    private String shift;
    private String supportAgentUuid;
    private LocalDate hireDate;
    private String qualification;
    private List<String> responsibilities;
    @Length(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}