package com.edueasy.assessment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private AssessmentType type;

    @Builder.Default
    private Double maxScore = 20.0;

    @Builder.Default
    private Double passingScore = 10.0;

    @Builder.Default
    private Double coefficient = 1.0;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String questions;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();

    @Builder.Default
    private Boolean isPublished = false;

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
        if (isPublished == null) {
            isPublished = false;
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si l'évaluation est publiée
     */
    public boolean isPublished() {
        return isPublished != null && isPublished;
    }

    /**
     * Vérifie si l'évaluation est en cours
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return startDate != null && endDate != null
                && now.isAfter(startDate)
                && now.isBefore(endDate);
    }

    /**
     * Vérifie si l'évaluation est terminée
     */
    public boolean isFinished() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    /**
     * Vérifie si l'évaluation est à venir
     */
    public boolean isUpcoming() {
        return startDate != null && LocalDateTime.now().isBefore(startDate);
    }

    /**
     * Vérifie si l'évaluation a commencé
     */
    public boolean hasStarted() {
        return startDate != null && LocalDateTime.now().isAfter(startDate);
    }

    /**
     * Vérifie si l'évaluation a une description
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Vérifie si l'évaluation a des questions
     */
    public boolean hasQuestions() {
        return questions != null && !questions.isEmpty();
    }

    /**
     * Vérifie si l'évaluation a des soumissions
     */
    public boolean hasSubmissions() {
        return submissions != null && !submissions.isEmpty();
    }

    /**
     * Retourne le nombre de soumissions
     */
    public int getSubmissionsCount() {
        return submissions != null ? submissions.size() : 0;
    }

    /**
     * Vérifie si la note est valide
     */
    public boolean isValidScore() {
        return maxScore != null && maxScore > 0;
    }

    /**
     * Vérifie si la note de passage est valide
     */
    public boolean isValidPassingScore() {
        return passingScore != null && maxScore != null
                && passingScore >= 0 && passingScore <= maxScore;
    }

    /**
     * Publie l'évaluation
     */
    public void publish() {
        this.isPublished = true;
    }

    /**
     * Dépublie l'évaluation
     */
    public void unpublish() {
        this.isPublished = false;
    }

    /**
     * Ajoute une soumission
     */
    public void addSubmission(Submission submission) {
        if (submissions == null) {
            submissions = new ArrayList<>();
        }
        if (!submissions.contains(submission)) {
            submissions.add(submission);
            submission.setAssessment(this);
        }
    }

    /**
     * Supprime une soumission
     */
    public void removeSubmission(Submission submission) {
        if (submissions != null) {
            submissions.remove(submission);
            submission.setAssessment(null);
        }
    }

    /**
     * Vérifie si l'évaluation est un examen
     */
    public boolean isExam() {
        return type == AssessmentType.EXAM;
    }

    /**
     * Vérifie si l'évaluation est un quiz
     */
    public boolean isQuiz() {
        return type == AssessmentType.QUIZ;
    }

    /**
     * Vérifie si l'évaluation est un devoir
     */
    public boolean isAssignment() {
        return type == AssessmentType.ASSIGNMENT;
    }

    /**
     * Vérifie si l'évaluation est un projet
     */
    public boolean isProject() {
        return type == AssessmentType.PROJECT;
    }

    /**
     * Calcule la durée totale en minutes
     */
    public Integer getTotalDurationMinutes() {
        if (durationMinutes != null) {
            return durationMinutes;
        }
        if (startDate != null && endDate != null) {
            return (int) java.time.Duration.between(startDate, endDate).toMinutes();
        }
        return 0;
    }

    /**
     * Retourne la progression de l'évaluation en pourcentage
     */
    public Double getProgress() {
        if (startDate == null || endDate == null) {
            return 0.0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startDate)) {
            return 0.0;
        }
        if (now.isAfter(endDate)) {
            return 100.0;
        }
        long totalDuration = java.time.Duration.between(startDate, endDate).toMillis();
        long elapsed = java.time.Duration.between(startDate, now).toMillis();
        return (double) elapsed / totalDuration * 100.0;
    }

    /**
     * Vérifie si l'évaluation est accessible
     */
    public boolean isAccessible() {
        return isPublished() && (startDate == null || LocalDateTime.now().isAfter(startDate));
    }
}