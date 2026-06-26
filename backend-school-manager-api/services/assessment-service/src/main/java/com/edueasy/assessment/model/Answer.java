package com.edueasy.assessment.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "submission_id", nullable = false)
    private String submissionId;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "points_earned")
    private Double pointsEarned;

    // Champs supplémentaires pour une gestion complète
    @Column(name = "max_points")
    private Double maxPoints;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Column(name = "is_flagged")
    @Builder.Default
    private Boolean isFlagged = false;

    @Column(name = "flag_reason")
    private String flagReason;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (answeredAt == null) {
            answeredAt = LocalDateTime.now();
        }
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si la réponse est correcte
     */
    public boolean isCorrect() {
        return isCorrect != null && isCorrect;
    }

    /**
     * Vérifie si la réponse est incorrecte
     */
    public boolean isIncorrect() {
        return isCorrect != null && !isCorrect;
    }

    /**
     * Vérifie si la réponse a été notée
     */
    public boolean isGraded() {
        return pointsEarned != null;
    }

    /**
     * Vérifie si la réponse est partiellement correcte
     */
    public boolean isPartiallyCorrect() {
        return isCorrect() && pointsEarned != null && maxPoints != null
                && pointsEarned > 0 && pointsEarned < maxPoints;
    }

    /**
     * Vérifie si la réponse est parfaitement correcte
     */
    public boolean isFullyCorrect() {
        return isCorrect() && pointsEarned != null && maxPoints != null
                && pointsEarned.equals(maxPoints);
    }

    /**
     * Vérifie si la réponse a un feedback
     */
    public boolean hasFeedback() {
        return feedback != null && !feedback.isEmpty();
    }

    /**
     * Vérifie si la réponse est flaggée
     */
    public boolean isFlagged() {
        return isFlagged != null && isFlagged;
    }

    /**
     * Marque la réponse comme correcte
     */
    public void markAsCorrect() {
        this.isCorrect = true;
    }

    /**
     * Marque la réponse comme incorrecte
     */
    public void markAsIncorrect() {
        this.isCorrect = false;
    }

    /**
     * Note la réponse
     */
    public void grade(double points) {
        this.pointsEarned = points;
        if (maxPoints != null && points >= maxPoints) {
            this.isCorrect = true;
        }
    }

    /**
     * Ajoute un feedback
     */
    public void addFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Flague la réponse
     */
    public void flag(String reason) {
        this.isFlagged = true;
        this.flagReason = reason;
    }

    /**
     * Désactive le flag
     */
    public void unflag() {
        this.isFlagged = false;
        this.flagReason = null;
    }

    /**
     * Calcule le pourcentage de points obtenus
     */
    public Double getScorePercentage() {
        if (maxPoints == null || maxPoints == 0 || pointsEarned == null) {
            return 0.0;
        }
        return (pointsEarned / maxPoints) * 100.0;
    }

    /**
     * Vérifie si la réponse est vide
     */
    public boolean isEmpty() {
        return answerText == null || answerText.trim().isEmpty();
    }

    /**
     * Vérifie si la réponse a un contenu
     */
    public boolean hasContent() {
        return !isEmpty();
    }

    /**
     * Retourne la réponse sous forme courte
     */
    public String getShortAnswer() {
        if (answerText == null) {
            return "No answer";
        }
        if (answerText.length() <= 50) {
            return answerText;
        }
        return answerText.substring(0, 47) + "...";
    }

    /**
     * Retourne le temps de réponse formaté
     */
    public String getFormattedTimeSpent() {
        if (timeSpentSeconds == null || timeSpentSeconds < 0) {
            return "N/A";
        }
        int minutes = timeSpentSeconds / 60;
        int seconds = timeSpentSeconds % 60;
        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }

    /**
     * Retourne l'état de la réponse
     */
    public String getStatus() {
        if (isCorrect()) {
            return "CORRECT";
        } else if (isIncorrect()) {
            return "INCORRECT";
        } else if (isGraded()) {
            return "PARTIAL";
        } else {
            return "PENDING";
        }
    }

    /**
     * Retourne l'affichage de la note
     */
    public String getGradeDisplay() {
        if (pointsEarned == null) {
            return "Not graded";
        }
        if (maxPoints == null) {
            return String.format("%.1f", pointsEarned);
        }
        return String.format("%.1f / %.1f", pointsEarned, maxPoints);
    }

    /**
     * Vérifie si la réponse est récente (moins de 5 minutes)
     */
    public boolean isRecent() {
        if (answeredAt == null) {
            return false;
        }
        return LocalDateTime.now().minusMinutes(5).isBefore(answeredAt);
    }
}