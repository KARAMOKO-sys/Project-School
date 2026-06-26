package com.edueasy.user.service;

import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.SupportAgentRequestDTO;
import com.edueasy.common.dto.SupportAgentResponseDTO;
import com.edueasy.common.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupportAgentService {
    SupportAgentResponseDTO createSupportAgent(String adminUuid, SupportAgentRequestDTO requestDTO);

    SupportAgentResponseDTO getSupportAgentByUuid(String adminUuid, String uuid);

    SupportAgentResponseDTO getSupportAgentByEmail(String email);

    SupportAgentResponseDTO modifierSupportAgent(String uuid, SupportAgentRequestDTO requestDTO);

    void deleteSupportAgent(String uuid);

    void permanentDeleteSupportAgent(String uuid);

    void updateSupportAgentStatus(String uuid, UserStatus status);

    Page<SupportAgentResponseDTO> getAllSupportAgents(Pageable pageable, String adminUuid);

    Page<SupportAgentResponseDTO> searchSupportAgents(String keyword, Pageable pageable, String adminUuid);

    void updateLastLogin(String uuid);

    long getActiveSupportAgentsCount(String adminUuid);

    SupportAgentResponseDTO getMyProfile(String supportAgentUuid);

    void changeMyPassword(String supportAgentUuid, ChangePasswordRequestDTO passwordRequest);
}