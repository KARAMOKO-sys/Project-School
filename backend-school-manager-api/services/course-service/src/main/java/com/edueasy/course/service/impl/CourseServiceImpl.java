package com.edueasy.course.service.impl;

import com.edueasy.common.dto.StudentResponseDTO;
import com.edueasy.common.dto.TeacherResponseDTO;
import com.edueasy.common.enums.LevelStudent;
import com.edueasy.common.exception.ResourceNotFoundException;
import com.edueasy.common.exception.UnauthorizedAccessException;
import com.edueasy.course.client.UserServiceClient;
import com.edueasy.course.dto.ContentDTO;
import com.edueasy.course.dto.CourseDTO;
import com.edueasy.course.enums.CourseStatus;
import com.edueasy.course.mapper.ContentMapper;
import com.edueasy.course.mapper.CourseMapper;
import com.edueasy.course.model.Course;
import com.edueasy.course.repository.ContentRepository;
import com.edueasy.course.repository.CourseRepository;
import com.edueasy.course.service.CourseService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {
    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final CourseRepository courseRepository;
    private final ContentRepository contentRepository;
    private final UserServiceClient userServiceClient;
    private final CourseMapper courseMapper;
    private final ContentMapper contentMapper;

    @Transactional
    public CourseDTO createCourse(String teacherUuid, CourseDTO courseDTO) {
        log.info("Creating new course for teacher: {}", teacherUuid);

        try {
            TeacherResponseDTO teacher = this.userServiceClient.getTeacherByUuid(teacherUuid);
            log.info("Teacher found: {} {}", teacher.getFirstName(), teacher.getLastName());
        } catch (Exception var5) {
            throw new ResourceNotFoundException("Teacher", "uuid", teacherUuid);
        }

        if (courseDTO.getTeacherUuid() == null || courseDTO.getTeacherUuid().isEmpty()) {
            courseDTO.setTeacherUuid(teacherUuid);
        }

        Course course = this.courseMapper.toCourse(courseDTO);
        course = this.courseRepository.save(course);
        log.info("Course created successfully with uuid: {}, title: {}", course.getUuid(), course.getTitle());
        return this.courseMapper.toCourseDTO(course);
    }

    public CourseDTO getCourseByUuid(String courseUuid) {
        log.info("Fetching course by uuid: {}", courseUuid);
        Course course = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));
        return this.courseMapper.toCourseDTO(course);
    }

    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        return null;
    }

    public Page<CourseDTO> searchCourses(String keyword, Pageable pageable) {
        return null;
    }

    public List<CourseDTO> getCoursesByTeacher(String teacherUuid) {
        log.info("Fetching all courses for teacher: {}", teacherUuid);

        try {
            this.userServiceClient.getTeacherByUuid(teacherUuid);
        } catch (Exception var3) {
            throw new ResourceNotFoundException("Teacher", "uuid", teacherUuid);
        }

        return this.courseRepository.findByTeacherUuid(teacherUuid)
                .stream()
                .map(this.courseMapper::toCourseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO updateCourse(String teacherUuid, String courseUuid, CourseDTO courseDTO) {
        log.info("Updating course: {} for teacher: {}", courseUuid, teacherUuid);
        Course existingCourse = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!existingCourse.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to update this course");
        }

        if (courseDTO.getTitle() != null && !courseDTO.getTitle().isEmpty()) {
            existingCourse.setTitle(courseDTO.getTitle());
        }

        if (courseDTO.getDescription() != null && !courseDTO.getDescription().isEmpty()) {
            existingCourse.setDescription(courseDTO.getDescription());
        }

        if (courseDTO.getTypeCourses() != null) {
            existingCourse.setTypeCourses(courseDTO.getTypeCourses());
        }

        if (courseDTO.getThumbnailUrl() != null && !courseDTO.getThumbnailUrl().isEmpty()) {
            existingCourse.setThumbnailUrl(courseDTO.getThumbnailUrl());
        }

        if (courseDTO.getStatus() != null) {
            existingCourse.setStatus(courseDTO.getStatus());
        }

        if (courseDTO.getStartDate() != null) {
            existingCourse.setStartDate(courseDTO.getStartDate());
        }

        if (courseDTO.getEndDate() != null) {
            existingCourse.setEndDate(courseDTO.getEndDate());
        }

        if (courseDTO.getDurationHours() != null) {
            existingCourse.setDurationHours(courseDTO.getDurationHours());
        }

        if (courseDTO.getCredits() != null) {
            existingCourse.setCredits(courseDTO.getCredits());
        }

        if (existingCourse.getTimestamps() != null) {
            existingCourse.getTimestamps().setUpdatedAt(LocalDateTime.now());
        }

        existingCourse = this.courseRepository.save(existingCourse);
        log.info("Course updated successfully: {}", courseUuid);
        return this.courseMapper.toCourseDTO(existingCourse);
    }

    @Transactional
    public void deleteCourse(String teacherUuid, String courseUuid) {
        log.info("Deleting course: {} for teacher: {}", courseUuid, teacherUuid);
        Course course = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this course");
        }

        // CORRECTION: Passer le teacherUuid comme paramètre
        if (course.getTimestamps() != null) {
            course.getTimestamps().softDelete(teacherUuid);
            course.getTimestamps().setUpdatedAt(LocalDateTime.now());
        }

        this.courseRepository.save(course);
        log.info("Course deleted successfully: {}", courseUuid);
    }

    @Transactional
    public void deleteCoursePermanent(String teacherUuid, String courseUuid) {
        log.info("Permanently deleting course: {} for teacher: {}", courseUuid, teacherUuid);
        Course course = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this course");
        }

        this.courseRepository.delete(course);
        log.info("Course permanently deleted: {}", courseUuid);
    }

    public CourseDTO publishCourse(String teacherUuid, String courseUuid) {
        log.info("Publishing course: {} for teacher: {}", courseUuid, teacherUuid);
        Course course = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to publish this course");
        }

        long contentCount = this.contentRepository.countByCourseUuid(courseUuid);
        if (contentCount == 0L) {
            throw new IllegalStateException("Cannot publish a course with no content");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        if (course.getTimestamps() != null) {
            course.getTimestamps().setUpdatedAt(LocalDateTime.now());
        }

        course = this.courseRepository.save(course);
        log.info("Course published successfully: {}", courseUuid);
        return this.courseMapper.toCourseDTO(course);
    }

    public List<CourseDTO> getCoursesByStudentLevel(String studentUuid) {
        log.info("Fetching courses for student: {}", studentUuid);
        StudentResponseDTO student = this.userServiceClient.getStudentByUuid(studentUuid);
        LevelStudent studentLevel = student.getLevelStudent();

        return this.courseRepository.findByLevelStudent(studentLevel)
                .stream()
                .map(this.courseMapper::toCourseDTO)
                .collect(Collectors.toList());
    }

    public List<ContentDTO> getContentsByCourseAndStudentLevel(String studentUuid, String courseUuid) {
        StudentResponseDTO student = this.userServiceClient.getStudentByUuid(studentUuid);
        Course course = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getLevelStudent().equals(student.getLevelStudent())) {
            throw new UnauthorizedAccessException(String.format("Student level %s does not match course level %s",
                    student.getLevelStudent(), course.getLevelStudent()));
        }

        return this.contentRepository.findByCourseUuidOrderByOrderIndexAsc(courseUuid)
                .stream()
                .map(this.contentMapper::toContentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO archiveCourse(String teacherUuid, String courseUuid) {
        log.info("Archiving course: {} for teacher: {}", courseUuid, teacherUuid);
        Course course = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to archive this course");
        }

        course.setStatus(CourseStatus.ARCHIVED);
        course.setIsPublished(false);
        if (course.getTimestamps() != null) {
            course.getTimestamps().setUpdatedAt(LocalDateTime.now());
        }

        course = this.courseRepository.save(course);
        log.info("Course archived successfully: {}", courseUuid);
        return this.courseMapper.toCourseDTO(course);
    }

    @Transactional
    public CourseDTO unarchiveCourse(String teacherUuid, String courseUuid) {
        log.info("Unarchiving course: {} for teacher: {}", courseUuid, teacherUuid);
        Course course = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to unarchive this course");
        }

        course.setStatus(CourseStatus.ACTIVE);
        if (course.getTimestamps() != null) {
            course.getTimestamps().setUpdatedAt(LocalDateTime.now());
        }

        course = this.courseRepository.save(course);
        log.info("Course unarchived successfully: {}", courseUuid);
        return this.courseMapper.toCourseDTO(course);
    }

    @Transactional
    public CourseDTO duplicateCourse(String teacherUuid, String courseUuid) {
        log.info("Duplicating course: {} for teacher: {}", courseUuid, teacherUuid);
        Course original = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!original.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to duplicate this course");
        }

        Course copy = new Course();
        copy.setUuid(java.util.UUID.randomUUID().toString());
        copy.setTitle("Copie de " + original.getTitle());
        copy.setDescription(original.getDescription());
        copy.setTeacherUuid(teacherUuid);
        copy.setTypeCourses(original.getTypeCourses());
        copy.setStatus(CourseStatus.DRAFT);
        copy.setLevelStudent(original.getLevelStudent());
        copy.setThumbnailUrl(original.getThumbnailUrl());
        copy.setStartDate(original.getStartDate());
        copy.setEndDate(original.getEndDate());
        copy.setDurationHours(original.getDurationHours());
        copy.setCredits(original.getCredits());
        copy.setIsPublished(false);
        copy.setIsFeatured(false);
        copy.setLanguage(original.getLanguage());
        copy.setDifficultyLevel(original.getDifficultyLevel());
        copy.setPrerequisites(original.getPrerequisites());
        copy.setLearningObjectives(original.getLearningObjectives());
        copy.setTargetAudience(original.getTargetAudience());
        copy.setEnrollmentCount(0);
        copy.setRatingAverage(0.0);
        copy.setRatingCount(0);
        copy.setTags(original.getTags());

        if (copy.getTimestamps() == null) {
            copy.setTimestamps(new com.edueasy.common.model.AuditTimestamps());
        }
        copy.getTimestamps().setCreatedAt(LocalDateTime.now());
        copy.getTimestamps().setUpdatedAt(LocalDateTime.now());
        copy.getTimestamps().setCreatedBy(teacherUuid);
        copy.getTimestamps().setUpdatedBy(teacherUuid);
        copy.getTimestamps().setDeleted(false);

        copy = this.courseRepository.save(copy);
        log.info("Course duplicated successfully: {}", copy.getUuid());

        // Dupliquer les contenus
        List<com.edueasy.course.model.Content> originalContents = this.contentRepository.findByCourseUuidOrderByOrderIndexAsc(courseUuid);
        for (com.edueasy.course.model.Content originalContent : originalContents) {
            com.edueasy.course.model.Content contentCopy = new com.edueasy.course.model.Content();
            contentCopy.setUuid(java.util.UUID.randomUUID().toString());
            contentCopy.setTitle("Copie de " + originalContent.getTitle());
            contentCopy.setDescription(originalContent.getDescription());
            contentCopy.setType(originalContent.getType());
            contentCopy.setContentUrl(originalContent.getContentUrl());
            contentCopy.setDurationMinutes(originalContent.getDurationMinutes());
            contentCopy.setIsRequired(originalContent.isRequired());
            contentCopy.setIsFree(originalContent.isFree());
            contentCopy.setIsPublished(false);
            contentCopy.setCourse(copy);
            contentCopy.setCourseUuid(copy.getUuid());
            contentCopy.setTeacherUuid(teacherUuid);
            contentCopy.setOrderIndex(originalContent.getOrderIndex());

            if (contentCopy.getTimestamps() == null) {
                contentCopy.setTimestamps(new com.edueasy.common.model.AuditTimestamps());
            }
            contentCopy.getTimestamps().setCreatedAt(LocalDateTime.now());
            contentCopy.getTimestamps().setUpdatedAt(LocalDateTime.now());
            contentCopy.getTimestamps().setCreatedBy(teacherUuid);
            contentCopy.getTimestamps().setUpdatedBy(teacherUuid);
            contentCopy.getTimestamps().setDeleted(false);

            this.contentRepository.save(contentCopy);
        }

        return this.courseMapper.toCourseDTO(copy);
    }

    @Transactional
    public void permanentDeleteCourse(String teacherUuid, String courseUuid) {
        log.info("Permanently deleting course: {} for teacher: {}", courseUuid, teacherUuid);
        Course course = this.courseRepository.findByUuid(courseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "uuid", courseUuid));

        if (!course.getTeacherUuid().equals(teacherUuid)) {
            throw new UnauthorizedAccessException("You are not authorized to permanently delete this course");
        }

        // Supprimer d'abord tous les contenus associés
        this.contentRepository.deleteByCourseUuid(courseUuid);

        // Puis supprimer le cours
        this.courseRepository.delete(course);
        log.info("Course permanently deleted: {}", courseUuid);
    }

    public List<CourseDTO> getCoursesByStudent(String studentUuid) {
        log.info("Fetching courses for student: {}", studentUuid);
        // Implémentation à venir
        return List.of();
    }

    public CourseServiceImpl(final CourseRepository courseRepository,
                             final ContentRepository contentRepository,
                             final UserServiceClient userServiceClient,
                             final CourseMapper courseMapper,
                             final ContentMapper contentMapper) {
        this.courseRepository = courseRepository;
        this.contentRepository = contentRepository;
        this.userServiceClient = userServiceClient;
        this.courseMapper = courseMapper;
        this.contentMapper = contentMapper;
    }
}