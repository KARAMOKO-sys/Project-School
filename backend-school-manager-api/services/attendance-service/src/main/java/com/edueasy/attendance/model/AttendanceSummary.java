package com.edueasy.attendance.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "attendance_summaries",
        indexes = {
                @Index(name = "idx_summary_student", columnList = "student_id"),
                @Index(name = "idx_summary_class", columnList = "class_id"),
                @Index(name = "idx_summary_academic_year", columnList = "academic_year"),
                @Index(name = "idx_summary_period", columnList = "period")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "academic_year")
    private String academicYear;

    private String period;

    @Builder.Default
    private Integer totalDays = 0;

    @Builder.Default
    private Integer presentDays = 0;

    @Builder.Default
    private Integer absentDays = 0;

    @Builder.Default
    private Integer lateDays = 0;

    @Builder.Default
    private Integer excusedDays = 0;

    @Builder.Default
    private Integer unexcusedAbsences = 0;

    @Builder.Default
    private Integer halfDayAbsences = 0;

    private Double attendanceRate;

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
        initializeDefaults();
        calculateRate();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateRate();
    }

    // ===== Méthodes métier =====

    /**
     * Initialise les valeurs par défaut
     */
    private void initializeDefaults() {
        if (totalDays == null) totalDays = 0;
        if (presentDays == null) presentDays = 0;
        if (absentDays == null) absentDays = 0;
        if (lateDays == null) lateDays = 0;
        if (excusedDays == null) excusedDays = 0;
        if (unexcusedAbsences == null) unexcusedAbsences = 0;
        if (halfDayAbsences == null) halfDayAbsences = 0;
    }

    /**
     * Calcule le taux de présence
     */
    public void calculateRate() {
        if (totalDays != null && totalDays > 0 && presentDays != null) {
            this.attendanceRate = (double) presentDays / (double) totalDays * 100.0;
        } else {
            this.attendanceRate = 0.0;
        }
    }

    /**
     * Met à jour le résumé avec une nouvelle présence
     */
    public void addAttendance(Attendance attendance) {
        if (attendance == null) {
            return;
        }

        this.totalDays = (totalDays != null ? totalDays : 0) + 1;

        switch (attendance.getStatus()) {
            case PRESENT -> this.presentDays = (presentDays != null ? presentDays : 0) + 1;
            case ABSENT -> {
                this.absentDays = (absentDays != null ? absentDays : 0) + 1;
                this.unexcusedAbsences = (unexcusedAbsences != null ? unexcusedAbsences : 0) + 1;
            }
            case LATE -> this.lateDays = (lateDays != null ? lateDays : 0) + 1;
            case EXCUSED -> {
                this.excusedDays = (excusedDays != null ? excusedDays : 0) + 1;
                // L'absence excusée n'est pas comptée comme une absence non excusée
            }
            case UNJUSTIFIED -> {
                this.absentDays = (absentDays != null ? absentDays : 0) + 1;
                this.unexcusedAbsences = (unexcusedAbsences != null ? unexcusedAbsences : 0) + 1;
            }
        }

        calculateRate();
    }

    /**
     * Ajoute une demi-journée d'absence
     */
    public void addHalfDayAbsence() {
        this.halfDayAbsences = (halfDayAbsences != null ? halfDayAbsences : 0) + 1;
        this.totalDays = (totalDays != null ? totalDays : 0) + 1;
        // Une demi-journée compte comme une demi-présence pour le calcul
        calculateRate();
    }

    /**
     * Met à jour le résumé avec plusieurs présences
     */
    public void updateSummary(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return;
        }

        resetCounts();
        attendances.forEach(this::addAttendance);
        calculateRate();
    }

    /**
     * Réinitialise tous les compteurs
     */
    private void resetCounts() {
        this.totalDays = 0;
        this.presentDays = 0;
        this.absentDays = 0;
        this.lateDays = 0;
        this.excusedDays = 0;
        this.unexcusedAbsences = 0;
        this.halfDayAbsences = 0;
        this.attendanceRate = 0.0;
    }

    /**
     * Vérifie si le taux de présence est satisfaisant (>= 75%)
     */
    public boolean isSatisfactory() {
        return attendanceRate != null && attendanceRate >= 75.0;
    }

    /**
     * Vérifie si le taux de présence est excellent (>= 95%)
     */
    public boolean isExcellent() {
        return attendanceRate != null && attendanceRate >= 95.0;
    }

    /**
     * Vérifie si le taux de présence est faible (< 60%)
     */
    public boolean isLow() {
        return attendanceRate != null && attendanceRate < 60.0;
    }

    /**
     * Calcule le nombre de jours manquants
     */
    public Integer getMissingDays() {
        if (totalDays == null || presentDays == null) {
            return 0;
        }
        return totalDays - presentDays;
    }

    /**
     * Calcule le nombre de jours non excusés
     */
    public Integer getUnexcusedAbsences() {
        if (unexcusedAbsences == null) {
            return 0;
        }
        return unexcusedAbsences;
    }

    /**
     * Calcule le nombre total d'absences (excusées + non excusées)
     */
    public Integer getTotalAbsences() {
        Integer abs = absentDays != null ? absentDays : 0;
        Integer exc = excusedDays != null ? excusedDays : 0;
        return abs + exc;
    }

    /**
     * Retourne le taux de présence en pourcentage formaté
     */
    public String getFormattedRate() {
        if (attendanceRate == null) {
            return "N/A";
        }
        return String.format("%.1f%%", attendanceRate);
    }

    /**
     * Retourne le statut du taux de présence
     */
    public String getRateStatus() {
        if (attendanceRate == null) {
            return "N/A";
        }
        if (attendanceRate >= 95) return "EXCELLENT";
        if (attendanceRate >= 75) return "SATISFAISANT";
        if (attendanceRate >= 60) return "MOYEN";
        return "INSUFFISANT";
    }

    /**
     * Retourne le nombre de jours total formaté
     */
    public String getSummaryInfo() {
        return String.format("%d jours (%d présents, %d absents, %d retards)",
                totalDays != null ? totalDays : 0,
                presentDays != null ? presentDays : 0,
                absentDays != null ? absentDays : 0,
                lateDays != null ? lateDays : 0);
    }

    /**
     * Vérifie si le résumé est complet
     */
    public boolean isComplete() {
        return studentId != null && !studentId.isEmpty()
                && totalDays != null && totalDays > 0
                && attendanceRate != null;
    }

    /**
     * Calcule le taux de présence pour une période
     */
    public static double calculateRateForPeriod(List<AttendanceSummary> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            return 0.0;
        }
        int totalTotalDays = summaries.stream()
                .mapToInt(s -> s.getTotalDays() != null ? s.getTotalDays() : 0)
                .sum();
        int totalPresentDays = summaries.stream()
                .mapToInt(s -> s.getPresentDays() != null ? s.getPresentDays() : 0)
                .sum();
        if (totalTotalDays == 0) {
            return 0.0;
        }
        return (double) totalPresentDays / totalTotalDays * 100.0;
    }

    /**
     * Agrège plusieurs résumés en un seul
     */
    public static AttendanceSummary aggregate(List<AttendanceSummary> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            return null;
        }

        AttendanceSummary result = AttendanceSummary.builder()
                .studentId(summaries.get(0).getStudentId())
                .classId(summaries.get(0).getClassId())
                .academicYear(summaries.get(0).getAcademicYear())
                .period("AGGREGATED")
                .build();

        summaries.forEach(summary -> {
            result.setTotalDays(result.getTotalDays() + (summary.getTotalDays() != null ? summary.getTotalDays() : 0));
            result.setPresentDays(result.getPresentDays() + (summary.getPresentDays() != null ? summary.getPresentDays() : 0));
            result.setAbsentDays(result.getAbsentDays() + (summary.getAbsentDays() != null ? summary.getAbsentDays() : 0));
            result.setLateDays(result.getLateDays() + (summary.getLateDays() != null ? summary.getLateDays() : 0));
            result.setExcusedDays(result.getExcusedDays() + (summary.getExcusedDays() != null ? summary.getExcusedDays() : 0));
        });

        result.calculateRate();
        return result;
    }
}