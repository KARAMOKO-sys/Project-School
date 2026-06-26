package com.edueasy.assessment.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "grades",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "assessment_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    private Double score;

    private Double percentage;

    @Column(name = "letter_grade")
    private String letterGrade;

    @Column(length = 500)
    private String comment;

    @Column(name = "graded_by")
    private String gradedBy;

    @CreationTimestamp
    @Column(name = "graded_at", updatable = false)
    private LocalDateTime gradedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (gradedAt == null) {
            gradedAt = LocalDateTime.now();
        }
        calculatePercentage();
        calculateLetterGrade();
    }

    @PreUpdate
    protected void onUpdate() {
        calculatePercentage();
        calculateLetterGrade();
    }

    // ===== Méthodes métier =====

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
     * Calcule la note en lettre à partir du pourcentage
     */
    public void calculateLetterGrade() {
        if (percentage == null) {
            this.letterGrade = "N/A";
            return;
        }

        if (percentage >= 90) {
            this.letterGrade = "A";
        } else if (percentage >= 80) {
            this.letterGrade = "B";
        } else if (percentage >= 70) {
            this.letterGrade = "C";
        } else if (percentage >= 60) {
            this.letterGrade = "D";
        } else {
            this.letterGrade = "F";
        }
    }

    /**
     * Calcule la note en lettre avec système personnalisé
     */
    public void calculateLetterGradeWithCustomScale(GradeScale scale) {
        if (percentage == null || scale == null) {
            this.letterGrade = "N/A";
            return;
        }

        for (GradeScale.GradeThreshold threshold : scale.getThresholds()) {
            if (percentage >= threshold.getMinPercentage()) {
                this.letterGrade = threshold.getLetterGrade();
                return;
            }
        }
        this.letterGrade = "F";
    }

    /**
     * Vérifie si la note est une note de passage
     */
    public boolean isPassing() {
        if (assessment == null || assessment.getPassingScore() == null) {
            return percentage != null && percentage >= 60;
        }
        return score != null && score >= assessment.getPassingScore();
    }

    /**
     * Vérifie si l'étudiant a réussi
     */
    public boolean isPassed() {
        return isPassing();
    }

    /**
     * Vérifie si l'étudiant a échoué
     */
    public boolean isFailed() {
        return !isPassing();
    }

    /**
     * Vérifie si la note est excellente (> 90%)
     */
    public boolean isExcellent() {
        return percentage != null && percentage >= 90;
    }

    /**
     * Vérifie si la note est bonne (> 80%)
     */
    public boolean isGood() {
        return percentage != null && percentage >= 80 && percentage < 90;
    }

    /**
     * Vérifie si la note est moyenne (70-80%)
     */
    public boolean isAverage() {
        return percentage != null && percentage >= 70 && percentage < 80;
    }

    /**
     * Vérifie si la note est faible (< 60%)
     */
    public boolean isPoor() {
        return percentage != null && percentage < 60;
    }

    /**
     * Vérifie si la note a un commentaire
     */
    public boolean hasComment() {
        return comment != null && !comment.isEmpty();
    }

    /**
     * Vérifie si la note a été attribuée
     */
    public boolean isGraded() {
        return score != null && gradedBy != null && gradedAt != null;
    }

    /**
     * Met à jour la note
     */
    public void updateGrade(Double newScore, String gradedBy) {
        this.score = newScore;
        this.gradedBy = gradedBy;
        this.gradedAt = LocalDateTime.now();
        calculatePercentage();
        calculateLetterGrade();
    }

    /**
     * Ajoute un commentaire à la note
     */
    public void addComment(String comment) {
        this.comment = comment;
    }

    /**
     * Retourne le statut de la note
     */
    public String getStatus() {
        if (!isGraded()) {
            return "NOT_GRADED";
        }
        return isPassed() ? "PASSED" : "FAILED";
    }

    /**
     * Retourne le libellé de la note
     */
    public String getGradeLabel() {
        if (percentage == null) {
            return "Non noté";
        }
        if (isExcellent()) {
            return "Excellent";
        } else if (isGood()) {
            return "Bien";
        } else if (isAverage()) {
            return "Moyen";
        } else if (isPoor()) {
            return "Insuffisant";
        }
        return "N/A";
    }

    /**
     * Retourne le score formaté
     */
    public String getFormattedScore() {
        if (score == null) {
            return "N/A";
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
     * Vérifie si l'UUID est valide
     */
    public boolean hasValidUuid() {
        return uuid != null && !uuid.isEmpty();
    }

    /**
     * Vérifie si la note a été attribuée par un enseignant
     */
    public boolean isGradedByTeacher(String teacherId) {
        return gradedBy != null && gradedBy.equals(teacherId);
    }
}