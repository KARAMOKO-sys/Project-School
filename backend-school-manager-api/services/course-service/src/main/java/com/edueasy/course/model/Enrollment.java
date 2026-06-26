package com.edueasy.course.model;

import com.edueasy.course.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "course_id"})
        },
        indexes = {
                @Index(name = "idx_enrollment_student", columnList = "student_id"),
                @Index(name = "idx_enrollment_course", columnList = "course_id"),
                @Index(name = "idx_enrollment_status", columnList = "status"),
                @Index(name = "idx_enrollment_created", columnList = "created_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder.Default
    private Double progress = 0.0;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "last_accessed_content")
    private String lastAccessedContent;

    @Column(name = "completed_contents")
    @Builder.Default
    private Integer completedContents = 0;

    @Column(name = "total_contents")
    private Integer totalContents;

    @Column(name = "time_spent_minutes")
    @Builder.Default
    private Integer timeSpentMinutes = 0;

    @Column(name = "is_paid")
    @Builder.Default
    private Boolean isPaid = false;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @Column(name = "certificate_issued_at")
    private LocalDateTime certificateIssuedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = EnrollmentStatus.ENROLLED;
        }
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
        if (progress == null) {
            progress = 0.0;
        }
        if (completedContents == null) {
            completedContents = 0;
        }
        if (timeSpentMinutes == null) {
            timeSpentMinutes = 0;
        }
        if (isPaid == null) {
            isPaid = false;
        }
        if (course != null && totalContents == null) {
            totalContents = course.getContentsCount();
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si l'inscription est active
     */
    public boolean isActive() {
        return status == EnrollmentStatus.ENROLLED || status == EnrollmentStatus.IN_PROGRESS;
    }

    /**
     * Vérifie si l'inscription est terminée
     */
    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED;
    }

    /**
     * Vérifie si l'inscription est en attente
     */
    public boolean isPending() {
        return status == EnrollmentStatus.PENDING;
    }

    /**
     * Vérifie si l'inscription est annulée
     */
    public boolean isCancelled() {
        return status == EnrollmentStatus.CANCELLED;
    }

    /**
     * Vérifie si l'inscription est suspendue
     */
    public boolean isSuspended() {
        return status == EnrollmentStatus.SUSPENDED;
    }

    /**
     * Vérifie si l'inscription est expirée
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Vérifie si l'inscription a un certificat
     */
    public boolean hasCertificate() {
        return certificateUrl != null && !certificateUrl.isEmpty();
    }

    /**
     * Vérifie si l'inscription est payée
     */
    public boolean isPaid() {
        return isPaid != null && isPaid;
    }

    /**
     * Vérifie si l'inscription a des notes
     */
    public boolean hasNotes() {
        return notes != null && !notes.isEmpty();
    }

    /**
     * Met à jour la progression
     */
    public void updateProgress(double progress) {
        this.progress = Math.min(Math.max(progress, 0.0), 100.0);
        if (this.progress >= 100.0 && status != EnrollmentStatus.COMPLETED) {
            complete();
        } else if (this.progress > 0 && this.progress < 100 && status == EnrollmentStatus.ENROLLED) {
            this.status = EnrollmentStatus.IN_PROGRESS;
        }
    }

    /**
     * Met à jour le nombre de contenus complétés
     */
    public void updateCompletedContents(int completed) {
        this.completedContents = completed;
        if (totalContents != null && totalContents > 0) {
            double newProgress = (double) completed / totalContents * 100.0;
            updateProgress(newProgress);
        }
        updateLastAccessed();
    }

    /**
     * Marque l'inscription comme terminée
     */
    public void complete() {
        this.status = EnrollmentStatus.COMPLETED;
        this.progress = 100.0;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Annule l'inscription
     */
    public void cancel() {
        this.status = EnrollmentStatus.CANCELLED;
    }

    /**
     * Suspend l'inscription
     */
    public void suspend() {
        this.status = EnrollmentStatus.SUSPENDED;
    }

    /**
     * Réactive l'inscription
     */
    public void reactivate() {
        if (status == EnrollmentStatus.SUSPENDED || status == EnrollmentStatus.CANCELLED) {
            this.status = EnrollmentStatus.IN_PROGRESS;
        }
    }

    /**
     * Met à jour la date du dernier accès
     */
    public void updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Ajoute du temps passé
     */
    public void addTimeSpent(int minutes) {
        if (timeSpentMinutes == null) {
            timeSpentMinutes = 0;
        }
        this.timeSpentMinutes += minutes;
        updateLastAccessed();
    }

    /**
     * Marque comme payé
     */
    public void markAsPaid(String paymentReference) {
        this.isPaid = true;
        this.paymentReference = paymentReference;
    }

    /**
     * Émet le certificat
     */
    public void issueCertificate(String certificateUrl) {
        if (isCompleted()) {
            this.certificateUrl = certificateUrl;
            this.certificateIssuedAt = LocalDateTime.now();
        }
    }

    /**
     * Ajoute des notes
     */
    public void addNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Vérifie si l'inscription est valide
     */
    public boolean isValid() {
        return studentId != null && !studentId.isEmpty()
                && course != null
                && status != null
                && !isExpired();
    }

    /**
     * Retourne le statut en français
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case PENDING -> "En attente";
            case ENROLLED -> "Inscrit";
            case IN_PROGRESS -> "En cours";
            case COMPLETED -> "Terminé";
            case CANCELLED -> "Annulé";
            case DROPPED -> "Abandonné";
            case SUSPENDED -> "Suspendu";
        };
    }

    /**
     * Retourne la progression formatée
     */
    public String getFormattedProgress() {
        if (progress == null) {
            return "0%";
        }
        return String.format("%.1f%%", progress);
    }

    /**
     * Vérifie si le cours a été complété à 100%
     */
    public boolean isFullyCompleted() {
        return progress != null && progress >= 100.0;
    }

    /**
     * Retourne le temps passé formaté
     */
    public String getFormattedTimeSpent() {
        if (timeSpentMinutes == null || timeSpentMinutes == 0) {
            return "0 min";
        }
        if (timeSpentMinutes < 60) {
            return timeSpentMinutes + " min";
        }
        int hours = timeSpentMinutes / 60;
        int minutes = timeSpentMinutes % 60;
        if (minutes == 0) {
            return hours + " h";
        }
        return hours + "h " + minutes + "min";
    }

    /**
     * Vérifie si l'inscription est éligible au certificat
     */
    public boolean isEligibleForCertificate() {
        return isCompleted() && !hasCertificate();
    }

    /**
     * Retourne la durée depuis l'inscription
     */
    public String getTimeSinceEnrollment() {
        if (enrolledAt == null) {
            return "N/A";
        }
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(enrolledAt, now).toDays();
        if (days < 1) {
            long hours = java.time.Duration.between(enrolledAt, now).toHours();
            if (hours < 1) {
                long minutes = java.time.Duration.between(enrolledAt, now).toMinutes();
                return minutes + " min";
            }
            return hours + " h";
        }
        if (days < 30) {
            return days + " j";
        }
        long months = days / 30;
        return months + " mois";
    }
}