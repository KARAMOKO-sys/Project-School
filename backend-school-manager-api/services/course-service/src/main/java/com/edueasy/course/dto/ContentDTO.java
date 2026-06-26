package com.edueasy.course.dto;


import com.edueasy.course.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {
    private String uuid;
    private String title;
    private String description;
    private ContentType contentType;
    private String contentUrl;
    private Integer orderIndex;
    private Integer durationMinutes;
    private Boolean isRequired;
}
