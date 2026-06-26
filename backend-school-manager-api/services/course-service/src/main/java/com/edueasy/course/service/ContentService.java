package com.edueasy.course.service;

import com.edueasy.course.dto.ContentDTO;
import java.util.List;

public interface ContentService {
    ContentDTO addContentToCourse(String teacherUuid, String courseUuid, ContentDTO contentDTO);

    List<ContentDTO> getContentsByCourse(String teacherUuid, String courseUuid);

    ContentDTO updateContent(String teacherUuid, String contentUuid, ContentDTO contentDTO);

    void deleteContent(String teacherUuid, String contentUuid);

    void reorderContents(String teacherUuid, String courseUuid, List<String> contentUuidsInOrder);

    List<ContentDTO> bulkAddContents(String teacherUuid, String courseUuid, List<ContentDTO> contentDTOs);

    void delete(String teacherUuid, String contentUuid);

    ContentDTO getContentByUuid(String contentUuid);

    List<ContentDTO> getPublishedContents(String teacherUuid, String courseUuid);

    ContentDTO duplicateContent(String teacherUuid, String contentUuid);

    void archiveContent(String teacherUuid, String contentUuid);

    void unarchiveContent(String teacherUuid, String contentUuid);
}
