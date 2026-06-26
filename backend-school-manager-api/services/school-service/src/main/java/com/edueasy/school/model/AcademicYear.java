package com.edueasy.school.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(
        name = "academic_years",
        indexes = {
                @Index(name = "idx_academic_year_school", columnList = "school_id"),
                @Index(name = "idx_academic_year_current", columnList = "is_current"),
                @Index(name = "idx_academic_year_dates", columnList = "start_date, end_date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "school_id", nullable = false)
    private String schoolId;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current")
    @Builder.Default
    private Boolean isCurrent = false;

    @Column(name = "is_closed")
    @Builder.Default
    private Boolean isClosed = false;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "semester_count")
    @Builder.Default
    private Integer semesterCount = 2;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "registration_start")
    private LocalDate registrationStart;

    @Column(name = "registration_end")
    private LocalDate registrationEnd;

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
        if (isCurrent == null) {
            isCurrent = false;
        }
        if (isClosed == null) {
            isClosed = false;
        }
        if (semesterCount == null) {
            semesterCount = 2;
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si l'année académique est actuelle
     */
    public boolean isCurrent() {
        return isCurrent != null && isCurrent;
    }

    /**
     * Vérifie si l'année académique est fermée
     */
    public boolean isClosed() {
        return isClosed != null && isClosed;
    }

    /**
     * Vérifie si l'année académique est en cours
     */
    public boolean isOngoing() {
        LocalDate today = LocalDate.now();
        return !isClosed()
                && startDate != null && endDate != null
                && !today.isBefore(startDate)
                && !today.isAfter(endDate);
    }

    /**
     * Vérifie si l'année académique est terminée
     */
    public boolean isFinished() {
        return endDate != null && LocalDate.now().isAfter(endDate);
    }

    /**
     * Vérifie si l'année académique est à venir
     */
    public boolean isUpcoming() {
        return startDate != null && LocalDate.now().isBefore(startDate);
    }

    /**
     * Vérifie si l'année académique est valide
     */
    public boolean isValid() {
        return schoolId != null && !schoolId.isEmpty()
                && name != null && !name.isEmpty()
                && startDate != null && endDate != null
                && !endDate.isBefore(startDate);
    }

    /**
     * Marque l'année académique comme actuelle
     */
    public void markAsCurrent() {
        this.isCurrent = true;
    }

    /**
     * Marque l'année académique comme non actuelle
     */
    public void markAsNotCurrent() {
        this.isCurrent = false;
    }

    /**
     * Ferme l'année académique
     */
    public void close() {
        this.isClosed = true;
        this.closedAt = LocalDateTime.now();
        this.isCurrent = false;
    }

    /**
     * Rouvre l'année académique
     */
    public void reopen() {
        this.isClosed = false;
        this.closedAt = null;
    }

    /**
     * Retourne la durée en jours
     */
    public long getDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Retourne la durée en mois
     */
    public long getDurationInMonths() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.MONTHS.between(startDate, endDate);
    }

    /**
     * Vérifie si une date est dans l'année académique
     */
    public boolean containsDate(LocalDate date) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Retourne le nombre de jours restants
     */
    public long getDaysRemaining() {
        if (endDate == null || isFinished()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    /**
     * Retourne le pourcentage de progression
     */
    public double getProgressPercentage() {
        if (startDate == null || endDate == null) {
            return 0.0;
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return 0.0;
        }
        if (today.isAfter(endDate)) {
            return 100.0;
        }
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        long elapsedDays = ChronoUnit.DAYS.between(startDate, today);
        return (double) elapsedDays / totalDays * 100.0;
    }

    /**
     * Vérifie si l'année académique est en période d'inscription
     */
    public boolean isRegistrationPeriod() {
        LocalDate today = LocalDate.now();
        return registrationStart != null && registrationEnd != null
                && !today.isBefore(registrationStart)
                && !today.isAfter(registrationEnd);
    }

    /**
     * Retourne le nom de l'année académique formaté
     */
    public String getDisplayName() {
        if (isCurrent()) {
            return name + " (Actuelle)";
        }
        if (isClosed()) {
            return name + " (Fermée)";
        }
        return name;
    }

    /**
     * Retourne le statut de l'année académique
     */
    public String getStatusLabel() {
        if (isClosed()) {
            return "Fermée";
        }
        if (isCurrent()) {
            return "Actuelle";
        }
        if (isOngoing()) {
            return "En cours";
        }
        if (isFinished()) {
            return "Terminée";
        }
        if (isUpcoming()) {
            return "À venir";
        }
        return "Non définie";
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (isClosed()) {
            return "gray";
        }
        if (isCurrent() || isOngoing()) {
            return "green";
        }
        if (isFinished()) {
            return "red";
        }
        if (isUpcoming()) {
            return "blue";
        }
        return "gray";
    }

    /**
     * Retourne le semestre actuel
     */
    public int getCurrentSemester() {
        if (!isOngoing() || semesterCount == null) {
            return 0;
        }
        long days = getDurationInDays();
        long elapsed = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        if (days == 0) {
            return 0;
        }
        return (int) Math.min(Math.ceil((double) elapsed / days * semesterCount), semesterCount);
    }

    /**
     * Vérifie si l'année académique a des inscriptions ouvertes
     */
    public boolean isRegistrationOpen() {
        return !isClosed() && isRegistrationPeriod();
    }
}