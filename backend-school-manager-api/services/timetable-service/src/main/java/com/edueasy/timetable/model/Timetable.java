package com.edueasy.timetable.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(
        name = "timetables",
        indexes = {
                @Index(name = "idx_timetable_school", columnList = "school_id"),
                @Index(name = "idx_timetable_class", columnList = "class_id"),
                @Index(name = "idx_timetable_teacher", columnList = "teacher_id"),
                @Index(name = "idx_timetable_room", columnList = "room_id"),
                @Index(name = "idx_timetable_date", columnList = "date"),
                @Index(name = "idx_timetable_course", columnList = "course_id"),
                @Index(name = "idx_timetable_status", columnList = "status"),
                @Index(name = "idx_timetable_academic_year", columnList = "academic_year")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "school_id")
    private String schoolId;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "course_id")
    private String courseId;

    private String subject;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Enumerated(EnumType.STRING)
    private TimetableStatus status;

    @Column(name = "academic_year")
    private String academicYear;

    private String period;

    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(name = "recurrence_id")
    private String recurrenceId;

    @Column(name = "teacher_notes", columnDefinition = "TEXT")
    private String teacherNotes;

    @Column(name = "is_cancelled")
    @Builder.Default
    private Boolean isCancelled = false;

    @Column(name = "cancelled_reason", columnDefinition = "TEXT")
    private String cancelledReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "is_makeup")
    @Builder.Default
    private Boolean isMakeup = false;

    @Column(name = "original_timetable_id")
    private String originalTimetableId;

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
            status = TimetableStatus.SCHEDULED;
        }
        if (isRecurring == null) {
            isRecurring = false;
        }
        if (isCancelled == null) {
            isCancelled = false;
        }
        if (isMakeup == null) {
            isMakeup = false;
        }
        if (dayOfWeek == null && date != null) {
            dayOfWeek = String.valueOf(date.getDayOfWeek().getValue());
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si le cours est programmé
     */
    public boolean isScheduled() {
        return status == TimetableStatus.SCHEDULED;
    }

    /**
     * Vérifie si le cours est en cours
     */
    public boolean isOngoing() {
        if (status != TimetableStatus.ONGOING) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return date.equals(today) && !now.isBefore(start) && !now.isAfter(end);
    }

    /**
     * Vérifie si le cours est terminé
     */
    public boolean isCompleted() {
        return status == TimetableStatus.COMPLETED;
    }

    /**
     * Vérifie si le cours est annulé
     */
    public boolean isCancelled() {
        return isCancelled != null && isCancelled;
    }

    /**
     * Vérifie si le cours est reporté
     */
    public boolean isPostponed() {
        return status == TimetableStatus.POSTPONED;
    }

    /**
     * Vérifie si le cours est reprogrammé
     */
    public boolean isRescheduled() {
        return status == TimetableStatus.RESCHEDULED;
    }

    /**
     * Vérifie si le cours est en cours (IN_PROGRESS)
     */
    public boolean isInProgress() {
        return status == TimetableStatus.IN_PROGRESS;
    }

    /**
     * Vérifie si le cours est récurrent
     */
    public boolean isRecurring() {
        return isRecurring != null && isRecurring;
    }

    /**
     * Vérifie si le cours est de rattrapage
     */
    public boolean isMakeup() {
        return isMakeup != null && isMakeup;
    }

    /**
     * Vérifie si le cours est passé
     */
    public boolean isPast() {
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            return true;
        }
        if (date.isAfter(today)) {
            return false;
        }
        LocalTime now = LocalTime.now();
        LocalTime end = LocalTime.parse(endTime);
        return now.isAfter(end);
    }

    /**
     * Vérifie si le cours est futur
     */
    public boolean isFuture() {
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            return true;
        }
        if (date.isBefore(today)) {
            return false;
        }
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.parse(startTime);
        return now.isBefore(start);
    }

    /**
     * Vérifie si le cours est en conflit avec un autre
     */
    public boolean conflictsWith(Timetable other) {
        if (other == null) {
            return false;
        }

        // Même date
        if (!date.equals(other.date)) {
            return false;
        }

        // Même enseignant
        if (teacherId != null && teacherId.equals(other.teacherId)) {
            return timeOverlaps(other);
        }

        // Même salle
        if (roomId != null && roomId.equals(other.roomId)) {
            return timeOverlaps(other);
        }

        // Même classe
        if (classId != null && classId.equals(other.classId)) {
            return timeOverlaps(other);
        }

        return false;
    }

    /**
     * Vérifie si les horaires se chevauchent
     */
    private boolean timeOverlaps(Timetable other) {
        LocalTime thisStart = LocalTime.parse(startTime);
        LocalTime thisEnd = LocalTime.parse(endTime);
        LocalTime otherStart = LocalTime.parse(other.startTime);
        LocalTime otherEnd = LocalTime.parse(other.endTime);

        return !thisEnd.isBefore(otherStart) && !thisEnd.equals(otherStart)
                && !thisStart.isAfter(otherEnd) && !thisStart.equals(otherEnd);
    }

    /**
     * Vérifie si le cours est en conflit avec un autre (version avec paramètres)
     */
    public boolean conflictsWith(String otherTeacherId, String otherRoomId, String otherClassId, LocalDate otherDate, String otherStartTime, String otherEndTime) {
        if (!date.equals(otherDate)) {
            return false;
        }

        if (teacherId != null && teacherId.equals(otherTeacherId)) {
            return timeOverlaps(otherStartTime, otherEndTime);
        }

        if (roomId != null && roomId.equals(otherRoomId)) {
            return timeOverlaps(otherStartTime, otherEndTime);
        }

        if (classId != null && classId.equals(otherClassId)) {
            return timeOverlaps(otherStartTime, otherEndTime);
        }

        return false;
    }

    /**
     * Vérifie si les horaires se chevauchent (version avec paramètres)
     */
    private boolean timeOverlaps(String otherStartTime, String otherEndTime) {
        LocalTime thisStart = LocalTime.parse(startTime);
        LocalTime thisEnd = LocalTime.parse(endTime);
        LocalTime otherStart = LocalTime.parse(otherStartTime);
        LocalTime otherEnd = LocalTime.parse(otherEndTime);

        return !thisEnd.isBefore(otherStart) && !thisEnd.equals(otherStart)
                && !thisStart.isAfter(otherEnd) && !thisStart.equals(otherEnd);
    }

    /**
     * Marque le cours comme en cours
     */
    public void markAsOngoing() {
        this.status = TimetableStatus.ONGOING;
    }

    /**
     * Marque le cours comme en progression
     */
    public void markAsInProgress() {
        this.status = TimetableStatus.IN_PROGRESS;
    }

    /**
     * Marque le cours comme terminé
     */
    public void markAsCompleted() {
        this.status = TimetableStatus.COMPLETED;
    }

    /**
     * Annule le cours
     */
    public void cancel(String reason, String cancelledBy) {
        this.isCancelled = true;
        this.cancelledReason = reason;
        this.cancelledBy = cancelledBy;
        this.cancelledAt = LocalDateTime.now();
        this.status = TimetableStatus.CANCELLED;
    }

    /**
     * Reporte le cours
     */
    public void postpone(String reason) {
        this.status = TimetableStatus.POSTPONED;
        this.cancelledReason = reason;
        this.isCancelled = true;
    }

    /**
     * Reprogramme le cours
     */
    public void reschedule(LocalDate newDate, String newStartTime, String newEndTime) {
        this.date = newDate;
        this.startTime = newStartTime;
        this.endTime = newEndTime;
        this.dayOfWeek = String.valueOf(newDate.getDayOfWeek().getValue());
        this.status = TimetableStatus.RESCHEDULED;
    }

    /**
     * Crée un cours de rattrapage
     */
    public Timetable createMakeup(LocalDate newDate, String startTime, String endTime) {
        Timetable makeup = new Timetable();
        makeup.setUuid(UUID.randomUUID().toString());
        makeup.setSchoolId(this.schoolId);
        makeup.setClassId(this.classId);
        makeup.setTeacherId(this.teacherId);
        makeup.setRoomId(this.roomId);
        makeup.setCourseId(this.courseId);
        makeup.setSubject(this.subject);
        makeup.setDate(newDate);
        makeup.setStartTime(startTime);
        makeup.setEndTime(endTime);
        makeup.setDayOfWeek(String.valueOf(newDate.getDayOfWeek().getValue()));
        makeup.setStatus(TimetableStatus.SCHEDULED);
        makeup.setAcademicYear(this.academicYear);
        makeup.setPeriod(this.period);
        makeup.setIsMakeup(true);
        makeup.setOriginalTimetableId(this.id);
        return makeup;
    }

    /**
     * Retourne le créneau horaire formaté
     */
    public String getTimeSlot() {
        return startTime + " - " + endTime;
    }

    /**
     * Retourne le créneau horaire formaté avec la date
     */
    public String getFullTimeSlot() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(dateFormatter) + " de " + startTime + " à " + endTime;
    }

    /**
     * Retourne le statut en français - VERSION CORRIGÉE
     */
    public String getStatusLabel() {
        if (isCancelled()) {
            return "Annulé";
        }
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case SCHEDULED -> "Programmé";
            case ONGOING -> "En cours";
            case IN_PROGRESS -> "En progression";
            case COMPLETED -> "Terminé";
            case POSTPONED -> "Reporté";
            case CANCELLED -> "Annulé";
            case RESCHEDULED -> "Reprogrammé";
        };
    }

    /**
     * Retourne la couleur associée au statut - VERSION CORRIGÉE
     */
    public String getStatusColor() {
        if (isCancelled()) {
            return "red";
        }
        if (status == null) {
            return "gray";
        }
        return switch (status) {
            case SCHEDULED -> "blue";
            case ONGOING -> "green";
            case IN_PROGRESS -> "yellow";
            case COMPLETED -> "gray";
            case POSTPONED -> "orange";
            case CANCELLED -> "red";
            case RESCHEDULED -> "purple";
        };
    }

    /**
     * Retourne l'icône associée au statut
     */
    public String getStatusIcon() {
        if (isCancelled()) {
            return "fa-times-circle";
        }
        if (status == null) {
            return "fa-question-circle";
        }
        return switch (status) {
            case SCHEDULED -> "fa-calendar-check";
            case ONGOING -> "fa-play-circle";
            case IN_PROGRESS -> "fa-spinner";
            case COMPLETED -> "fa-check-circle";
            case POSTPONED -> "fa-clock";
            case CANCELLED -> "fa-times-circle";
            case RESCHEDULED -> "fa-calendar-plus";
        };
    }

    /**
     * Vérifie si le cours est valide
     */
    public boolean isValid() {
        return date != null
                && startTime != null && !startTime.isEmpty()
                && endTime != null && !endTime.isEmpty()
                && (classId != null || teacherId != null || roomId != null);
    }

    /**
     * Vérifie si le cours a un enseignant
     */
    public boolean hasTeacher() {
        return teacherId != null && !teacherId.isEmpty();
    }

    /**
     * Vérifie si le cours a une salle
     */
    public boolean hasRoom() {
        return roomId != null && !roomId.isEmpty();
    }

    /**
     * Vérifie si le cours a une classe
     */
    public boolean hasClass() {
        return classId != null && !classId.isEmpty();
    }

    /**
     * Vérifie si le cours a un sujet
     */
    public boolean hasSubject() {
        return subject != null && !subject.isEmpty();
    }

    /**
     * Vérifie si le cours a un cours associé
     */
    public boolean hasCourse() {
        return courseId != null && !courseId.isEmpty();
    }

    /**
     * Retourne le nom du jour de la semaine
     */
    public String getDayOfWeekLabel() {
        if (dayOfWeek == null) {
            return "N/A";
        }
        try {
            int day = Integer.parseInt(dayOfWeek);
            return switch (day) {
                case 1 -> "Lundi";
                case 2 -> "Mardi";
                case 3 -> "Mercredi";
                case 4 -> "Jeudi";
                case 5 -> "Vendredi";
                case 6 -> "Samedi";
                case 7 -> "Dimanche";
                default -> "N/A";
            };
        } catch (NumberFormatException e) {
            return dayOfWeek;
        }
    }

    /**
     * Retourne l'année académique formatée
     */
    public String getFormattedAcademicYear() {
        if (academicYear == null) {
            return "N/A";
        }
        return academicYear;
    }

    /**
     * Vérifie si le cours peut être modifié
     */
    public boolean isModifiable() {
        return status == TimetableStatus.SCHEDULED || status == TimetableStatus.RESCHEDULED;
    }

    /**
     * Vérifie si le cours peut être annulé
     */
    public boolean isCancellable() {
        return status != TimetableStatus.COMPLETED && status != TimetableStatus.CANCELLED;
    }

    /**
     * Calcule la durée du cours en minutes
     */
    public long getDurationMinutes() {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return java.time.Duration.between(start, end).toMinutes();
    }

    /**
     * Vérifie si le cours est un cours du matin
     */
    public boolean isMorningClass() {
        LocalTime start = LocalTime.parse(startTime);
        return start.isBefore(LocalTime.NOON);
    }

    /**
     * Vérifie si le cours est un cours de l'après-midi
     */
    public boolean isAfternoonClass() {
        LocalTime start = LocalTime.parse(startTime);
        return start.isAfter(LocalTime.NOON) || start.equals(LocalTime.NOON);
    }

    /**
     * Crée une copie du cours
     */
    public Timetable copy() {
        Timetable copy = new Timetable();
        copy.setUuid(UUID.randomUUID().toString());
        copy.setSchoolId(this.schoolId);
        copy.setClassId(this.classId);
        copy.setTeacherId(this.teacherId);
        copy.setRoomId(this.roomId);
        copy.setCourseId(this.courseId);
        copy.setSubject(this.subject);
        copy.setDate(this.date);
        copy.setStartTime(this.startTime);
        copy.setEndTime(this.endTime);
        copy.setDayOfWeek(this.dayOfWeek);
        copy.setStatus(this.status);
        copy.setAcademicYear(this.academicYear);
        copy.setPeriod(this.period);
        copy.setIsRecurring(this.isRecurring);
        copy.setRecurrenceId(this.recurrenceId);
        copy.setTeacherNotes(this.teacherNotes);
        copy.setIsMakeup(this.isMakeup);
        copy.setOriginalTimetableId(this.id);
        return copy;
    }
}