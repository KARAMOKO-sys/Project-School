package com.edueasy.course.controller;

import com.edueasy.common.dto.ApiResponse;
import com.edueasy.course.dto.CourseDTO;
import com.edueasy.course.service.CourseService;
import com.edueasy.course.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@Tag(name = "Course Management", description = "APIs for managing courses")
@SecurityRequirement(name = "Bearer Authentication")
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;
    private final CurrentUserService currentUserService;

    public CourseController(CourseService courseService, CurrentUserService currentUserService) {
        this.courseService = courseService;
        this.currentUserService = currentUserService;
    }
    @PostMapping
    @Operation(summary = "Create a new course", description = "Create a new course. Teacher must be authenticated.")
    public ResponseEntity<ApiResponse<CourseDTO>> createCourse(@RequestBody @Valid CourseDTO courseDTO) {
        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        courseDTO.setTeacherUuid(teacherUuid);
        log.info("POST /api/courses - Creating new course for teacher: {}", teacherUuid);

        CourseDTO createdCourse = courseService.createCourse(teacherUuid, courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCourse, "Course created successfully"));
    }


    @GetMapping("/{courseUuid}")
    @Operation(summary = "Get course by UUID", description = "Get detailed information about a specific course")
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseByUuid(@PathVariable String courseUuid) {
        log.info("GET /api/courses/{} - Fetching course", courseUuid);
        CourseDTO course = courseService.getCourseByUuid(courseUuid);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @GetMapping("/teacher/{teacherUuid}")
    @Operation(summary = "Get courses by teacher", description = "Get all courses created by a specific teacher")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByTeacher(@PathVariable String teacherUuid) {
        log.info("GET /api/courses/teacher/{} - Fetching courses", teacherUuid);
        List<CourseDTO> courses = courseService.getCoursesByTeacher(teacherUuid);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @PutMapping("/{courseUuid}")
    @Operation(summary = "Update course", description = "Update an existing course. Only the course owner can update.")
    public ResponseEntity<ApiResponse<CourseDTO>> updateCourse(@PathVariable String courseUuid,
                                                               @RequestBody @Valid CourseDTO courseDTO) {
        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }
        log.info("PUT /api/courses/{} - Updating course for teacher: {}", courseUuid, teacherUuid);
        CourseDTO updatedCourse = courseService.updateCourse(teacherUuid, courseUuid, courseDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedCourse, "Course updated successfully"));
    }

    @PostMapping("/{courseUuid}/publish")
    @Operation(summary = "Publish course", description = "Publish a course to make it visible to students")
    public ResponseEntity<ApiResponse<CourseDTO>> publishCourse(@PathVariable String courseUuid) {
        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }
        log.info("POST /api/courses/{}/publish - Publishing course by teacher: {}", courseUuid, teacherUuid);
        CourseDTO publishedCourse = courseService.publishCourse(teacherUuid, courseUuid);
        return ResponseEntity.ok(ApiResponse.success(publishedCourse, "Course published successfully"));
    }

    @DeleteMapping("/{courseUuid}")
    @Operation(summary = "Delete course", description = "Soft delete a course. Only the course owner can delete.")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable String courseUuid) {
        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }
        log.info("DELETE /api/courses/{} - Deleting course for teacher: {}", courseUuid, teacherUuid);
        courseService.deleteCourse(teacherUuid, courseUuid);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully"));
    }

    @GetMapping("/student/{studentUuid}/courses")
    @Operation(summary = "Get courses by student level")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByStudentLevel(@PathVariable String studentUuid) {
        log.info("GET /api/courses/student/{}/courses", studentUuid);
        List<CourseDTO> courses = courseService.getCoursesByStudentLevel(studentUuid);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }
}