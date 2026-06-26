package com.edueasy.school.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "classes",
        indexes = {
                @Index(name = "idx_class_school", columnList = "school_id"),
                @Index(name = "idx_class_name", columnList = "name"),
                @Index(name = "idx_class_level", columnList = "level"),
                @Index(name = "idx_class_academic_year", columnList = "academic_year"),
                @Index(name = "idx_class_teacher", columnList = "main_teacher_id"),
                @Index(name = "idx_class_active", columnList = "active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "school_id", nullable = false)
    private String schoolId;

    @Column(nullable = false)
    private String name;

    private String level;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "max_students")
    @Builder.Default
    private Integer maxStudents = 30;

    @Column(name = "current_students")
    @Builder.Default
    private Integer currentStudents = 0;

    @Column(name = "main_teacher_id")
    private String mainTeacherId;

    private String room;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> subjectsTeachers = new HashMap<>();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "section")
    private String section;

    @Column(name = "academic_year_id")
    private String academicYearId;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_full")
    @Builder.Default
    private Boolean isFull = false;

    @Column(name = "class_code", unique = true)
    private String classCode;

    @Column(name = "description", length = 500)
    private String description;

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
        if (maxStudents == null) {
            maxStudents = 30;
        }
        if (currentStudents == null) {
            currentStudents = 0;
        }
        if (active == null) {
            active = true;
        }
        if (isFull == null) {
            isFull = false;
        }
        if (subjectsTeachers == null) {
            subjectsTeachers = new HashMap<>();
        }
        updateFullStatus();
        generateClassCode();
    }

    @PreUpdate
    protected void onUpdate() {
        updateFullStatus();
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si la classe est active
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Vérifie si la classe est pleine
     */
    public boolean isFull() {
        return isFull != null && isFull;
    }

    /**
     * Met à jour le statut de la classe (pleine ou non)
     */
    public void updateFullStatus() {
        if (maxStudents != null && currentStudents != null) {
            this.isFull = currentStudents >= maxStudents;
        }
    }

    /**
     * Génère le code de la classe
     */
    public void generateClassCode() {
        if (classCode == null && name != null && schoolId != null) {
            String prefix = schoolId.length() > 3 ? schoolId.substring(0, 3).toUpperCase() : schoolId.toUpperCase();
            String namePart = name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            this.classCode = prefix + "-" + namePart;
        }
    }

    /**
     * Ajoute un étudiant à la classe
     */
    public boolean addStudent() {
        if (isFull()) {
            return false;
        }
        if (currentStudents == null) {
            currentStudents = 0;
        }
        currentStudents++;
        updateFullStatus();
        return true;
    }

    /**
     * Retire un étudiant de la classe
     */
    public boolean removeStudent() {
        if (currentStudents == null || currentStudents <= 0) {
            return false;
        }
        currentStudents--;
        updateFullStatus();
        return true;
    }

    /**
     * Vérifie si la classe a de la place
     */
    public boolean hasAvailableSpace() {
        return !isFull() && maxStudents != null && currentStudents != null
                && currentStudents < maxStudents;
    }

    /**
     * Retourne le nombre de places disponibles
     */
    public int getAvailableSpaces() {
        if (maxStudents == null || currentStudents == null) {
            return 0;
        }
        return Math.max(0, maxStudents - currentStudents);
    }

    /**
     * Vérifie si la classe a un enseignant principal
     */
    public boolean hasMainTeacher() {
        return mainTeacherId != null && !mainTeacherId.isEmpty();
    }

    /**
     * Vérifie si la classe a des matières
     */
    public boolean hasSubjects() {
        return subjectsTeachers != null && !subjectsTeachers.isEmpty();
    }

    /**
     * Ajoute une matière avec son enseignant
     */
    public void addSubjectTeacher(String subject, String teacherId) {
        if (subjectsTeachers == null) {
            subjectsTeachers = new HashMap<>();
        }
        subjectsTeachers.put(subject, teacherId);
    }

    /**
     * Supprime une matière
     */
    public void removeSubject(String subject) {
        if (subjectsTeachers != null) {
            subjectsTeachers.remove(subject);
        }
    }

    /**
     * Vérifie si une matière est enseignée dans la classe
     */
    public boolean hasSubject(String subject) {
        return subjectsTeachers != null && subjectsTeachers.containsKey(subject);
    }

    /**
     * Active la classe
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Désactive la classe
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Retourne le nom complet de la classe
     */
    public String getFullName() {
        if (level != null && !level.isEmpty()) {
            return level + " - " + name;
        }
        return name;
    }

    /**
     * Retourne le statut de la classe
     */
    public String getStatusLabel() {
        if (!isActive()) {
            return "Inactive";
        }
        if (isFull()) {
            return "Pleine";
        }
        if (currentStudents == null || currentStudents == 0) {
            return "Vide";
        }
        return "Active";
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (!isActive()) {
            return "gray";
        }
        if (isFull()) {
            return "red";
        }
        if (currentStudents == null || currentStudents == 0) {
            return "orange";
        }
        return "green";
    }

    /**
     * Vérifie si la classe est valide
     */
    public boolean isValid() {
        return schoolId != null && !schoolId.isEmpty()
                && name != null && !name.isEmpty()
                && maxStudents != null && maxStudents > 0;
    }

    /**
     * Retourne le taux d'occupation de la classe
     */
    public double getOccupancyRate() {
        if (maxStudents == null || currentStudents == null || maxStudents == 0) {
            return 0.0;
        }
        return (double) currentStudents / maxStudents * 100.0;
    }

    /**
     * Met à jour la capacité maximale
     */
    public void updateCapacity(int newCapacity) {
        if (newCapacity > 0) {
            this.maxStudents = newCapacity;
            updateFullStatus();
        }
    }

    /**
     * Vérifie si la classe est surchargée (plus de 90% de capacité)
     */
    public boolean isOvercrowded() {
        return getOccupancyRate() >= 90.0;
    }
}