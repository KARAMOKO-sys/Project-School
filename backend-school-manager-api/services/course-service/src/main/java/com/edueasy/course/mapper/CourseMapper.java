package com.edueasy.course.mapper;

import com.edueasy.common.model.AuditTimestamps;
import com.edueasy.course.dto.CourseDTO;
import com.edueasy.course.enums.CourseStatus;
import com.edueasy.course.model.Course;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    public CourseMapper() {
    }

    public CourseDTO toCourseDTO(Course course) {
        if (course == null) {
            return null;
        } else {
            CourseDTO.CourseDTOBuilder builder = CourseDTO.builder().title(course.getTitle()).description(course.getDescription()).thumbnailUrl(course.getThumbnailUrl()).typeCourses(course.getTypeCourses()).status(course.getStatus()).teacherUuid(course.getTeacherUuid()).startDate(course.getStartDate()).endDate(course.getEndDate()).durationHours(course.getDurationHours()).credits(course.getCredits());
            if (course.getContents() != null && !course.getContents().isEmpty()) {
                builder.contents(List.of());
            }

            return builder.build();
        }
    }

    public Course toCourse(CourseDTO courseDTO) {
        if (courseDTO == null) {
            return null;
        } else {
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
            if (course.getTimestamps() == null) {
                course.setTimestamps(new AuditTimestamps());
            }

            course.getTimestamps().setCreatedAt(LocalDateTime.now());
            course.getTimestamps().setUpdatedAt(LocalDateTime.now());
            return course;
        }
    }
}
