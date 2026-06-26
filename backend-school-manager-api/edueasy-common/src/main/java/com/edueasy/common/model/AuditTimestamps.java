package com.edueasy.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditTimestamps {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    /**
     * Marque l'entité comme supprimée (soft delete) - Version avec paramètre
     */
    public void softDelete(String deletedBy) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Marque l'entité comme supprimée (soft delete) - Version sans paramètre (pour rétrocompatibilité)
     * Utilise le createdBy comme deletedBy si disponible
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = this.createdBy != null ? this.createdBy : "system";
    }

    /**
     * Marque l'entité comme supprimée (alias pour softDelete avec paramètre)
     */
    public void markAsDeleted(String deletedBy) {
        softDelete(deletedBy);
    }

    /**
     * Marque l'entité comme supprimée (alias pour softDelete sans paramètre)
     */
    public void markAsDeleted() {
        softDelete();
    }

    /**
     * Restaure l'entité après un soft delete
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }

    /**
     * Vérifie si l'entité est supprimée
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Vérifie si l'entité a été créée
     */
    public boolean isCreated() {
        return createdAt != null;
    }

    /**
     * Vérifie si l'entité a été mise à jour
     */
    public boolean isUpdated() {
        return updatedAt != null && !updatedAt.equals(createdAt);
    }

    /**
     * Retourne le temps depuis la création en minutes
     */
    public long getMinutesSinceCreation() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    }

    /**
     * Retourne le temps depuis la création en heures
     */
    public long getHoursSinceCreation() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
    }

    /**
     * Retourne le temps depuis la création en jours
     */
    public long getDaysSinceCreation() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
    }
}