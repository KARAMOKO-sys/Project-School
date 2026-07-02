package com.edueasy.course.controller;

import com.edueasy.common.dto.ApiResponse;
import com.edueasy.course.dto.ContentDTO;
import com.edueasy.course.service.ContentService;
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
@RequestMapping("/api/courses/{courseUuid}/contents")
@Tag(name = "Content Management", description = "APIs for managing course contents")
@SecurityRequirement(name = "Bearer Authentication")
public class ContentController {

    private static final Logger log = LoggerFactory.getLogger(ContentController.class);

    private final ContentService contentService;
    private final CurrentUserService currentUserService;  // 🔥 Injection du service

    public ContentController(ContentService contentService, CurrentUserService currentUserService) {
        this.contentService = contentService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "Add content to course", description = "Add a new content to an existing course")
    public ResponseEntity<ApiResponse<ContentDTO>> addContentToCourse(
            @PathVariable String courseUuid,
            @RequestBody @Valid ContentDTO contentDTO) {

        // 🔥 Utiliser le CurrentUserService
        String teacherUuid = currentUserService.getCurrentUserUuid();
        log.info("Current user UUID from service: {}", teacherUuid);

        if (teacherUuid == null) {
            log.warn("User not authenticated - teacherUuid is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("POST /api/courses/{}/contents - Adding content to course by teacher: {}", courseUuid, teacherUuid);
        ContentDTO createdContent = contentService.addContentToCourse(teacherUuid, courseUuid, contentDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdContent, "Content added successfully"));
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk add contents", description = "Add multiple contents to a course at once")
    public ResponseEntity<ApiResponse<List<ContentDTO>>> bulkAddContents(
            @PathVariable String courseUuid,
            @RequestBody @Valid List<ContentDTO> contentDTOs) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("POST /api/courses/{}/contents/bulk - Bulk adding {} contents", courseUuid, contentDTOs.size());
        List<ContentDTO> createdContents = contentService.bulkAddContents(teacherUuid, courseUuid, contentDTOs);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdContents, "Contents added successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all contents", description = "Get all contents of a course in order")
    public ResponseEntity<ApiResponse<List<ContentDTO>>> getCourseContents(
            @PathVariable String courseUuid) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("GET /api/courses/{}/contents - Fetching all contents", courseUuid);
        List<ContentDTO> contents = contentService.getContentsByCourse(teacherUuid, courseUuid);
        return ResponseEntity.ok(ApiResponse.success(contents));
    }

    @PutMapping("/{contentUuid}")
    @Operation(summary = "Update content", description = "Update an existing content")
    public ResponseEntity<ApiResponse<ContentDTO>> updateContent(
            @PathVariable String courseUuid,
            @PathVariable String contentUuid,
            @RequestBody @Valid ContentDTO contentDTO) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("PUT /api/courses/{}/contents/{} - Updating content", courseUuid, contentUuid);
        ContentDTO updatedContent = contentService.updateContent(teacherUuid, contentUuid, contentDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedContent, "Content updated successfully"));
    }

    @PostMapping("/reorder")
    @Operation(summary = "Reorder contents", description = "Reorder all contents of a course")
    public ResponseEntity<ApiResponse<Void>> reorderContents(
            @PathVariable String courseUuid,
            @RequestBody List<String> contentUuidsInOrder) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("POST /api/courses/{}/contents/reorder - Reordering contents", courseUuid);
        contentService.reorderContents(teacherUuid, courseUuid, contentUuidsInOrder);
        return ResponseEntity.ok(ApiResponse.success("Contents reordered successfully"));
    }

    @DeleteMapping("/{contentUuid}")
    @Operation(summary = "Delete content", description = "Delete a content from a course")
    public ResponseEntity<ApiResponse<Void>> deleteContent(
            @PathVariable String courseUuid,
            @PathVariable String contentUuid) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("DELETE /api/courses/{}/contents/{} - Deleting content", courseUuid, contentUuid);
        contentService.deleteContent(teacherUuid, contentUuid);
        return ResponseEntity.ok(ApiResponse.success("Content deleted successfully"));
    }

    @DeleteMapping("/soft/{contentUuid}")
    @Operation(summary = "Soft delete content", description = "Soft delete a content from a course")
    public ResponseEntity<ApiResponse<Void>> softDeleteContent(
            @PathVariable String courseUuid,
            @PathVariable String contentUuid) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("DELETE /api/courses/{}/contents/soft/{} - Soft deleting content", courseUuid, contentUuid);
        contentService.delete(teacherUuid, contentUuid);
        return ResponseEntity.ok(ApiResponse.success("Content soft deleted successfully"));
    }

    @PostMapping("/{contentUuid}/duplicate")
    @Operation(summary = "Duplicate content", description = "Duplicate an existing content")
    public ResponseEntity<ApiResponse<ContentDTO>> duplicateContent(
            @PathVariable String courseUuid,
            @PathVariable String contentUuid) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("POST /api/courses/{}/contents/{}/duplicate - Duplicating content", courseUuid, contentUuid);
        ContentDTO duplicatedContent = contentService.duplicateContent(teacherUuid, contentUuid);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(duplicatedContent, "Content duplicated successfully"));
    }

    @PostMapping("/{contentUuid}/archive")
    @Operation(summary = "Archive content", description = "Archive a content")
    public ResponseEntity<ApiResponse<Void>> archiveContent(
            @PathVariable String courseUuid,
            @PathVariable String contentUuid) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("POST /api/courses/{}/contents/{}/archive - Archiving content", courseUuid, contentUuid);
        contentService.archiveContent(teacherUuid, contentUuid);
        return ResponseEntity.ok(ApiResponse.success("Content archived successfully"));
    }

    @PostMapping("/{contentUuid}/unarchive")
    @Operation(summary = "Unarchive content", description = "Unarchive a content")
    public ResponseEntity<ApiResponse<Void>> unarchiveContent(
            @PathVariable String courseUuid,
            @PathVariable String contentUuid) {

        String teacherUuid = currentUserService.getCurrentUserUuid();
        if (teacherUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        log.info("POST /api/courses/{}/contents/{}/unarchive - Unarchiving content", courseUuid, contentUuid);
        contentService.unarchiveContent(teacherUuid, contentUuid);
        return ResponseEntity.ok(ApiResponse.success("Content unarchived successfully"));
    }
}