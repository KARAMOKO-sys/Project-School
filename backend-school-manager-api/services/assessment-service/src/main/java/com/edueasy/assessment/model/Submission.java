package com.edueasy.assessment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(columnDefinition = "TEXT")
    private String answers;

    private Double score;

    private Double percentage;

    @Column(length = 2000)
    private String feedback;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Column(name = "graded_by")
    private String gradedBy;

    @Column(name = "attempt_number")
    @Builder.Default
    private Integer attemptNumber = 1;

    @Column(name = "is_late")
    @Builder.Default
    private Boolean isLate = false;

    @Column(name = "file_url")
    private String fileUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = SubmissionStatus.SUBMITTED;
        }
        if (attemptNumber == null) {
            attemptNumber = 1;
        }
        if (isLate == null) {
            isLate = false;
        }
        checkLateSubmission();
    }

    @PreUpdate
    protected void onUpdate() {
        checkLateSubmission();
        calculatePercentage();
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si la soumission est en retard
     */
    public void checkLateSubmission() {
        if (assessment != null && assessment.getEndDate() != null && submittedAt != null) {
            this.isLate = submittedAt.isAfter(assessment.getEndDate());
        }
    }

    /**
     * Calcule le pourcentage à partir du score et du maxScore de l'évaluation
     */
    public void calculatePercentage() {
        if (score != null && assessment != null && assessment.getMaxScore() != null
                && assessment.getMaxScore() > 0) {
            this.percentage = (score / assessment.getMaxScore()) * 100.0;
        }
    }

    /**
     * Vérifie si la soumission a été notée
     */
    public boolean isGraded() {
        return score != null && status == SubmissionStatus.GRADED;
    }

    /**
     * Vérifie si la soumission est en attente
     */
    public boolean isPending() {
        return status == SubmissionStatus.PENDING || status == SubmissionStatus.SUBMITTED;
    }

    /**
     * Vérifie si la soumission est en cours de révision
     */
    public boolean isUnderReview() {
        return status == SubmissionStatus.UNDER_REVIEW;
    }

    /**
     * Vérifie si la soumission a été rejetée
     */
    public boolean isRejected() {
        return status == SubmissionStatus.REJECTED;
    }

    /**
     * Vérifie si la soumission est acceptée
     */
    public boolean isAccepted() {
        return status == SubmissionStatus.ACCEPTED;
    }

    /**
     * Vérifie si la soumission est en brouillon
     */
    public boolean isDraft() {
        return status == SubmissionStatus.DRAFT;
    }

    /**
     * Vérifie si la soumission est en retard
     */
    public boolean isLateStatus() {
        return status == SubmissionStatus.LATE;
    }

    /**
     * Vérifie si la soumission est manquante
     */
    public boolean isMissing() {
        return status == SubmissionStatus.MISSING;
    }

    /**
     * Vérifie si la soumission est excusée
     */
    public boolean isExcused() {
        return status == SubmissionStatus.EXCUSED;
    }

    /**
     * Soumet la soumission
     */
    public void submit() {
        this.submittedAt = LocalDateTime.now();
        this.status = SubmissionStatus.SUBMITTED;
        checkLateSubmission();
    }

    /**
     * Note la soumission
     */
    public void grade(Double score, String gradedBy) {
        this.score = score;
        this.gradedBy = gradedBy;
        this.gradedAt = LocalDateTime.now();
        this.status = SubmissionStatus.GRADED;
        calculatePercentage();
    }

    /**
     * Accepte la soumission
     */
    public void accept() {
        this.status = SubmissionStatus.ACCEPTED;
    }

    /**
     * Rejette la soumission
     */
    public void reject(String reason) {
        this.status = SubmissionStatus.REJECTED;
        this.feedback = reason;
    }

    /**
     * Met la soumission en révision
     */
    public void requestReview() {
        this.status = SubmissionStatus.UNDER_REVIEW;
    }

    /**
     * Met la soumission en attente
     */
    public void setPending() {
        this.status = SubmissionStatus.PENDING;
    }

    /**
     * Marque la soumission comme en retard
     */
    public void markLate() {
        this.status = SubmissionStatus.LATE;
        this.isLate = true;
    }

    /**
     * Marque la soumission comme manquante
     */
    public void markMissing() {
        this.status = SubmissionStatus.MISSING;
    }

    /**
     * Marque la soumission comme excusée
     */
    public void markExcused() {
        this.status = SubmissionStatus.EXCUSED;
    }

    /**
     * Met à jour le feedback
     */
    public void addFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Vérifie si la soumission a du feedback
     */
    public boolean hasFeedback() {
        return feedback != null && !feedback.isEmpty();
    }

    /**
     * Vérifie si la soumission a une réponse
     */
    public boolean hasAnswers() {
        return answers != null && !answers.isEmpty();
    }

    /**
     * Vérifie si la soumission a un fichier
     */
    public boolean hasFile() {
        return fileUrl != null && !fileUrl.isEmpty();
    }

    /**
     * Vérifie si l'étudiant a réussi
     */
    public boolean isPassed() {
        if (!isGraded()) {
            return false;
        }
        if (assessment != null && assessment.getPassingScore() != null) {
            return score != null && score >= assessment.getPassingScore();
        }
        return percentage != null && percentage >= 60;
    }

    /**
     * Vérifie si l'étudiant a échoué
     */
    public boolean isFailed() {
        return isGraded() && !isPassed();
    }

    /**
     * Retourne la note formatée
     */
    public String getFormattedScore() {
        if (score == null) {
            return "Not graded";
        }
        if (assessment != null && assessment.getMaxScore() != null) {
            return String.format("%.1f / %.1f", score, assessment.getMaxScore());
        }
        return String.format("%.1f", score);
    }

    /**
     * Retourne le pourcentage formaté
     */
    public String getFormattedPercentage() {
        if (percentage == null) {
            return "N/A";
        }
        return String.format("%.1f%%", percentage);
    }

    /**
     * Retourne le statut de la soumission - VERSION CORRIGÉE
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Unknown";
        }
        return switch (status) {
            case DRAFT -> "Brouillon";
            case PENDING -> "En attente";
            case SUBMITTED -> "Soumis";
            case UNDER_REVIEW -> "En révision";
            case GRADED -> "Noté";
            case ACCEPTED -> "Accepté";
            case REJECTED -> "Rejeté";
            case LATE -> "En retard";
            case MISSING -> "Manquant";
            case EXCUSED -> "Excusé";
        };
    }

    /**
     * Retourne la couleur associée au statut (pour l'UI)
     */
    public String getStatusColor() {
        if (status == null) {
            return "secondary";
        }
        return switch (status) {
            case DRAFT, PENDING -> "warning";
            case SUBMITTED, UNDER_REVIEW -> "info";
            case GRADED, ACCEPTED -> "success";
            case REJECTED, LATE, MISSING -> "danger";
            case EXCUSED -> "primary";
        };
    }

    /**
     * Vérifie si la soumission est modifiable
     */
    public boolean isModifiable() {
        return status == SubmissionStatus.DRAFT ||
                status == SubmissionStatus.PENDING ||
                status == SubmissionStatus.SUBMITTED;
    }

    /**
     * Vérifie si la soumission peut être notée
     */
    public boolean canBeGraded() {
        return status == SubmissionStatus.SUBMITTED ||
                status == SubmissionStatus.UNDER_REVIEW ||
                status == SubmissionStatus.LATE;
    }

    /**
     * Incrémente le numéro de tentative
     */
    public void incrementAttempt() {
        if (attemptNumber == null) {
            attemptNumber = 1;
        } else {
            attemptNumber++;
        }
    }

    /**
     * Vérifie si c'est la première tentative
     */
    public boolean isFirstAttempt() {
        return attemptNumber != null && attemptNumber == 1;
    }

    /**
     * Vérifie si c'est la dernière tentative autorisée
     */
    public boolean isLastAllowedAttempt(int maxAttempts) {
        return attemptNumber != null && attemptNumber >= maxAttempts;
    }

    /**
     * Vérifie si la soumission est terminée (état final)
     */
    public boolean isFinalState() {
        return status == SubmissionStatus.GRADED ||
                status == SubmissionStatus.ACCEPTED ||
                status == SubmissionStatus.REJECTED ||
                status == SubmissionStatus.MISSING ||
                status == SubmissionStatus.EXCUSED;
    }
}