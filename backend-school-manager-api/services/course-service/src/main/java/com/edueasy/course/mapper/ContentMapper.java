package com.edueasy.course.mapper;

import com.edueasy.course.dto.ContentDTO;
import com.edueasy.course.model.Content;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {
    public ContentMapper() {
    }

    public ContentDTO toContentDto(Content content) {
        return content == null ? null : ContentDTO.builder().uuid(content.getUuid()).title(content.getTitle()).description(content.getDescription()).contentUrl(content.getContentUrl()).contentType(content.getType()).build();
    }

    public Content toContent(ContentDTO contentDTO) {
        if (contentDTO == null) {
            return null;
        } else {
            Content content = new Content();
            content.setUuid(contentDTO.getUuid());
            content.setTitle(contentDTO.getTitle());
            content.setDescription(contentDTO.getDescription());
            content.setContentUrl(contentDTO.getContentUrl());
            content.setType(contentDTO.getContentType());
            return content;
        }
    }
}