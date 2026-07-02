package com.edueasy.course.model;

import com.edueasy.common.enums.LevelStudent;
import com.edueasy.common.model.AuditTimestamps;
import com.edueasy.course.enums.CourseStatus;
import com.edueasy.course.enums.TypeCourses;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "courses",
        indexes = {
                @Index(name = "idx_course_status", columnList = "status"),
                @Index(name = "idx_course_teacher", columnList = "teacher_uuid"),
                @Index(name = "idx_course_school", columnList = "school_id"),
                @Index(name = "idx_course_class", columnList = "class_id"),
                @Index(name = "idx_course_subject", columnList = "subject"),
                @Index(name = "idx_course_dates", columnList = "start_date, end_date"),
                @Index(name = "idx_course_type", columnList = "type_courses"),
                @Index(name = "idx_course_level", columnList = "level_student")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Embedded
    private AuditTimestamps timestamps;

    @Column(nullable = false)
    private String title;  // ← Plus d'annotation @Enumerated ici !

    @Column(length = 2000)
    private String description;

    @Column(name = "school_id")
    private String schoolId;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "teacher_uuid")
    private String teacherUuid;

    private String subject;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_courses")
    private TypeCourses typeCourses;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "duration_hours")
    @Builder.Default
    private Integer durationHours = 0;

    @Builder.Default
    private Integer credits = 0;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_student")
    private LevelStudent levelStudent;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = false;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "language")
    @Builder.Default
    private String language = "fr";

    @Column(name = "difficulty_level")
    private String difficultyLevel;

    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites;

    @Column(name = "learning_objectives", columnDefinition = "TEXT")
    private String learningObjectives;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "enrollment_count")
    @Builder.Default
    private Integer enrollmentCount = 0;

    @Column(name = "rating_average")
    @Builder.Default
    private Double ratingAverage = 0.0;

    @Column(name = "rating_count")
    @Builder.Default
    private Integer ratingCount = 0;

    @Column(name = "tags")
    private String tags;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Content> contents = new ArrayList<>();

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (durationHours == null) {
            durationHours = 0;
        }
        if (credits == null) {
            credits = 0;
        }
        if (isPublished == null) {
            isPublished = false;
        }
        if (isFeatured == null) {
            isFeatured = false;
        }
        if (language == null) {
            language = "fr";
        }
        if (enrollmentCount == null) {
            enrollmentCount = 0;
        }
        if (ratingAverage == null) {
            ratingAverage = 0.0;
        }
        if (ratingCount == null) {
            ratingCount = 0;
        }
        if (contents == null) {
            contents = new ArrayList<>();
        }
        if (status == null) {
            status = CourseStatus.DRAFT;
        }
        if (timestamps == null) {
            timestamps = new AuditTimestamps();
            timestamps.setCreatedAt(LocalDateTime.now());
            timestamps.setUpdatedAt(LocalDateTime.now());
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (timestamps != null) {
            timestamps.setUpdatedAt(LocalDateTime.now());
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si le cours est publié
     */
    public boolean isPublished() {
        return isPublished != null && isPublished;
    }

    /**
     * Vérifie si le cours est en vedette
     */
    public boolean isFeatured() {
        return isFeatured != null && isFeatured;
    }

    /**
     * Vérifie si le cours est actif
     */
    public boolean isActive() {
        return status == CourseStatus.ACTIVE;
    }

    /**
     * Vérifie si le cours est en brouillon
     */
    public boolean isDraft() {
        return status == CourseStatus.DRAFT;
    }

    /**
     * Vérifie si le cours est archivé
     */
    public boolean isArchived() {
        return status == CourseStatus.ARCHIVED;
    }

    /**
     * Vérifie si le cours est terminé
     */
    public boolean isCompleted() {
        return status == CourseStatus.COMPLETED;
    }

    /**
     * Vérifie si le cours est en progression
     */
    public boolean isInProgress() {
        return status == CourseStatus.IN_PROGRESS;
    }

    /**
     * Vérifie si le cours a commencé
     */
    public boolean hasStarted() {
        return startDate != null && LocalDateTime.now().isAfter(startDate);
    }

    /**
     * Vérifie si le cours est terminé
     */
    public boolean isEnded() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    /**
     * Vérifie si le cours est en cours
     */
    public boolean isOngoing() {
        return hasStarted() && !isEnded();
    }

    /**
     * Vérifie si le cours a une description
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Vérifie si le cours a une vignette
     */
    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.isEmpty();
    }

    /**
     * Vérifie si le cours a des prérequis
     */
    public boolean hasPrerequisites() {
        return prerequisites != null && !prerequisites.isEmpty();
    }

    /**
     * Vérifie si le cours a des objectifs d'apprentissage
     */
    public boolean hasLearningObjectives() {
        return learningObjectives != null && !learningObjectives.isEmpty();
    }

    /**
     * Vérifie si le cours a des contenus
     */
    public boolean hasContents() {
        return contents != null && !contents.isEmpty();
    }

    /**
     * Retourne le nombre de contenus
     */
    public int getContentsCount() {
        return contents != null ? contents.size() : 0;
    }

    /**
     * Ajoute un contenu au cours
     */
    public void addContent(Content content) {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        if (!contents.contains(content)) {
            contents.add(content);
            content.setCourse(this);
            content.setCourseUuid(this.uuid);
        }
    }

    /**
     * Supprime un contenu du cours
     */
    public void removeContent(Content content) {
        if (contents != null) {
            contents.remove(content);
            content.setCourse(null);
            content.setCourseUuid(null);
        }
    }

    /**
     * Incrémente le nombre d'inscriptions
     */
    public void incrementEnrollment() {
        if (enrollmentCount == null) {
            enrollmentCount = 0;
        }
        enrollmentCount++;
    }

    /**
     * Décrémente le nombre d'inscriptions
     */
    public void decrementEnrollment() {
        if (enrollmentCount != null && enrollmentCount > 0) {
            enrollmentCount--;
        }
    }

    /**
     * Ajoute une note
     */
    public void addRating(double rating) {
        if (ratingAverage == null) {
            ratingAverage = 0.0;
        }
        if (ratingCount == null) {
            ratingCount = 0;
        }
        double total = ratingAverage * ratingCount;
        ratingCount++;
        ratingAverage = (total + rating) / ratingCount;
    }

    /**
     * Publie le cours
     */
    public void publish() {
        this.isPublished = true;
        if (status == CourseStatus.DRAFT) {
            this.status = CourseStatus.ACTIVE;
        }
    }

    /**
     * Dépublie le cours
     */
    public void unpublish() {
        this.isPublished = false;
    }

    /**
     * Archive le cours
     */
    public void archive() {
        this.status = CourseStatus.ARCHIVED;
        this.isPublished = false;
    }

    /**
     * Réactive le cours
     */
    public void restore() {
        this.status = CourseStatus.ACTIVE;
        this.isPublished = true;
    }

    /**
     * Met en vedette le cours
     */
    public void feature() {
        this.isFeatured = true;
    }

    /**
     * Retire la vedette
     */
    public void unfeature() {
        this.isFeatured = false;
    }

    /**
     * Vérifie si le cours est complet (a tous les éléments essentiels)
     */
    public boolean isComplete() {
        return title != null && !title.isEmpty()
                && description != null && !description.isEmpty()
                && teacherUuid != null && !teacherUuid.isEmpty()
                && status != null
                && typeCourses != null
                && levelStudent != null;
    }

    /**
     * Retourne la durée formatée
     */
    public String getFormattedDuration() {
        if (durationHours == null || durationHours == 0) {
            return "N/A";
        }
        if (durationHours < 24) {
            return durationHours + " heure" + (durationHours > 1 ? "s" : "");
        }
        int days = durationHours / 24;
        int hours = durationHours % 24;
        if (hours == 0) {
            return days + " jour" + (days > 1 ? "s" : "");
        }
        return days + "j " + hours + "h";
    }

    /**
     * Retourne le statut du cours en français
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case DRAFT -> "Brouillon";
            case ACTIVE -> "Actif";
            case IN_PROGRESS -> "En cours";
            case COMPLETED -> "Terminé";
            case ARCHIVED -> "Archivé";
            case PUBLISHED -> "Publié";
        };
    }

    /**
     * Retourne la couleur du statut
     */
    public String getStatusColor() {
        if (status == null) {
            return "gray";
        }
        return switch (status) {
            case DRAFT -> "gray";
            case ACTIVE -> "green";
            case IN_PROGRESS -> "blue";
            case COMPLETED -> "purple";
            case ARCHIVED -> "orange";
            case PUBLISHED -> "teal";
        };
    }

    /**
     * Retourne le type de cours en français
     */
    public String getTypeLabel() {
        if (typeCourses == null) {
            return "Non défini";
        }
        return switch (typeCourses) {
            case THEORIQUE -> "Théorique";
            case PRATIQUE -> "Pratique";
            case MIXTE -> "Mixte";
            case ATELIER -> "Atelier";
          //  case LABORATOIRE -> "Laboratoire";
         //   case EN_LIGNE -> "En ligne";
        };
    }

    /**
     * Retourne le niveau de l'étudiant en français
     */
    public String getLevelLabel() {
        if (levelStudent == null) {
            return "Non défini";
        }
        return levelStudent.getLabel();
    }

    /**
     * Vérifie si le cours est complet (tous les contenus sont présents)
     */
    public boolean isFullyLoaded() {
        return hasContents() && contents.stream().allMatch(Content::isComplete);
    }

    /**
     * Calcule le pourcentage de progression du cours
     */
    public double getCompletionPercentage(int completedContents) {
        int total = getContentsCount();
        if (total == 0) {
            return 0.0;
        }
        return Math.min((double) completedContents / total * 100.0, 100.0);
    }

    /**
     * Vérifie si le cours est accessible aux débutants
     */
    public boolean isBeginnerFriendly() {
        return levelStudent != null && levelStudent.isBeginnerFriendly();
    }

    /**
     * Vérifie si le cours est avancé
     */
    public boolean isAdvanced() {
        return levelStudent != null && levelStudent.isAdvanced();
    }

    /**
     * Vérifie si le cours a des places disponibles
     */
    public boolean hasAvailableSeats() {
        if (maxStudents == null) {
            return true;
        }
        int enrolled = enrollmentCount != null ? enrollmentCount : 0;
        return enrolled < maxStudents;
    }
}