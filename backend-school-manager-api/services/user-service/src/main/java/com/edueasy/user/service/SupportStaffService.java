package com.edueasy.user.service;

import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.SupportStaffRequestDTO;
import com.edueasy.common.dto.SupportStaffResponseDTO;
import com.edueasy.common.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupportStaffService {
    SupportStaffResponseDTO createSupportStaff(String supervisorUuid, SupportStaffRequestDTO requestDTO);

    SupportStaffResponseDTO getSupportStaffByUuid(String uuid, String supportAgentUuid);

    SupportStaffResponseDTO getSupportStaffByEmail(String email);

    SupportStaffResponseDTO updateSupportStaff(String supportAgentUuid, String uuid, SupportStaffRequestDTO requestDTO);

    void deleteSupportStaff(String supportAgentUuid, String uuid);

    void permanentDeleteSupportStaff(String supportAgentUuid, String uuid);

    void updateSupportStaffStatus(String supportAgentUuid, String uuid, UserStatus status);

    Page<SupportStaffResponseDTO> getAllSupportStaff(String supportAgentUuid, Pageable pageable);

    Page<SupportStaffResponseDTO> searchSupportStaff(String supportAgentUuid, String keyword, Pageable pageable);

    void updateLastLogin(String uuid);

    long getActiveSupportStaffCount(String supportAgentUuid);

    void assignTicket(String staffUuid, String ticketId);

    void resolveTicket(String staffUuid, String ticketId);

    SupportStaffResponseDTO getMyProfile(String supportAgentUuid);

    void changeMyPassword(String supportAgentUuid, ChangePasswordRequestDTO passwordRequest);

    SupportStaffResponseDTO modifierSupportStaff(String uuid, SupportStaffRequestDTO requestDTO);
}
