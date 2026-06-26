package com.edueasy.course.repository;

import com.edueasy.course.enums.ContentType;
import com.edueasy.course.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {

    Optional<Content> findByUuid(String uuid);

    List<Content> findByCourseUuidOrderByOrderIndexAsc(String courseUuid);

    List<Content> findByCourseUuidAndType(String courseUuid, ContentType type);

    // CORRECTION: Retourner List<Content> au lieu de Arrays
    List<Content> findByCourseUuidAndIsPublishedTrueOrderByOrderIndexAsc(String courseUuid);

    @Modifying
    @Query("UPDATE Content c SET c.orderIndex = :orderIndex WHERE c.uuid = :contentUuid")
    void updateOrderIndex(@Param("contentUuid") String contentUuid, @Param("orderIndex") Integer orderIndex);

    @Modifying
    @Query("DELETE FROM Content c WHERE c.course.uuid = :courseUuid")
    void deleteByCourseUuid(@Param("courseUuid") String courseUuid);

    long countByCourseUuid(String courseUuid);

    // Méthodes supplémentaires utiles
    List<Content> findByCourseUuidAndIsPublishedTrue(String courseUuid);

    List<Content> findByCourseUuidAndIsFreeTrue(String courseUuid);

    List<Content> findByCourseUuidOrderByOrderIndexDesc(String courseUuid);

    Optional<Content> findByCourseUuidAndOrderIndex(String courseUuid, Integer orderIndex);
}