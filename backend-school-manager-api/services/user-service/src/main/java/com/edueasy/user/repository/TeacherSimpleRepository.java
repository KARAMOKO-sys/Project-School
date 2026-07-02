package com.edueasy.user.repository;

import com.edueasy.common.enums.LevelTeacher;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.TeacherSimple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSimpleRepository extends JpaRepository<TeacherSimple, String> {

    // ===== Recherches par champ =====

    Optional<TeacherSimple> findByUuid(String teacherUuid);

    boolean existsByUsername(String username);

    Optional<TeacherSimple> findByEmail(String email);

    Optional<TeacherSimple> findByTeacherNumber(String teacherNumber);

    Optional<TeacherSimple> findByEmailAndStatus(String email, UserStatus status);

    boolean existsByEmail(String email);

    boolean existsByUuid(String uuid);

   // boolean existsByTeacherNumber(String teacherNumber);

    // ===== Recherches par statut =====

    Page<TeacherSimple> findAllByStatus(UserStatus status, Pageable pageable);

    long countByStatus(UserStatus status);

    // ===== Recherches par niveau =====

    Page<TeacherSimple> findByLevelTeacher(LevelTeacher levelTeacher, Pageable pageable);

    Page<TeacherSimple> findByStatutUserSimple(StatutUserSimple statutUserSimple, Pageable pageable);

    Page<TeacherSimple> findByLevelTeacherAndStatus(LevelTeacher levelTeacher, UserStatus status, Pageable pageable);

    Page<TeacherSimple> findByStatutUserSimpleAndStatus(StatutUserSimple statutUserSimple, UserStatus status, Pageable pageable);

    // ===== Recherches par texte =====

    Page<TeacherSimple> findByFirstNameContainingOrLastNameContainingOrEmailContaining(
            String firstName, String lastName, String email, Pageable pageable);

    // ===== AJOUT : Méthode de recherche générique =====

    @Query("SELECT t FROM TeacherSimple t WHERE " +
            "LOWER(t.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.teacherNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<TeacherSimple> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t FROM TeacherSimple t WHERE " +
            "LOWER(t.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.status = :status")
    Page<TeacherSimple> searchByStatus(@Param("keyword") String keyword,
                                       @Param("status") UserStatus status,
                                       Pageable pageable);

    // ===== Recherches par date =====

    List<TeacherSimple> findByLastLoginAtBefore(LocalDateTime date);

    List<TeacherSimple> findByLastLoginAtAfter(LocalDateTime date);

    // ===== Requêtes de mise à jour =====

    @Modifying
    @Transactional
    @Query("UPDATE TeacherSimple t SET t.status = :status WHERE t.id = :teacherId")
    void updateStatus(@Param("teacherId") String teacherId, @Param("status") UserStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE TeacherSimple t SET t.status = :status WHERE t.uuid = :uuid")
    void updateStatusByUuid(@Param("uuid") String uuid, @Param("status") UserStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE TeacherSimple t SET t.lastLoginAt = CURRENT_TIMESTAMP WHERE t.id = :teacherId")
    void updateLastLogin(@Param("teacherId") String teacherId);

    @Modifying
    @Transactional
    @Query("UPDATE TeacherSimple t SET t.lastLoginAt = CURRENT_TIMESTAMP WHERE t.uuid = :uuid")
    void updateLastLoginByUuid(@Param("uuid") String uuid);

    // ===== Requêtes de comptage =====

    @Query("SELECT COUNT(t) FROM TeacherSimple t WHERE t.levelTeacher = :level")
    long countByLevelTeacher(@Param("level") LevelTeacher level);

    @Query("SELECT COUNT(t) FROM TeacherSimple t WHERE t.statutUserSimple = :statut")
    long countByStatutUserSimple(@Param("statut") StatutUserSimple statut);

    @Query("SELECT COUNT(t) FROM TeacherSimple t WHERE t.status = :status AND t.levelTeacher = :level")
    long countByStatusAndLevel(@Param("status") UserStatus status, @Param("level") LevelTeacher level);

    @Query("SELECT COUNT(t) FROM TeacherSimple t WHERE t.status = :status AND t.statutUserSimple = :statut")
    long countByStatusAndStatut(@Param("status") UserStatus status, @Param("statut") StatutUserSimple statut);

    // ===== Requêtes d'existence =====

    boolean existsByEmailAndStatus(String email, UserStatus status);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TeacherSimple t WHERE t.teacherNumber = :teacherNumber")
    boolean existsByTeacherNumber(@Param("teacherNumber") String teacherNumber);
}