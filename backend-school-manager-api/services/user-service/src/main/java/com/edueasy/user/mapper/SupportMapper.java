package com.edueasy.user.mapper;

import com.edueasy.common.dto.SupportAgentRequestDTO;
import com.edueasy.common.dto.SupportAgentResponseDTO;
import com.edueasy.common.dto.SupportStaffRequestDTO;
import com.edueasy.common.dto.SupportStaffResponseDTO;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.SupportAgent;
import com.edueasy.common.model.SupportStaff;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class SupportMapper {

    public SupportMapper() {
    }

    // ===== SupportAgent Mappings =====

    public SupportAgent toSupportAgentEntity(SupportAgentRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        SupportAgent agent = new SupportAgent();
        agent.setFirstName(requestDTO.getFirstName());
        agent.setLastName(requestDTO.getLastName());
        agent.setEmail(requestDTO.getEmail());
        agent.setPhoneNumber(requestDTO.getPhoneNumber());
        agent.setDepartment(requestDTO.getDepartment());
        agent.setShift(requestDTO.getShift());
        agent.setHireDate(requestDTO.getHireDate());
        agent.setQualification(requestDTO.getQualification());
        agent.setResponsibilities(requestDTO.getResponsibilities() != null ? requestDTO.getResponsibilities() : new ArrayList<>());
        agent.setAssignedTickets(new ArrayList<>());
        agent.setIsOnCall(false);
        agent.setStatus(UserStatus.ACTIVE);
        agent.setLocale("fr");
        agent.setTimezone("Europe/Paris");

        // Générer le numéro de personnel
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        agent.setStaffNumber("SA-" + timestamp + "-" + randomCode);

        // Générer l'UUID
        agent.setUuid(UUID.randomUUID().toString());

        // NOTE: Les champs createdAt et updatedAt seront automatiquement gérés
        // par AuditTimestamps via @PrePersist et @PreUpdate

        return agent;
    }

    public SupportAgentResponseDTO toSupportAgentResponseDTO(SupportAgent agent) {
        if (agent == null) {
            return null;
        }

        return SupportAgentResponseDTO.builder()
                .uuid(agent.getUuid())
                .firstName(agent.getFirstName())
                .lastName(agent.getLastName())
                .email(agent.getEmail())
                .phoneNumber(agent.getPhoneNumber())
                .staffNumber(agent.getStaffNumber())
                .department(agent.getDepartment())
                .shift(agent.getShift())
                .hireDate(agent.getHireDate())
                .qualification(agent.getQualification())
                .isOnCall(agent.getIsOnCall())
                .responsibilities(agent.getResponsibilities())
                .assignedTickets(agent.getAssignedTickets())
                .status(agent.getStatus() != null ? agent.getStatus().name() : null)
                .createdAt(agent.getCreatedAt())
                .updatedAt(agent.getUpdatedAt())
                .build();
    }

    public void updateSupportAgentEntity(SupportAgent agent, SupportAgentRequestDTO requestDTO) {
        if (requestDTO == null) {
            return;
        }

        if (requestDTO.getFirstName() != null) {
            agent.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null) {
            agent.setLastName(requestDTO.getLastName());
        }

        if (requestDTO.getEmail() != null) {
            agent.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getPhoneNumber() != null) {
            agent.setPhoneNumber(requestDTO.getPhoneNumber());
        }

        if (requestDTO.getDepartment() != null) {
            agent.setDepartment(requestDTO.getDepartment());
        }

        if (requestDTO.getShift() != null) {
            agent.setShift(requestDTO.getShift());
        }

        if (requestDTO.getHireDate() != null) {
            agent.setHireDate(requestDTO.getHireDate());
        }

        if (requestDTO.getQualification() != null) {
            agent.setQualification(requestDTO.getQualification());
        }

        if (requestDTO.getResponsibilities() != null) {
            agent.setResponsibilities(requestDTO.getResponsibilities());
        }

        // NOTE: updatedAt sera automatiquement mis à jour par AuditTimestamps
    }

    // ===== SupportStaff Mappings =====

    public SupportStaff toSupportStaffEntity(SupportStaffRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        SupportStaff staff = new SupportStaff();
        staff.setFirstName(requestDTO.getFirstName());
        staff.setLastName(requestDTO.getLastName());
        staff.setEmail(requestDTO.getEmail());
        staff.setPhoneNumber(requestDTO.getPhoneNumber());
        staff.setDepartment(requestDTO.getDepartment());
        staff.setShift(requestDTO.getShift());
        staff.setSupportAgentUuid(requestDTO.getSupportAgentUuid());
        staff.setHireDate(requestDTO.getHireDate());
        staff.setQualification(requestDTO.getQualification());
        staff.setResponsibilities(requestDTO.getResponsibilities() != null ? requestDTO.getResponsibilities() : new ArrayList<>());
        staff.setAssignedTickets(new ArrayList<>());
        staff.setIsOnCall(false);
        staff.setStatus(UserStatus.ACTIVE);
        staff.setLocale("fr");
        staff.setTimezone("Europe/Paris");

        // Générer le numéro de personnel
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        staff.setStaffNumber("SS-" + timestamp + "-" + randomCode);

        // Générer l'UUID
        staff.setUuid(UUID.randomUUID().toString());

        // NOTE: Les champs createdAt et updatedAt seront automatiquement gérés
        // par AuditTimestamps via @PrePersist et @PreUpdate

        return staff;
    }

    public SupportStaffResponseDTO toSupportStaffResponseDTO(SupportStaff staff) {
        if (staff == null) {
            return null;
        }

        return SupportStaffResponseDTO.builder()
                .uuid(staff.getUuid())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .staffNumber(staff.getStaffNumber())
                .department(staff.getDepartment())
                .shift(staff.getShift())
                .supportAgentUuid(staff.getSupportAgentUuid())
                .hireDate(staff.getHireDate())
                .qualification(staff.getQualification())
                .isOnCall(staff.getIsOnCall())
                .responsibilities(staff.getResponsibilities())
                .assignedTickets(staff.getAssignedTickets())
                .status(staff.getStatus() != null ? staff.getStatus().name() : null)
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .build();
    }

    public void updateSupportStaffEntity(SupportStaff staff, SupportStaffRequestDTO requestDTO) {
        if (requestDTO == null) {
            return;
        }

        if (requestDTO.getFirstName() != null) {
            staff.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null) {
            staff.setLastName(requestDTO.getLastName());
        }

        if (requestDTO.getEmail() != null) {
            staff.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getPhoneNumber() != null) {
            staff.setPhoneNumber(requestDTO.getPhoneNumber());
        }

        if (requestDTO.getDepartment() != null) {
            staff.setDepartment(requestDTO.getDepartment());
        }

        if (requestDTO.getShift() != null) {
            staff.setShift(requestDTO.getShift());
        }

        if (requestDTO.getSupportAgentUuid() != null) {
            staff.setSupportAgentUuid(requestDTO.getSupportAgentUuid());
        }

        if (requestDTO.getHireDate() != null) {
            staff.setHireDate(requestDTO.getHireDate());
        }

        if (requestDTO.getQualification() != null) {
            staff.setQualification(requestDTO.getQualification());
        }

        if (requestDTO.getResponsibilities() != null) {
            staff.setResponsibilities(requestDTO.getResponsibilities());
        }

        // NOTE: updatedAt sera automatiquement mis à jour par AuditTimestamps
    }
}