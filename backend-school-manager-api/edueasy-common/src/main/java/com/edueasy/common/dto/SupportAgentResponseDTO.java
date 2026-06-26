package com.edueasy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportAgentResponseDTO {
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String staffNumber;
    private String department;
    private String shift;
    private LocalDate hireDate;
    private String qualification;
    private Boolean isOnCall;
    private List<String> responsibilities;
    private List<String> assignedTickets;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
