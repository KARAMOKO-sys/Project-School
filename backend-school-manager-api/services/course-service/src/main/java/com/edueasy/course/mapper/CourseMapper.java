package com.edueasy.course.mapper;

import com.edueasy.common.model.AuditTimestamps;
import com.edueasy.course.dto.ContentDTO;
import com.edueasy.course.dto.CourseDTO;
import com.edueasy.course.enums.CourseStatus;
import com.edueasy.course.model.Content;
import com.edueasy.course.model.Course;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    public CourseMapper() {
    }

    public CourseDTO toCourseDTO(Course course) {
        if (course == null) {
            return null;
        }

        CourseDTO.CourseDTOBuilder builder = CourseDTO.builder()
                .uuid(course.getUuid())  // 🔥 AJOUTER L'UUID
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .typeCourses(course.getTypeCourses())
                .status(course.getStatus())
                .teacherUuid(course.getTeacherUuid())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .durationHours(course.getDurationHours())
                .credits(course.getCredits())
                .levelStudent(course.getLevelStudent());

        // Mapper les contenus
        if (course.getContents() != null && !course.getContents().isEmpty()) {
            List<ContentDTO> contentDTOs = course.getContents().stream()
                    .map(this::toContentDTO)
                    .collect(Collectors.toList());
            builder.contents(contentDTOs);
        }

        return builder.build();
    }

    public ContentDTO toContentDTO(Content content) {
        if (content == null) {
            return null;
        }
        return ContentDTO.builder()
                .uuid(content.getUuid())
                .title(content.getTitle())
                .description(content.getDescription())
                .contentType(content.getType())
                .contentUrl(content.getContentUrl())
                .orderIndex(content.getOrderIndex())
                .durationMinutes(content.getDurationMinutes())
                .isRequired(content.getIsRequired())
                .build();
    }

    public Course toCourse(CourseDTO courseDTO) {
        if (courseDTO == null) {
            return null;
        }

        Course course = new Course();
        if (courseDTO.getUuid() != null && !courseDTO.getUuid().isEmpty()) {
            course.setUuid(courseDTO.getUuid());
        } else {
            course.setUuid(UUID.randomUUID().toString());
        }

        course.setTeacherUuid(courseDTO.getTeacherUuid());
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setThumbnailUrl(courseDTO.getThumbnailUrl());
        course.setTypeCourses(courseDTO.getTypeCourses());
        course.setStatus(courseDTO.getStatus() != null ? courseDTO.getStatus() : CourseStatus.DRAFT);
        course.setStartDate(courseDTO.getStartDate());
        course.setEndDate(courseDTO.getEndDate());
        course.setDurationHours(courseDTO.getDurationHours());
        course.setCredits(courseDTO.getCredits());
        course.setLevelStudent(courseDTO.getLevelStudent());

        if (course.getTimestamps() == null) {
            course.setTimestamps(new AuditTimestamps());
        }
        course.getTimestamps().setCreatedAt(LocalDateTime.now());
        course.getTimestamps().setUpdatedAt(LocalDateTime.now());

        return course;
    }
}