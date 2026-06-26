package com.edueasy.course.repository;

import com.edueasy.common.enums.LevelStudent;
import com.edueasy.course.enums.CourseStatus;
import com.edueasy.course.model.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByUuid(String uuid);

    List<Course> findByTeacherUuid(String teacherUuid);

    Page<Course> findByTeacherUuid(String teacherUuid, Pageable pageable);

    List<Course> findByStatus(CourseStatus status);

    Page<Course> findByStatusAndTeacherUuid(CourseStatus status, String teacherUuid, Pageable pageable);

    @Modifying
    @Query("UPDATE Course c SET c.status = :status WHERE c.uuid = :courseUuid")
    void updateStatus(@Param("courseUuid") String courseUuid, @Param("status") CourseStatus status);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.uuid = :courseUuid")
    long countEnrollmentsByCourseUuid(@Param("courseUuid") String courseUuid);

    List<Course> findByLevelStudent(LevelStudent levelStudent);
}