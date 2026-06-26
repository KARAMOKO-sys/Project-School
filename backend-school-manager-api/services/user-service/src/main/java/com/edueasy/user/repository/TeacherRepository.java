package com.edueasy.user.repository;


import com.edueasy.common.model.TeacherSimple;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherSimple, String> {
    Optional<TeacherSimple> findByEmail(String email);

    Optional<TeacherSimple> findByTeacherNumber(String teacherNumber);

    boolean existsByEmail(String email);

    boolean existsByTeacherNumber(String teacherNumber);

    @Modifying
    @Transactional
    @Query("UPDATE TeacherSimple t SET t.lastLoginAt = CURRENT_TIMESTAMP WHERE t.id = :teacherId")
    void updateLastLogin(@Param("teacherId") String teacherId);
}
