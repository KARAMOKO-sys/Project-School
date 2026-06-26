package com.edueasy.user.repository;

import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findByUuid(String uuid);

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    List<Admin> findByAccessLevelGreaterThanEqual(Integer accessLevel);

    List<Admin> findByStatus(UserStatus status);

    List<Admin> findByDepartment(String department);

    Page<Admin> findAll(Pageable pageable);

    @Query("SELECT a FROM Admin a WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.employeeNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Admin> search(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Admin a SET a.lastLoginAt = CURRENT_TIMESTAMP WHERE a.uuid = :uuid")
    void updateLastLogin(@Param("uuid") String uuid);

    long countByStatus(UserStatus status);

    @Query("SELECT a FROM Admin a WHERE a.accessLevel >= 5")
    List<Admin> findSuperAdmins();

    @Query("SELECT a FROM Admin a WHERE :permission MEMBER OF a.permissions")
    List<Admin> findByPermission(@Param("permission") String permission);

    /**
     * Compte les admins actifs créés aujourd'hui - CORRIGÉ
     * Utilise directement le champ createdAt hérité de AuditTimestamps
     */
    @Query("SELECT COUNT(a) FROM Admin a WHERE a.status = 'ACTIVE' AND a.createdAt >= CURRENT_DATE")
    long countActiveAdminsToday();

    /**
     * Compte les admins par niveau d'accès
     */
    @Query("SELECT COUNT(a) FROM Admin a WHERE a.accessLevel >= :level")
    long countByAccessLevelGreaterThanEqual(@Param("level") Integer level);

    /**
     * Recherche les admins par nom complet
     */
    @Query("SELECT a FROM Admin a WHERE CONCAT(a.firstName, ' ', a.lastName) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    Page<Admin> findByFullNameContaining(@Param("fullName") String fullName, Pageable pageable);
}