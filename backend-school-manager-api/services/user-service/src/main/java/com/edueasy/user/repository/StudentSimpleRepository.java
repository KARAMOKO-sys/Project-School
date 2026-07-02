package com.edueasy.user.repository;

import com.edueasy.common.enums.LevelStudent;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.StudentSimple;
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
public interface StudentSimpleRepository extends JpaRepository<StudentSimple, String> {

    // ===== Recherches par champ =====

    boolean existsByUsername(String username);

    Optional<StudentSimple> findByEmail(String email);

    Optional<StudentSimple> findByUuid(String uuid);

    Optional<StudentSimple> findByEmailAndStatus(String email, UserStatus status);

    boolean existsByEmail(String email);

    boolean existsByUuid(String uuid);

    // ===== Recherches par statut =====

    Page<StudentSimple> findAllByStatus(UserStatus status, Pageable pageable);

    long countByStatus(UserStatus status);

    // ===== AJOUT : Recherche par LevelStudent =====

    Page<StudentSimple> findByLevelStudent(LevelStudent levelStudent, Pageable pageable);

    List<StudentSimple> findByLevelStudent(LevelStudent levelStudent);

    // ===== AJOUT : Recherche par StatutUserSimple =====

    Page<StudentSimple> findByStatutUserSimple(StatutUserSimple statutUserSimple, Pageable pageable);

    List<StudentSimple> findByStatutUserSimple(StatutUserSimple statutUserSimple);

    // ===== Recherches combinées =====

    Page<StudentSimple> findByLevelStudentAndStatus(LevelStudent levelStudent, UserStatus status, Pageable pageable);

    Page<StudentSimple> findByStatutUserSimpleAndStatus(StatutUserSimple statutUserSimple, UserStatus status, Pageable pageable);

    // ===== Recherches par texte =====

    Page<StudentSimple> findByFirstNameContainingOrLastNameContainingOrEmailContaining(
            String firstName, String lastName, String email, Pageable pageable);

    Page<StudentSimple> findByFirstNameContainingOrLastNameContainingOrEmailContainingAndStatus(
            String firstName, String lastName, String email, UserStatus status, Pageable pageable);

    // ===== Recherches par date =====

    List<StudentSimple> findByLastLoginAtBefore(LocalDateTime date);

    List<StudentSimple> findByLastLoginAtAfter(LocalDateTime date);

    List<StudentSimple> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // ===== Recherches par niveau et statut simple =====

    Page<StudentSimple> findByLevelStudentAndStatutUserSimple(
            LevelStudent levelStudent, StatutUserSimple statutUserSimple, Pageable pageable);

    // ===== Requêtes personnalisées =====

    @Modifying
    @Transactional
    @Query("UPDATE StudentSimple s SET s.status = :status WHERE s.uuid = :uuid")
    void updateStatusByUuid(@Param("uuid") String uuid, @Param("status") UserStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE StudentSimple s SET s.status = :status WHERE s.id = :studentId")
    void updateStatus(@Param("studentId") String studentId, @Param("status") UserStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE StudentSimple s SET s.lastLoginAt = CURRENT_TIMESTAMP WHERE s.uuid = :uuid")
    void updateLastLoginByUuid(@Param("uuid") String uuid);

    @Modifying
    @Transactional
    @Query("UPDATE StudentSimple s SET s.lastLoginAt = CURRENT_TIMESTAMP WHERE s.id = :studentId")
    void updateLastLogin(@Param("studentId") String studentId);

    // ===== Requêtes de comptage =====

    @Query("SELECT COUNT(s) FROM StudentSimple s WHERE FUNCTION('DATE', s.createdAt) = CURRENT_DATE")
    long countTodayRegistrations();

    @Query("SELECT COUNT(s) FROM StudentSimple s WHERE s.status = :status AND FUNCTION('DATE', s.createdAt) = CURRENT_DATE")
    long countTodayRegistrationsByStatus(@Param("status") UserStatus status);

    @Query("SELECT COUNT(s) FROM StudentSimple s WHERE s.levelStudent = :level")
    long countByLevelStudent(@Param("level") LevelStudent level);

    @Query("SELECT COUNT(s) FROM StudentSimple s WHERE s.statutUserSimple = :statut")
    long countByStatutUserSimple(@Param("statut") StatutUserSimple statut);

    // ===== Requêtes d'existence =====

    boolean existsByEmailAndStatus(String email, UserStatus status);

    boolean existsByUuidAndStatus(String uuid, UserStatus status);

    // ===== Requêtes avec projections =====

    @Query("SELECT s.email FROM StudentSimple s WHERE s.status = :status")
    List<String> findAllEmailsByStatus(@Param("status") UserStatus status);

    @Query("SELECT s.uuid FROM StudentSimple s WHERE s.status = :status")
    List<String> findAllUuidsByStatus(@Param("status") UserStatus status);

    // ===== Recherche par nom complet (JPQL) =====

    @Query("SELECT s FROM StudentSimple s WHERE CONCAT(s.firstName, ' ', s.lastName) LIKE %:fullName%")
    Page<StudentSimple> findByFullNameContaining(@Param("fullName") String fullName, Pageable pageable);

    @Query("SELECT s FROM StudentSimple s WHERE CONCAT(s.firstName, ' ', s.lastName) LIKE %:fullName% AND s.status = :status")
    Page<StudentSimple> findByFullNameContainingAndStatus(
            @Param("fullName") String fullName,
            @Param("status") UserStatus status,
            Pageable pageable);

    // ===== Recherche par période d'inscription =====

    @Query("SELECT s FROM StudentSimple s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    List<StudentSimple> findStudentsRegisteredBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ===== Recherche des étudiants inactifs depuis longtemps =====

    @Query("SELECT s FROM StudentSimple s WHERE s.lastLoginAt IS NOT NULL AND s.lastLoginAt < :date AND s.status = :status")
    List<StudentSimple> findInactiveStudentsSince(
            @Param("date") LocalDateTime date,
            @Param("status") UserStatus status);

    // ===== Statistiques =====

    @Query("SELECT COUNT(s) FROM StudentSimple s WHERE s.levelStudent = :level AND s.status = :status")
    long countByLevelAndStatus(
            @Param("level") LevelStudent level,
            @Param("status") UserStatus status);

    @Query("SELECT COUNT(s) FROM StudentSimple s WHERE s.statutUserSimple = :statut AND s.status = :status")
    long countByStatutAndStatus(
            @Param("statut") StatutUserSimple statut,
            @Param("status") UserStatus status);

    // ===== Recherche avec pagination et tri =====

    Page<StudentSimple> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<StudentSimple> findAllByOrderByLastNameAsc(Pageable pageable);

    Page<StudentSimple> findAllByOrderByFirstNameAsc(Pageable pageable);
}