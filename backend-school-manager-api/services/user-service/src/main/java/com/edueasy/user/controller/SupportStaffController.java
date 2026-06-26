package com.edueasy.user.controller;

import com.edueasy.common.dto.ApiResponse;
import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.PageResponse;
import com.edueasy.common.dto.SupportStaffRequestDTO;
import com.edueasy.common.dto.SupportStaffResponseDTO;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.SupportStaff;
import com.edueasy.user.service.CurrentUserService;
import com.edueasy.user.service.SupportStaffService;
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
@RequestMapping("/api/support-agents/staff")
@SecurityRequirement(name = "Bearer Authentication")
public class SupportStaffController {

    private static final Logger log = LoggerFactory.getLogger(SupportStaffController.class);

    private final SupportStaffService supportStaffService;
    private final CurrentUserService currentUserService;

    public SupportStaffController(SupportStaffService supportStaffService,
                                  CurrentUserService currentUserService) {
        this.supportStaffService = supportStaffService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "Create a new support staff (Support Agent only)")
    public ResponseEntity<ApiResponse<SupportStaffResponseDTO>> createSupportStaff(
            @RequestBody @Valid SupportStaffRequestDTO requestDTO) {
        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        log.info("POST /api/support-agents/staff - Support Agent {} is creating support staff", supportAgentUuid);

        SupportStaffResponseDTO response = supportStaffService.createSupportStaff(supportAgentUuid, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Support staff created successfully"));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get support staff by UUID (Support Agent only)")
    public ResponseEntity<ApiResponse<SupportStaffResponseDTO>> getSupportStaffByUuid(
            @PathVariable String uuid) {
        log.info("GET /api/support-agents/staff/{}", uuid);

        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        SupportStaffResponseDTO response = supportStaffService.getSupportStaffByUuid(uuid, supportAgentUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get support staff by email (Support Agent only)")
    public ResponseEntity<ApiResponse<SupportStaffResponseDTO>> getSupportStaffByEmail(
            @PathVariable String email) {
        log.info("GET /api/support-agents/staff/email/{}", email);

        SupportStaffResponseDTO response = supportStaffService.getSupportStaffByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update support staff (Support Agent only)")
    public ResponseEntity<ApiResponse<SupportStaffResponseDTO>> updateSupportStaff(
            @PathVariable String uuid,
            @RequestBody @Valid SupportStaffRequestDTO requestDTO) {
        log.info("PUT /api/support-agents/staff/{}", uuid);

        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        SupportStaffResponseDTO response = supportStaffService.updateSupportStaff(supportAgentUuid, uuid, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Support staff updated successfully"));
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Soft delete support staff (Support Agent only)")
    public ResponseEntity<ApiResponse<Void>> deleteSupportStaff(
            @PathVariable String uuid) {
        log.info("DELETE /api/support-agents/staff/{}", uuid);

        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        supportStaffService.deleteSupportStaff(supportAgentUuid, uuid);
        return ResponseEntity.ok(ApiResponse.success("Support staff deleted successfully"));
    }

    @DeleteMapping("/{uuid}/permanent")
    @Operation(summary = "Permanently delete support staff (Support Agent only)")
    public ResponseEntity<ApiResponse<Void>> permanentDeleteSupportStaff(
            @PathVariable String uuid) {
        log.warn("DELETE /api/support-agents/staff/{}/permanent", uuid);

        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        supportStaffService.permanentDeleteSupportStaff(supportAgentUuid, uuid);
        return ResponseEntity.ok(ApiResponse.success("Support staff permanently deleted"));
    }

    @PatchMapping("/{uuid}/status")
    @Operation(summary = "Update support staff status (Support Agent only)")
    public ResponseEntity<ApiResponse<Void>> updateSupportStaffStatus(
            @PathVariable String uuid,
            @RequestParam UserStatus status) {
        log.info("PATCH /api/support-agents/staff/{}/status", uuid);

        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        supportStaffService.updateSupportStaffStatus(supportAgentUuid, uuid, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all support staff with pagination (Support Agent only)")
    public ResponseEntity<ApiResponse<PageResponse<SupportStaffResponseDTO>>> getAllSupportStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("GET /api/support-agents/staff - page={}, size={}", page, size);

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        Page<SupportStaffResponseDTO> staffList = supportStaffService.getAllSupportStaff(supportAgentUuid, pageable);
        PageResponse<SupportStaffResponseDTO> pageResponse = PageResponse.from(staffList);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/search")
    @Operation(summary = "Search support staff (Support Agent only)")
    public ResponseEntity<ApiResponse<PageResponse<SupportStaffResponseDTO>>> searchSupportStaff(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/support-agents/staff/search - keyword={}", keyword);

        Pageable pageable = PageRequest.of(page, size);
        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();

        Page<SupportStaffResponseDTO> results = supportStaffService.searchSupportStaff(supportAgentUuid, keyword, pageable);
        PageResponse<SupportStaffResponseDTO> pageResponse = PageResponse.from(results);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @PostMapping("/{staffUuid}/tickets/{ticketId}/assign")
    @Operation(summary = "Assign ticket to support staff (Support Agent only)")
    public ResponseEntity<ApiResponse<Void>> assignTicket(
            @PathVariable String staffUuid,
            @PathVariable String ticketId) {
        log.info("POST /api/support-agents/staff/{}/tickets/{}/assign", staffUuid, ticketId);

        supportStaffService.assignTicket(staffUuid, ticketId);
        return ResponseEntity.ok(ApiResponse.success("Ticket assigned successfully"));
    }

    @PostMapping("/{staffUuid}/tickets/{ticketId}/resolve")
    @Operation(summary = "Resolve ticket from support staff (Support Agent only)")
    public ResponseEntity<ApiResponse<Void>> resolveTicket(
            @PathVariable String staffUuid,
            @PathVariable String ticketId) {
        log.info("POST /api/support-agents/staff/{}/tickets/{}/resolve", staffUuid, ticketId);

        supportStaffService.resolveTicket(staffUuid, ticketId);
        return ResponseEntity.ok(ApiResponse.success("Ticket resolved successfully"));
    }

    @GetMapping("/statistics/active-count")
    @Operation(summary = "Get active support staff count (Support Agent only)")
    public ResponseEntity<ApiResponse<Long>> getActiveSupportStaffCount() {
        log.info("GET /api/support-agents/staff/statistics/active-count");

        String supportAgentUuid = currentUserService.getCurrentSupportAgent().getUuid();
        long count = supportStaffService.getActiveSupportStaffCount(supportAgentUuid);

        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update my profile (Support Staff only)")
    public ResponseEntity<ApiResponse<SupportStaffResponseDTO>> modifierSupportStaff(
            @RequestBody @Valid SupportStaffRequestDTO requestDTO) {
        String supportStaffUuid = currentUserService.getCurrentSupportStaff().getUuid();
        log.info("PUT /api/support-agents/staff/profile - Support staff: {}", supportStaffUuid);

        SupportStaffResponseDTO response = supportStaffService.modifierSupportStaff(supportStaffUuid, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Support staff updated successfully"));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get my profile (Support Staff only)")
    public ResponseEntity<ApiResponse<SupportStaffResponseDTO>> getMyProfile() {
        String supportStaffUuid = currentUserService.getCurrentSupportStaff().getUuid();
        log.info("GET /api/support-agents/staff/profile - Support staff: {}", supportStaffUuid);

        SupportStaffResponseDTO response = supportStaffService.getMyProfile(supportStaffUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change my password (Support Staff only)")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(
            @RequestBody @Valid ChangePasswordRequestDTO passwordRequest) {
        String supportStaffUuid = currentUserService.getCurrentSupportStaff().getUuid();
        log.info("POST /api/support-agents/staff/change-password - Support staff: {}", supportStaffUuid);

        supportStaffService.changeMyPassword(supportStaffUuid, passwordRequest);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
}