package com.edueasy.user.repository;


import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.SupportStaff;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SupportStaffRepository extends JpaRepository<SupportStaff, Long> {
    Optional<SupportStaff> findByUuid(String uuid);
    boolean existsByUsername(String username);  // ← AJOUTER CETTE MÉTHODE
    Optional<SupportStaff> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByStaffNumber(String staffNumber);

    Page<SupportStaff> findAll(Pageable pageable);

    @Query("SELECT s FROM SupportStaff s WHERE s.firstName LIKE %:keyword% OR s.lastName LIKE %:keyword% OR s.email LIKE %:keyword%")
    Page<SupportStaff> search(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE SupportStaff s SET s.lastLoginAt = CURRENT_TIMESTAMP WHERE s.uuid = :uuid")
    void updateLastLogin(@Param("uuid") String uuid);

    long countByStatus(UserStatus status);
}
