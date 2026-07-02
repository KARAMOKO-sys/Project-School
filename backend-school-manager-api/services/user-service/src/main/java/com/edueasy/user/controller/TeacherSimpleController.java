package com.edueasy.user.controller;

import com.edueasy.common.dto.ApiResponse;
import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.PageResponse;
import com.edueasy.common.dto.TeacherAuthResponseDTO;
import com.edueasy.common.dto.TeacherRequestDTO;
import com.edueasy.common.dto.TeacherResponseDTO;
import com.edueasy.common.dto.TeacherSimpleRegisterDTO;
import com.edueasy.common.dto.TeacherSimpleRequestDTO;
import com.edueasy.common.dto.UserStatusUpdateDTO;
import com.edueasy.user.service.TeacherSimpleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/teachers-simple")
@Tag(name = "Teacher Management", description = "APIs for managing teachers")
public class TeacherSimpleController {

    private static final Logger log = LoggerFactory.getLogger(TeacherSimpleController.class);

    private final TeacherSimpleService teacherService;

    public TeacherSimpleController(TeacherSimpleService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> registerTeacher(
            @RequestBody @Valid TeacherRequestDTO requestDTO) {
        log.info("POST /api/teachers-simple/register - Registering new teacher");

        TeacherResponseDTO response = teacherService.registerTeacher(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Teacher registered successfully"));
    }

    @PostMapping("/register-simple")
    @Operation(summary = "Register a simple teacher with minimal info")
    public ResponseEntity<ApiResponse<TeacherAuthResponseDTO>> registerTeacherSimple(
            @RequestBody @Valid TeacherSimpleRegisterDTO registerDTO) {
        log.info("POST /api/teachers-simple/register-simple - Registering simple teacher");

        TeacherAuthResponseDTO response = teacherService.registerTeacherSimple(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Simple teacher registered successfully"));
    }


    @GetMapping("/{teacherUuid}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getTeacherByUuid(
            @PathVariable String teacherUuid) {
        log.info("GET /api/teachers-simple/{} - Fetching teacher by uuid", teacherUuid);

        TeacherResponseDTO response = teacherService.getTeacherByUuid(teacherUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }



    @GetMapping("/by-email/{email}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getTeacherByEmail(
            @PathVariable String email) {
        log.info("GET /api/teachers-simple/by-email/{} - Fetching teacher by email", email);

        TeacherResponseDTO response = teacherService.getTeacherByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /*
    @GetMapping("/by-number/{teacherNumber}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getTeacherByNumber(
            @PathVariable String teacherNumber) {
        log.info("GET /api/teachers-simple/by-number/{} - Fetching teacher by number", teacherNumber);

        TeacherResponseDTO response = teacherService.getTeacherByTeacherNumber(teacherNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

     */

    @PutMapping("/profile/{teacherUuid}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> updateProfilTeacherSimple(
            @PathVariable String teacherUuid,
            @RequestBody @Valid TeacherSimpleRequestDTO requestDTO) {
        log.info("PUT /api/teachers-simple/profile/{} - Updating teacher profile", teacherUuid);

        TeacherResponseDTO response = teacherService.updateProfilTeacherSimple(teacherUuid, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Teacher profile updated successfully"));
    }

    @PutMapping("/{teacherUuid}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> updateTeacherSimple(
            @PathVariable String teacherUuid,
            @RequestBody @Valid TeacherSimpleRegisterDTO requestDTO) {
        log.info("PUT /api/teachers-simple/{} - Updating teacher", teacherUuid);

        TeacherResponseDTO response = teacherService.updateTeacherSimple(teacherUuid, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Teacher updated successfully"));
    }

    @DeleteMapping("/{teacherUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(
            @PathVariable String teacherUuid) {
        log.info("DELETE /api/teachers-simple/{} - Soft deleting teacher", teacherUuid);

        teacherService.deleteTeacher(teacherUuid);
        return ResponseEntity.ok(ApiResponse.success("Teacher deleted successfully"));
    }

    @DeleteMapping("/{teacherUuid}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentDeleteTeacher(
            @PathVariable String teacherUuid) {
        log.warn("DELETE /api/teachers-simple/{}/permanent - Permanently deleting teacher", teacherUuid);

        teacherService.permanentDeleteTeacher(teacherUuid);
        return ResponseEntity.ok(ApiResponse.success("Teacher permanently deleted"));
    }

    @PatchMapping("/{teacherUuid}/status")
    public ResponseEntity<ApiResponse<Void>> updateTeacherStatus(
            @PathVariable String teacherUuid,
            @RequestBody @Valid UserStatusUpdateDTO statusUpdate) {
        log.info("PATCH /api/teachers-simple/{}/status - Updating teacher status", teacherUuid);

        teacherService.updateTeacherStatus(teacherUuid, statusUpdate);
        return ResponseEntity.ok(ApiResponse.success("Teacher status updated successfully"));
    }

    /*
    @PatchMapping("/{teacherUuid}/status/{status}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> updateTeacherStatusDirect(
            @PathVariable String teacherUuid,
            @PathVariable com.edueasy.common.enums.UserStatus status) {
        log.info("PATCH /api/teachers-simple/{}/status/{} - Updating teacher status", teacherUuid, status);

        TeacherResponseDTO response = teacherService.updateTeacherStatus(teacherUuid, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Teacher status updated successfully"));
    }

     */

    @PostMapping("/{teacherUuid}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String teacherUuid,
            @RequestBody @Valid ChangePasswordRequestDTO passwordRequest) {
        log.info("POST /api/teachers-simple/{}/change-password - Changing teacher password", teacherUuid);

        teacherService.changePassword(teacherUuid, passwordRequest);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TeacherResponseDTO>>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("GET /api/teachers-simple - Fetching all teachers with pagination");

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<TeacherResponseDTO> teacherPage = teacherService.getAllTeachers(pageable);
        PageResponse<TeacherResponseDTO> pageResponse = PageResponse.from(teacherPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<TeacherResponseDTO>>> searchTeachers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/teachers-simple/search - Searching teachers with keyword: {}", keyword);

        Pageable pageable = PageRequest.of(page, size);
        Page<TeacherResponseDTO> teacherPage = teacherService.searchTeachers(keyword, pageable);
        PageResponse<TeacherResponseDTO> pageResponse = PageResponse.from(teacherPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /*
    @GetMapping("/by-status")
    public ResponseEntity<ApiResponse<PageResponse<TeacherResponseDTO>>> getTeachersByStatus(
            @RequestParam com.edueasy.common.enums.UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/teachers-simple/by-status - Fetching teachers by status: {}", status);

        Pageable pageable = PageRequest.of(page, size);
        Page<TeacherResponseDTO> teacherPage = teacherService.getTeachersByStatus(status, pageable);
        PageResponse<TeacherResponseDTO> pageResponse = PageResponse.from(teacherPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

     */

    @GetMapping("/statistics/active-count")
    public ResponseEntity<ApiResponse<Long>> getActiveTeachersCount() {
        log.info("GET /api/teachers-simple/statistics/active-count - Getting active teachers count");

        long count = teacherService.getActiveTeachersCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /*
    @GetMapping("/statistics/count")
    public ResponseEntity<ApiResponse<Long>> getTeachersCount() {
        log.info("GET /api/teachers-simple/statistics/count - Getting total teachers count");

        long count = teacherService.getTeachersCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

     */

    @PostMapping("/{teacherUuid}/update-login")
    public ResponseEntity<ApiResponse<Void>> updateLastLogin(
            @PathVariable String teacherUuid) {
        log.info("POST /api/teachers-simple/{}/update-login - Updating last login", teacherUuid);

        teacherService.updateLastLogin(teacherUuid);
        return ResponseEntity.ok(ApiResponse.success("Last login updated"));
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
            @PathVariable String email) {
        log.info("GET /api/teachers-simple/exists/{} - Checking if email exists", email);

        boolean exists = teacherService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    /*
    @GetMapping("/exists-number/{teacherNumber}")
    public ResponseEntity<ApiResponse<Boolean>> checkTeacherNumberExists(
            @PathVariable String teacherNumber) {
        log.info("GET /api/teachers-simple/exists-number/{} - Checking if teacher number exists", teacherNumber);

        boolean exists = teacherService.existsByTeacherNumber(teacherNumber);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

     */
}
