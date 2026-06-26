package com.edueasy.user.controller;

import com.edueasy.common.dto.ApiResponse;
import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.SupportAgentRequestDTO;
import com.edueasy.common.dto.SupportAgentResponseDTO;
import com.edueasy.common.dto.PageResponse;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.user.service.CurrentUserService;
import com.edueasy.user.service.SupportAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/support-agents")
@SecurityRequirement(name = "Bearer Authentication")
public class SupportAgentController {

    private static final Logger log = LoggerFactory.getLogger(SupportAgentController.class);

    private final SupportAgentService supportAgentService;
    private final CurrentUserService currentUserService;

    public SupportAgentController(SupportAgentService supportAgentService,
                                  CurrentUserService currentUserService) {
        this.supportAgentService = supportAgentService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "Create a new support agent (Admin only)")
    public ResponseEntity<ApiResponse<SupportAgentResponseDTO>> createSupportAgent(
            @RequestBody @Valid SupportAgentRequestDTO requestDTO) {
        String adminUuid = currentUserService.getCurrentUserUuid();
        log.info("POST /api/admin/support-agents - Admin {} is creating support agent", adminUuid);

        SupportAgentResponseDTO response = supportAgentService.createSupportAgent(adminUuid, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Support agent created successfully"));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get support agent by UUID (Admin only)")
    public ResponseEntity<ApiResponse<SupportAgentResponseDTO>> getSupportAgentByUuid(
            @PathVariable String uuid) {
        String adminUuid = currentUserService.getCurrentUserUuid();
        log.info("GET /api/admin/support-agents/{} - Admin {} is fetching support agent", uuid, adminUuid);

        SupportAgentResponseDTO response = supportAgentService.getSupportAgentByUuid(adminUuid, uuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get support agent by email (Admin only)")
    public ResponseEntity<ApiResponse<SupportAgentResponseDTO>> getSupportAgentByEmail(
            @PathVariable String email) {
        log.info("GET /api/admin/support-agents/email/{}", email);

        SupportAgentResponseDTO response = supportAgentService.getSupportAgentByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update support agent (Admin only)")
    public ResponseEntity<ApiResponse<SupportAgentResponseDTO>> modifierSupportAgent(
            @PathVariable String uuid,
            @RequestBody @Valid SupportAgentRequestDTO requestDTO) {
        String adminUuid = currentUserService.getCurrentUserUuid();
        log.info("PUT /api/admin/support-agents/{} - Admin {}", uuid, adminUuid);

        SupportAgentResponseDTO response = supportAgentService.modifierSupportAgent(uuid, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Support agent updated successfully"));
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Soft delete support agent (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteSupportAgent(
            @PathVariable String uuid) {
        String adminUuid = currentUserService.getCurrentUserUuid();
        log.info("DELETE /api/admin/support-agents/{} - Admin {}", uuid, adminUuid);

        supportAgentService.deleteSupportAgent(uuid);
        return ResponseEntity.ok(ApiResponse.success("Support agent deleted successfully"));
    }

    @DeleteMapping("/{uuid}/permanent")
    @Operation(summary = "Permanently delete support agent (Admin only)")
    public ResponseEntity<ApiResponse<Void>> permanentDeleteSupportAgent(
            @PathVariable String uuid) {
        String adminUuid = currentUserService.getCurrentUserUuid();
        log.warn("DELETE /api/admin/support-agents/{}/permanent - Admin {}", uuid, adminUuid);

        supportAgentService.permanentDeleteSupportAgent(uuid);
        return ResponseEntity.ok(ApiResponse.success("Support agent permanently deleted"));
    }

    @PatchMapping("/{uuid}/status")
    @Operation(summary = "Update support agent status (Admin only)")
    public ResponseEntity<ApiResponse<Void>> updateSupportAgentStatus(
            @PathVariable String uuid,
            @RequestParam UserStatus status) {
        String adminUuid = currentUserService.getCurrentUserUuid();
        log.info("PATCH /api/admin/support-agents/{}/status - Admin {}", uuid, adminUuid);

        supportAgentService.updateSupportAgentStatus(uuid, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all support agents with pagination (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<SupportAgentResponseDTO>>> getAllSupportAgents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("GET /api/admin/support-agents - page={}, size={}", page, size);

        String adminUuid = currentUserService.getCurrentUserUuid();
        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<SupportAgentResponseDTO> supportAgents = supportAgentService.getAllSupportAgents(pageable, adminUuid);
        PageResponse<SupportAgentResponseDTO> pageResponse = PageResponse.from(supportAgents);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/search")
    @Operation(summary = "Search support agents (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<SupportAgentResponseDTO>>> searchSupportAgents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/admin/support-agents/search - keyword={}", keyword);

        String adminUuid = currentUserService.getCurrentUserUuid();
        Pageable pageable = PageRequest.of(page, size);

        Page<SupportAgentResponseDTO> results = supportAgentService.searchSupportAgents(keyword, pageable, adminUuid);
        PageResponse<SupportAgentResponseDTO> pageResponse = PageResponse.from(results);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/statistics/active-count")
    @Operation(summary = "Get active support agents count (Admin only)")
    public ResponseEntity<ApiResponse<Long>> getActiveSupportAgentsCount() {
        log.info("GET /api/admin/support-agents/statistics/active-count");

        String adminUuid = currentUserService.getCurrentUserUuid();
        long count = supportAgentService.getActiveSupportAgentsCount(adminUuid);

        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get my profile (Support Agent only)")
    public ResponseEntity<ApiResponse<SupportAgentResponseDTO>> getMyProfile() {
        String agentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        log.info("GET /api/admin/support-agents/profile - Support agent: {}", agentUuid);

        SupportAgentResponseDTO response = supportAgentService.getMyProfile(agentUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change my password (Support Agent only)")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(
            @RequestBody @Valid ChangePasswordRequestDTO passwordRequest) {
        String agentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        log.info("POST /api/admin/support-agents/change-password - Support agent: {}", agentUuid);

        supportAgentService.changeMyPassword(agentUuid, passwordRequest);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
}