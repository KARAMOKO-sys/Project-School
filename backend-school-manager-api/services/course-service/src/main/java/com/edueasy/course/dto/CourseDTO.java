package com.edueasy.course.dto;

import com.edueasy.common.enums.LevelStudent;
import com.edueasy.course.enums.CourseStatus;
import com.edueasy.course.enums.TypeCourses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String uuid;
    private String title;
    private String description;
    private TypeCourses typeCourses;
    private String thumbnailUrl;
    private CourseStatus status;
    private LevelStudent levelStudent;
    private String teacherUuid;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer durationHours;
    private Integer credits;
    private List<ContentDTO> contents;
}
