package com.edueasy.attendance.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "justifications",
        indexes = {
                @Index(name = "idx_justification_student", columnList = "student_id"),
                @Index(name = "idx_justification_attendance", columnList = "attendance_id"),
                @Index(name = "idx_justification_status", columnList = "status"),
                @Index(name = "idx_justification_submitted_at", columnList = "submitted_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Justification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "attendance_id", nullable = false)
    private String attendanceId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "document_url", length = 1000)
    private String documentUrl;

    @Enumerated(EnumType.STRING)
    private JustificationStatus status;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = JustificationStatus.PENDING;
        }
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si la justification est en attente
     */
    public boolean isPending() {
        return status == JustificationStatus.PENDING;
    }

    /**
     * Vérifie si la justification est approuvée
     */
    public boolean isApproved() {
        return status == JustificationStatus.APPROVED;
    }

    /**
     * Vérifie si la justification est rejetée
     */
    public boolean isRejected() {
        return status == JustificationStatus.REJECTED;
    }

    /**
     * Vérifie si la justification a été révisée
     */
    public boolean isReviewed() {
        return status == JustificationStatus.APPROVED ||
                status == JustificationStatus.REJECTED;
    }

    /**
     * Vérifie si la justification a un document joint
     */
    public boolean hasDocument() {
        return documentUrl != null && !documentUrl.isEmpty();
    }

    /**
     * Vérifie si la justification est valide
     */
    public boolean isValid() {
        return reason != null && !reason.trim().isEmpty()
                && studentId != null && !studentId.isEmpty()
                && attendanceId != null && !attendanceId.isEmpty();
    }

    /**
     * Approuve la justification
     */
    public void approve(String reviewerId) {
        this.status = JustificationStatus.APPROVED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Rejette la justification
     */
    public void reject(String reviewerId) {
        this.status = JustificationStatus.REJECTED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Annule la justification
     */
    public void cancel() {
        if (isPending()) {
            this.status = JustificationStatus.CANCELLED;
        }
    }

    /**
     * Vérifie si la justification peut être modifiée
     */
    public boolean isModifiable() {
        return status == JustificationStatus.PENDING;
    }

    /**
     * Vérifie si la justification est en attente depuis plus d'un certain nombre de jours
     */
    public boolean isPendingForMoreThan(int days) {
        if (submittedAt == null || status != JustificationStatus.PENDING) {
            return false;
        }
        return submittedAt.isBefore(LocalDateTime.now().minusDays(days));
    }

    /**
     * Retourne le libellé du statut
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case PENDING -> "En attente";
            case APPROVED -> "Approuvée";
            case REJECTED -> "Rejetée";
            case CANCELLED -> "Annulée";
        };
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (status == null) {
            return "gray";
        }
        return switch (status) {
            case PENDING -> "orange";
            case APPROVED -> "green";
            case REJECTED -> "red";
            case CANCELLED -> "gray";
        };
    }

    /**
     * Retourne le temps écoulé depuis la soumission
     */
    public String getTimeSinceSubmission() {
        if (submittedAt == null) {
            return "N/A";
        }
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(submittedAt, now).toMinutes();

        if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " heure" + (hours > 1 ? "s" : "");
        }
        long days = hours / 24;
        return days + " jour" + (days > 1 ? "s" : "");
    }

    /**
     * Met à jour la raison
     */
    public void updateReason(String newReason) {
        if (isModifiable() && newReason != null && !newReason.trim().isEmpty()) {
            this.reason = newReason;
        }
    }

    /**
     * Met à jour le document
     */
    public void updateDocument(String documentUrl) {
        if (isModifiable()) {
            this.documentUrl = documentUrl;
        }
    }

    /**
     * Vérifie si la justification est urgente (plus de 2 jours en attente)
     */
    public boolean isUrgent() {
        return isPendingForMoreThan(2);
    }

    /**
     * Vérifie si la justification a un commentaire de rejet
     */
    public boolean hasRejectionReason() {
        return isRejected() && reviewedBy != null;
    }

    /**
     * Retourne le nom du réviseur ou "N/A"
     */
    public String getReviewedByName() {
        return reviewedBy != null ? reviewedBy : "N/A";
    }
}