package com.edueasy.course.service;

import com.edueasy.course.dto.ContentDTO;
import com.edueasy.course.dto.CourseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    CourseDTO createCourse(String teacherUuid, CourseDTO courseDTO);

    CourseDTO getCourseByUuid(String courseUuid);

    Page<CourseDTO> getAllCourses(Pageable pageable);

    Page<CourseDTO> searchCourses(String keyword, Pageable pageable);

    List<CourseDTO> getCoursesByTeacher(String teacherUuid);

    CourseDTO updateCourse(String teacherUuid, String courseUuid, CourseDTO courseDTO);

    void deleteCourse(String teacherUuid, String courseUuid);

    CourseDTO publishCourse(String teacherUuid, String courseUuid);

    List<CourseDTO> getCoursesByStudentLevel(String studentUuid);

    List<ContentDTO> getContentsByCourseAndStudentLevel(String studentUuid, String courseUuid);

    CourseDTO archiveCourse(String teacherUuid, String courseUuid);

    CourseDTO unarchiveCourse(String teacherUuid, String courseUuid);

    CourseDTO duplicateCourse(String teacherUuid, String courseUuid);

    void permanentDeleteCourse(String teacherUuid, String courseUuid);

    List<CourseDTO> getCoursesByStudent(String studentUuid);
}
