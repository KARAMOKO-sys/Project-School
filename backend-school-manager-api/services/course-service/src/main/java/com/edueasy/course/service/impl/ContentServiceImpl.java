package com.edueasy.course.service.impl;

import com.edueasy.common.exception.ResourceNotFoundException;
import com.edueasy.common.exception.UnauthorizedAccessException;
import com.edueasy.common.model.AuditTimestamps;
import com.edueasy.course.dto.ContentDTO;
import com.edueasy.course.mapper.ContentMapper;
import com.edueasy.course.model.Content;
import com.edueasy.course.model.Course;
import com.edueasy.course.repository.ContentRepository;
import com.edueasy.course.repository.CourseRepository;
import com.edueasy.course.service.ContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService {

    private static final Logger log = LoggerFactory.getLogger(ContentServiceImpl.class);
    private final ContentRepository contentRepository;
    private final CourseRepository courseRepository;
    private final ContentMapper contentMapper;

    public ContentServiceImpl(ContentRepository contentRepository,
                              CourseRepository courseRepository,
                              ContentMapper contentMapper) {
        this.contentRepository = contentRepository;
        this.courseRepository = courseRepository;
        this.contentMapper = contentMapper;
    }

    @Override
    @Transactional
    public ContentDTO addContentToCourse(String teacherUuid, String courseUuid, ContentDTO contentDTO) {
        log.info("Adding content to course: {} for teacher: {}", courseUuid, teacherUuid);

        Course course = courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to add content to this course");
        }

        String contentUuid = UUID.randomUUID().toString();
        Content content = contentMapper.toContent(contentDTO);
        content.setUuid(contentUuid);
        content.setCourse(course);
        content.setTeacherUuid(teacherUuid);
        content.setCourseUuid(courseUuid);

        AuditTimestamps timestamps = new AuditTimestamps();
        //timestamps.init();
        content.setTimestamps(timestamps);

        long currentContentCount = contentRepository.countByCourseUuid(courseUuid);
        content.setOrderIndex((int) currentContentCount + 1);

        content = contentRepository.save(content);
        log.info("Content added successfully with uuid: {}", content.getUuid());

        return contentMapper.toContentDto(content);
    }

    @Override
    public List<ContentDTO> getContentsByCourse(String teacherUuid, String courseUuid) {
        log.info("Fetching contents for course: {}", courseUuid);

        Course course = courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to view contents of this course");
        }

        return contentRepository.findByCourseUuidOrderByOrderIndexAsc(courseUuid)
                .stream()
                .map(contentMapper::toContentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContentDTO updateContent(String teacherUuid, String contentUuid, ContentDTO contentDTO) {
        log.info("Updating content: {} for teacher: {}", contentUuid, teacherUuid);

        Content content = contentRepository.findByUuid(contentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "uuid", contentUuid));

        if (!content.getCourse().getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to update this content");
        }

        content.setTitle(contentDTO.getTitle());
        content.setDescription(contentDTO.getDescription());
        content.setType(contentDTO.getContentType());
        content.setContentUrl(contentDTO.getContentUrl());
        content.setDurationMinutes(contentDTO.getDurationMinutes());
        content.setIsRequired(contentDTO.getIsRequired() != null ? contentDTO.getIsRequired() : true);
     //   content.setIsPublished(contentDTO.getIsPublished() != null ? contentDTO.getIsPublished() : false);
      //  content.setIsFree(contentDTO.getIsFree() != null ? contentDTO.getIsFree() : false);

        if (content.getTimestamps() != null) {
            content.getTimestamps().setUpdatedAt(java.time.LocalDateTime.now());
        }

        content = contentRepository.save(content);
        log.info("Content updated successfully: {}", contentUuid);

        return contentMapper.toContentDto(content);
    }

    @Override
    @Transactional
    public void deleteContent(String teacherUuid, String contentUuid) {
        log.info("Deleting content: {} for teacher: {}", contentUuid, teacherUuid);

        Content content = contentRepository.findByUuid(contentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "uuid", contentUuid));

        if (!content.getCourse().getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this content");
        }

        contentRepository.delete(content);
        log.info("Content deleted successfully: {}", contentUuid);
    }

    @Override
    @Transactional
    public void delete(String teacherUuid, String contentUuid) {
        log.info("Soft deleting content: {} for teacher: {}", contentUuid, teacherUuid);

        Content content = contentRepository.findByUuid(contentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "uuid", contentUuid));

        if (!content.getCourse().getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this content");
        }

        if (content.getTimestamps() != null) {
            content.getTimestamps().markAsDeleted();
            content.getTimestamps().setUpdatedAt(java.time.LocalDateTime.now());
        }
        contentRepository.save(content);
        log.info("Content soft deleted successfully: {}", contentUuid);
    }

    @Override
    public ContentDTO getContentByUuid(String contentUuid) {
        log.info("Fetching content by uuid: {}", contentUuid);
        Content content = contentRepository.findByUuid(contentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "uuid", contentUuid));
        return contentMapper.toContentDto(content);
    }

    @Override
    public List<ContentDTO> getPublishedContents(String teacherUuid, String courseUuid) {
        log.info("Fetching published contents for course: {}", courseUuid);

        Course course = courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to view contents of this course");
        }

        return contentRepository.findByCourseUuidAndIsPublishedTrueOrderByOrderIndexAsc(courseUuid)
                .stream()
                .map(contentMapper::toContentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContentDTO duplicateContent(String teacherUuid, String contentUuid) {
        log.info("Duplicating content: {} for teacher: {}", contentUuid, teacherUuid);

        Content original = contentRepository.findByUuid(contentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "uuid", contentUuid));

        if (!original.getCourse().getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to duplicate this content");
        }

        Content copy = new Content();
        copy.setUuid(UUID.randomUUID().toString());
        copy.setTitle("Copie de " + original.getTitle());
        copy.setDescription(original.getDescription());
        copy.setType(original.getType());
        copy.setContentUrl(original.getContentUrl());
        copy.setDurationMinutes(original.getDurationMinutes());
        copy.setIsRequired(original.isRequired());
        copy.setIsFree(original.isFree());
        copy.setIsPublished(false);
        copy.setCourse(original.getCourse());
        copy.setCourseUuid(original.getCourseUuid());
        copy.setTeacherUuid(teacherUuid);

        AuditTimestamps timestamps = new AuditTimestamps();
    //    timestamps.init();
        copy.setTimestamps(timestamps);

        long currentContentCount = contentRepository.countByCourseUuid(original.getCourseUuid());
        copy.setOrderIndex((int) currentContentCount + 1);

        copy = contentRepository.save(copy);
        log.info("Content duplicated successfully: {}", copy.getUuid());

        return contentMapper.toContentDto(copy);
    }

    @Override
    @Transactional
    public void archiveContent(String teacherUuid, String contentUuid) {
        log.info("Archiving content: {} for teacher: {}", contentUuid, teacherUuid);

        Content content = contentRepository.findByUuid(contentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "uuid", contentUuid));

        if (!content.getCourse().getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to archive this content");
        }

        content.setIsPublished(false);
        contentRepository.save(content);
        log.info("Content archived successfully: {}", contentUuid);
    }

    @Override
    @Transactional
    public void unarchiveContent(String teacherUuid, String contentUuid) {
        log.info("Unarchiving content: {} for teacher: {}", contentUuid, teacherUuid);

        Content content = contentRepository.findByUuid(contentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "uuid", contentUuid));

        if (!content.getCourse().getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to unarchive this content");
        }

        content.setIsPublished(true);
        contentRepository.save(content);
        log.info("Content unarchived successfully: {}", contentUuid);
    }

    @Override
    @Transactional
    public void reorderContents(String teacherUuid, String courseUuid, List<String> contentUuidsInOrder) {
        log.info("Reordering contents for course: {}", courseUuid);

        Course course = courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to reorder contents of this course");
        }

        for (int i = 0; i < contentUuidsInOrder.size(); i++) {
            contentRepository.updateOrderIndex(contentUuidsInOrder.get(i), i + 1);
        }

        log.info("Contents reordered successfully for course: {}", courseUuid);
    }

    @Override
    @Transactional
    public List<ContentDTO> bulkAddContents(String teacherUuid, String courseUuid, List<ContentDTO> contentDTOs) {
        log.info("Bulk adding {} contents to course: {}", contentDTOs.size(), courseUuid);

        List<ContentDTO> createdContents = contentDTOs.stream()
                .map(contentDTO -> addContentToCourse(teacherUuid, courseUuid, contentDTO))
                .collect(Collectors.toList());

        log.info("Successfully added {} contents", createdContents.size());
        return createdContents;
    }
}