package com.edueasy.common.dto.responses;

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
public class SupportStaffResponseDTO {
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String staffNumber;
    private String department;
    private String shift;
    private String supportAgentUuid;
    private LocalDate hireDate;
    private String qualification;
    private Boolean isOnCall;
    private List<String> responsibilities;
    private List<String> assignedTickets;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
