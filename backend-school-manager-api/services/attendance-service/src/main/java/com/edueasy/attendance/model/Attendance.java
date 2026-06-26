package com.edueasy.attendance.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "date", "time_slot"})
        },
        indexes = {
                @Index(name = "idx_attendance_student_date", columnList = "student_id, date"),
                @Index(name = "idx_attendance_class_date", columnList = "class_id, date"),
                @Index(name = "idx_attendance_course_date", columnList = "course_id, date"),
                @Index(name = "idx_attendance_teacher_date", columnList = "teacher_id, date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "time_slot")
    private String timeSlot;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    @Column(name = "late_minutes")
    @Builder.Default
    private Integer lateMinutes = 0;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Column(name = "justified_by")
    private String justifiedBy;

    @Column(name = "justified_at")
    private LocalDateTime justifiedAt;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "is_online")
    @Builder.Default
    private Boolean isOnline = false;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

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
        if (status == null) {
            status = AttendanceStatus.PRESENT;
        }
        if (lateMinutes == null) {
            lateMinutes = 0;
        }
        if (isOnline == null) {
            isOnline = false;
        }
        if (checkInTime == null && date != null) {
            checkInTime = date.atStartOfDay();
        }
        calculateLateMinutes();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateLateMinutes();
    }

    // ===== Méthodes métier =====

    /**
     * Calcule les minutes de retard en fonction de l'heure d'arrivée
     */
    public void calculateLateMinutes() {
        if (checkInTime != null && date != null) {
            LocalDateTime startTime = date.atTime(8, 0); // Heure de début par défaut
            if (checkInTime.isAfter(startTime)) {
                this.lateMinutes = (int) ChronoUnit.MINUTES.between(startTime, checkInTime);
                if (this.lateMinutes > 0 && status != AttendanceStatus.ABSENT) {
                    this.status = AttendanceStatus.LATE;
                }
            } else {
                this.lateMinutes = 0;
            }
        }
    }

    /**
     * Vérifie si l'étudiant est présent
     */
    public boolean isPresent() {
        return status == AttendanceStatus.PRESENT;
    }

    /**
     * Vérifie si l'étudiant est absent
     */
    public boolean isAbsent() {
        return status == AttendanceStatus.ABSENT;
    }

    /**
     * Vérifie si l'étudiant est en retard
     */
    public boolean isLate() {
        return status == AttendanceStatus.LATE;
    }

    /**
     * Vérifie si l'absence est excusée
     */
    public boolean isExcused() {
        return status == AttendanceStatus.EXCUSED;
    }

    /**
     * Vérifie si l'absence est injustifiée
     */
    public boolean isUnjustified() {
        return status == AttendanceStatus.UNJUSTIFIED;
    }

    /**
     * Vérifie si c'est un jour férié
     */
    public boolean isHoliday() {
        return status == AttendanceStatus.HOLIDAY;
    }

    /**
     * Vérifie si c'est un congé maladie
     */
    public boolean isSickLeave() {
        return status == AttendanceStatus.SICK_LEAVE;
    }

    /**
     * Vérifie si la présence est justifiée
     */
    public boolean isJustified() {
        return justification != null && !justification.isEmpty();
    }

    /**
     * Vérifie si l'étudiant était présent (incluant retard)
     */
    public boolean wasPresent() {
        return status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE;
    }

    /**
     * Vérifie si l'absence est valide
     */
    public boolean isAbsenceValid() {
        return status == AttendanceStatus.ABSENT ||
                status == AttendanceStatus.UNJUSTIFIED ||
                status == AttendanceStatus.EXCUSED ||
                status == AttendanceStatus.SICK_LEAVE;
    }

    /**
     * Marque l'étudiant comme présent
     */
    public void markPresent() {
        this.status = AttendanceStatus.PRESENT;
        this.lateMinutes = 0;
        this.checkInTime = LocalDateTime.now();
    }

    /**
     * Marque l'étudiant comme présent avec heure d'arrivée
     */
    public void markPresentWithTime(LocalDateTime checkInTime) {
        this.status = AttendanceStatus.PRESENT;
        this.checkInTime = checkInTime;
        calculateLateMinutes();
    }

    /**
     * Marque l'étudiant comme absent
     */
    public void markAbsent() {
        this.status = AttendanceStatus.ABSENT;
        this.lateMinutes = 0;
        this.checkInTime = null;
        this.checkOutTime = null;
    }

    /**
     * Marque l'étudiant comme en retard
     */
    public void markLate(int minutes) {
        this.status = AttendanceStatus.LATE;
        this.lateMinutes = minutes;
        this.checkInTime = LocalDateTime.now();
    }

    /**
     * Marque l'étudiant comme en retard avec heure d'arrivée
     */
    public void markLateWithTime(LocalDateTime checkInTime, int expectedStartHour) {
        this.checkInTime = checkInTime;
        LocalDateTime startTime = date.atTime(expectedStartHour, 0);
        if (checkInTime.isAfter(startTime)) {
            this.lateMinutes = (int) ChronoUnit.MINUTES.between(startTime, checkInTime);
            this.status = AttendanceStatus.LATE;
        }
    }

    /**
     * Excuse une absence
     */
    public void excuse(String justification, String justifiedBy) {
        this.status = AttendanceStatus.EXCUSED;
        this.justification = justification;
        this.justifiedBy = justifiedBy;
        this.justifiedAt = LocalDateTime.now();
    }

    /**
     * Marque une absence comme injustifiée
     */
    public void markUnjustified() {
        this.status = AttendanceStatus.UNJUSTIFIED;
    }

    /**
     * Marque comme jour férié
     */
    public void markHoliday() {
        this.status = AttendanceStatus.HOLIDAY;
    }

    /**
     * Marque comme congé maladie
     */
    public void markSickLeave(String justification) {
        this.status = AttendanceStatus.SICK_LEAVE;
        this.justification = justification;
    }

    /**
     * Justifie une présence
     */
    public void justify(String justification, String justifiedBy) {
        if (status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE) {
            this.justification = justification;
            this.justifiedBy = justifiedBy;
            this.justifiedAt = LocalDateTime.now();
        }
    }

    /**
     * Ajoute une remarque
     */
    public void addRemark(String remark) {
        this.remarks = remark;
    }

    /**
     * Enregistre le départ
     */
    public void checkOut() {
        this.checkOutTime = LocalDateTime.now();
    }

    /**
     * Vérifie si l'étudiant a pointé son départ
     */
    public boolean hasCheckedOut() {
        return checkOutTime != null;
    }

    /**
     * Calcule la durée de présence en minutes
     */
    public Long getPresenceDurationMinutes() {
        if (checkInTime == null || checkOutTime == null) {
            return null;
        }
        return ChronoUnit.MINUTES.between(checkInTime, checkOutTime);
    }

    /**
     * Vérifie si le retard est important (plus de 15 minutes)
     */
    public boolean isSignificantlyLate() {
        return lateMinutes != null && lateMinutes > 15;
    }

    /**
     * Vérifie si le retard est critique (plus de 30 minutes)
     */
    public boolean isCriticallyLate() {
        return lateMinutes != null && lateMinutes > 30;
    }

    /**
     * Vérifie si c'est une présence en ligne
     */
    public boolean isOnline() {
        return isOnline != null && isOnline;
    }

    /**
     * Marque la présence comme en ligne
     */
    public void markAsOnline() {
        this.isOnline = true;
    }

    /**
     * Marque la présence comme en présentiel
     */
    public void markAsInPerson() {
        this.isOnline = false;
    }

    /**
     * Retourne le statut en français - VERSION CORRIGÉE
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case PRESENT -> "Présent";
            case ABSENT -> "Absent";
            case LATE -> "En retard";
            case EXCUSED -> "Absence excusée";
            case UNJUSTIFIED -> "Absence injustifiée";
            case HOLIDAY -> "Jour férié";
            case SICK_LEAVE -> "Congé maladie";
        };
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (status == null) {
            return "secondary";
        }
        return switch (status) {
            case PRESENT -> "success";
            case LATE -> "warning";
            case ABSENT, UNJUSTIFIED -> "danger";
            case EXCUSED, SICK_LEAVE -> "info";
            case HOLIDAY -> "primary";
        };
    }

    /**
     * Retourne l'icône associée au statut
     */
    public String getStatusIcon() {
        if (status == null) {
            return "fa-question-circle";
        }
        return switch (status) {
            case PRESENT -> "fa-check-circle";
            case LATE -> "fa-clock";
            case ABSENT -> "fa-times-circle";
            case EXCUSED -> "fa-check-circle";
            case UNJUSTIFIED -> "fa-exclamation-circle";
            case HOLIDAY -> "fa-calendar-day";
            case SICK_LEAVE -> "fa-hospital";
        };
    }

    /**
     * Retourne le libellé du retard formaté
     */
    public String getFormattedLateMinutes() {
        if (lateMinutes == null || lateMinutes == 0) {
            return "À l'heure";
        }
        if (lateMinutes < 60) {
            return lateMinutes + " minute" + (lateMinutes > 1 ? "s" : "") + " de retard";
        }
        int hours = lateMinutes / 60;
        int minutes = lateMinutes % 60;
        if (minutes == 0) {
            return hours + " heure" + (hours > 1 ? "s" : "") + " de retard";
        }
        return hours + "h" + minutes + " de retard";
    }

    /**
     * Vérifie si c'est une présence du jour
     */
    public boolean isToday() {
        return date != null && date.equals(LocalDate.now());
    }

    /**
     * Vérifie si c'est une présence de la semaine
     */
    public boolean isThisWeek() {
        if (date == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek);
    }

    /**
     * Vérifie si la présence est récente (moins de 7 jours)
     */
    public boolean isRecent() {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now().minusDays(7));
    }

    /**
     * Vérifie si la présence est en retard par rapport à l'horaire
     */
    public boolean isLateForTimeSlot(String expectedStartTime) {
        if (checkInTime == null || expectedStartTime == null) {
            return false;
        }
        LocalDateTime expectedStart = LocalDateTime.of(date, LocalTime.parse(expectedStartTime));
        return checkInTime.isAfter(expectedStart);
    }

    /**
     * Calcule le taux de présence pour une liste de présences
     */
    public static double calculateAttendanceRate(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return 0.0;
        }
        long presentCount = attendances.stream()
                .filter(Attendance::wasPresent)
                .count();
        return (double) presentCount / attendances.size() * 100.0;
    }

    /**
     * Calcule le nombre de retards
     */
    public static int countLates(List<Attendance> attendances) {
        if (attendances == null) {
            return 0;
        }
        return (int) attendances.stream()
                .filter(Attendance::isLate)
                .count();
    }

    /**
     * Calcule le nombre d'absences
     */
    public static int countAbsences(List<Attendance> attendances) {
        if (attendances == null) {
            return 0;
        }
        return (int) attendances.stream()
                .filter(a -> a.isAbsent() || a.isUnjustified())
                .count();
    }

    /**
     * Calcule le nombre d'absences excusées
     */
    public static int countExcusedAbsences(List<Attendance> attendances) {
        if (attendances == null) {
            return 0;
        }
        return (int) attendances.stream()
                .filter(Attendance::isExcused)
                .count();
    }

    /**
     * Calcule le nombre de présences en ligne
     */
    public static int countOnlinePresences(List<Attendance> attendances) {
        if (attendances == null) {
            return 0;
        }
        return (int) attendances.stream()
                .filter(Attendance::isOnline)
                .count();
    }

    /**
     * Calcule la moyenne des minutes de retard
     */
    public static double calculateAverageLateMinutes(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return 0.0;
        }
        return attendances.stream()
                .filter(Attendance::isLate)
                .mapToInt(a -> a.getLateMinutes() != null ? a.getLateMinutes() : 0)
                .average()
                .orElse(0.0);
    }

    /**
     * Retourne le résumé des présences
     */
    public static AttendanceSummary getSummary(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return AttendanceSummary.builder().build();
        }
        return AttendanceSummary.builder()
                .total(attendances.size())
                .present((int) attendances.stream().filter(Attendance::isPresent).count())
                .absent((int) attendances.stream().filter(Attendance::isAbsent).count())
                .late((int) attendances.stream().filter(Attendance::isLate).count())
                .excused((int) attendances.stream().filter(Attendance::isExcused).count())
                .unjustified((int) attendances.stream().filter(Attendance::isUnjustified).count())
                .holiday((int) attendances.stream().filter(Attendance::isHoliday).count())
                .sickLeave((int) attendances.stream().filter(Attendance::isSickLeave).count())
                .online((int) attendances.stream().filter(Attendance::isOnline).count())
                .attendanceRate(calculateAttendanceRate(attendances))
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceSummary {
        private int total;
        private int present;
        private int absent;
        private int late;
        private int excused;
        private int unjustified;
        private int holiday;
        private int sickLeave;
        private int online;
        private double attendanceRate;
    }
}