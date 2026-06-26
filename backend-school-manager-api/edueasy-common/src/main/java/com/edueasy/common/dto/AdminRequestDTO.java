package com.edueasy.common.dto;

import com.edueasy.common.enums.AdminPermission;
import com.edueasy.common.model.Address;
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
public class AdminRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private AdminPermission permission;
    private String phoneNumber;
    private LocalDate birthDate;
    private String username;
    private String department;
    private String position;
    private LocalDate hireDate;
    private Integer accessLevel;
    private List<AdminPermission> permissions;
    private Address address;

}
