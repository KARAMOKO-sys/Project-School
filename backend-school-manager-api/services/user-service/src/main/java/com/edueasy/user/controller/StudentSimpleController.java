package com.edueasy.user.controller;

import com.edueasy.common.dto.ApiResponse;
import com.edueasy.common.dto.ChangePasswordRequestDTO;
import com.edueasy.common.dto.PageResponse;
import com.edueasy.common.dto.StudentRequestDTO;
import com.edueasy.common.dto.StudentResponseDTO;
import com.edueasy.common.dto.StudentSimpleRegisterDTO;
import com.edueasy.common.dto.UserStatusUpdateDTO;
import com.edueasy.user.service.StudentSimpleService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/students")
public class StudentSimpleController {

    private static final Logger log = LoggerFactory.getLogger(StudentSimpleController.class);

    private final StudentSimpleService studentService;

    public StudentSimpleController(StudentSimpleService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register-simple")
    @Operation(summary = "Register a simple student with minimal info")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> registerStudentSimple(
            @RequestBody @Valid StudentSimpleRegisterDTO registerDTO) {
        log.info("POST /api/students/register-simple - Registering simple student: {} {}",
                registerDTO.getFirstName(), registerDTO.getLastName());

        StudentResponseDTO response = studentService.registerStudentSimple(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Simple student registered successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> registerStudent(
            @RequestBody @Valid StudentRequestDTO requestDTO) {
        log.info("POST /api/students/register - Registering new student");

        StudentResponseDTO response = studentService.registerStudent(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Student registered successfully"));
    }

    @GetMapping("/{studentUuid}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentByUuid(
            @PathVariable String studentUuid) {
        log.info("GET /api/students/{} - Fetching student by uuid", studentUuid);

        StudentResponseDTO response = studentService.getStudentByUuid(studentUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentByEmail(
            @PathVariable String email) {
        log.info("GET /api/students/by-email/{} - Fetching student by email", email);

        StudentResponseDTO response = studentService.getStudentByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{studentUuid}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable String studentUuid,
            @RequestBody @Valid StudentRequestDTO requestDTO) {
        log.info("PUT /api/students/{} - Updating student", studentUuid);

        StudentResponseDTO response = studentService.updateStudent(studentUuid, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Student updated successfully"));
    }

    @PutMapping("/profile/{studentUuid}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateProfilStudentSimple(
            @PathVariable String studentUuid,
            @RequestBody @Valid StudentRequestDTO requestDTO) {
        log.info("PUT /api/students/profile/{} - Updating student profile", studentUuid);

        StudentResponseDTO response = studentService.updateProfilStudentSimple(studentUuid, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Student profile updated successfully"));
    }

    @DeleteMapping("/{studentUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable String studentUuid) {
        log.info("DELETE /api/students/{} - Soft deleting student", studentUuid);

        studentService.deleteStudent(studentUuid);
        return ResponseEntity.ok(ApiResponse.success(null, "Student deleted successfully"));
    }

    @DeleteMapping("/{studentUuid}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentDeleteStudent(
            @PathVariable String studentUuid) {
        log.warn("DELETE /api/students/{}/permanent - Permanently deleting student", studentUuid);

        studentService.permanentDeleteStudent(studentUuid);
        return ResponseEntity.ok(ApiResponse.success(null, "Student permanently deleted"));
    }

    @PatchMapping("/{studentUuid}/status")
    public ResponseEntity<ApiResponse<Void>> updateStudentStatus(
            @PathVariable String studentUuid,
            @RequestBody @Valid UserStatusUpdateDTO statusUpdate) {
        log.info("PATCH /api/students/{}/status - Updating student status", studentUuid);

        studentService.updateStudentStatus(studentUuid, statusUpdate);
        return ResponseEntity.ok(ApiResponse.success(null, "Student status updated successfully"));
    }

    @PostMapping("/{studentUuid}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String studentUuid,
            @RequestBody @Valid ChangePasswordRequestDTO passwordRequest) {
        log.info("POST /api/students/{}/change-password - Changing student password", studentUuid);

        studentService.changePassword(studentUuid, passwordRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StudentResponseDTO>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("GET /api/students - Fetching all students with pagination");

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<StudentResponseDTO> studentPage = studentService.getAllStudents(pageable);
        PageResponse<StudentResponseDTO> pageResponse = PageResponse.from(studentPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponseDTO>>> searchStudents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/students/search - Searching students with keyword: {}", keyword);

        Pageable pageable = PageRequest.of(page, size);
        Page<StudentResponseDTO> studentPage = studentService.searchStudents(keyword, pageable);
        PageResponse<StudentResponseDTO> pageResponse = PageResponse.from(studentPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/statistics/active-count")
    public ResponseEntity<ApiResponse<Long>> getActiveStudentsCount() {
        log.info("GET /api/students/statistics/active-count - Getting active students count");

        long count = studentService.getActiveStudentsCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/statistics/today-registrations")
    public ResponseEntity<ApiResponse<Long>> getTodayRegistrationsCount() {
        log.info("GET /api/students/statistics/today-registrations - Getting today's registrations");

        long count = studentService.getTodayRegistrationsCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PostMapping("/{studentId}/update-login")
    public ResponseEntity<ApiResponse<Void>> updateLastLogin(
            @PathVariable String studentId) {
        log.info("POST /api/students/{}/update-login - Updating last login", studentId);

        studentService.updateLastLogin(studentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Last login updated"));
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
            @PathVariable String email) {
        log.info("GET /api/students/exists/{} - Checking if email exists", email);

        boolean exists = studentService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/by-teacher/{teacherUuid}")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponseDTO>>> getStudentsByTeacher(
            @PathVariable String teacherUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/students/by-teacher/{} - Fetching students by teacher", teacherUuid);

        Pageable pageable = PageRequest.of(page, size);
        // TODO: Implémenter la méthode dans le service si nécessaire
        Page<StudentResponseDTO> studentPage = Page.empty(pageable);
        PageResponse<StudentResponseDTO> pageResponse = PageResponse.from(studentPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }
}