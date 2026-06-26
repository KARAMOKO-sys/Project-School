package com.edueasy.timetable.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySlot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "subject")
    private String subject;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (isActive == null) {
            isActive = true;
        }
        validateTimes();
    }

    // ===== Méthodes métier =====

    /**
     * Valide les horaires du créneau
     */
    public void validateTimes() {
        if (startTime != null && endTime != null) {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            if (end.isBefore(start) || end.equals(start)) {
                throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
            }
        }
    }

    /**
     * Vérifie si le créneau est actif
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Active le créneau
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Désactive le créneau
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Vérifie si le créneau a un cours
     */
    public boolean hasCourse() {
        return courseId != null && !courseId.isEmpty();
    }

    /**
     * Vérifie si le créneau a un enseignant
     */
    public boolean hasTeacher() {
        return teacherId != null && !teacherId.isEmpty();
    }

    /**
     * Vérifie si le créneau a une salle
     */
    public boolean hasRoom() {
        return roomId != null && !roomId.isEmpty();
    }

    /**
     * Vérifie si le créneau a une classe
     */
    public boolean hasClass() {
        return classId != null && !classId.isEmpty();
    }

    /**
     * Vérifie si le créneau a une matière
     */
    public boolean hasSubject() {
        return subject != null && !subject.isEmpty();
    }

    /**
     * Retourne le créneau horaire formaté
     */
    public String getTimeSlot() {
        return startTime + " - " + endTime;
    }

    /**
     * Retourne le libellé du jour de la semaine
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
     * Retourne le numéro du jour de la semaine (1-7)
     */
    public Integer getDayOfWeekNumber() {
        if (dayOfWeek == null) {
            return null;
        }
        try {
            return Integer.parseInt(dayOfWeek);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Vérifie si le créneau se chevauche avec un autre
     */
    public boolean overlaps(WeeklySlot other) {
        if (dayOfWeek == null || other.dayOfWeek == null) {
            return false;
        }
        if (!dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        LocalTime thisStart = LocalTime.parse(startTime);
        LocalTime thisEnd = LocalTime.parse(endTime);
        LocalTime otherStart = LocalTime.parse(other.startTime);
        LocalTime otherEnd = LocalTime.parse(other.endTime);
        return !thisEnd.isBefore(otherStart) && !thisEnd.equals(otherStart)
                && !thisStart.isAfter(otherEnd) && !thisStart.equals(otherEnd);
    }

    /**
     * Vérifie si le créneau est valide
     */
    public boolean isValid() {
        return dayOfWeek != null && !dayOfWeek.isEmpty()
                && startTime != null && !startTime.isEmpty()
                && endTime != null && !endTime.isEmpty()
                && (courseId != null || subject != null);
    }

    /**
     * Retourne la durée en minutes
     */
    public long getDurationMinutes() {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return java.time.Duration.between(start, end).toMinutes();
    }

    /**
     * Retourne la durée formatée
     */
    public String getFormattedDuration() {
        long minutes = getDurationMinutes();
        if (minutes < 60) {
            return minutes + " min";
        }
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        if (remainingMinutes == 0) {
            return hours + " h";
        }
        return hours + "h " + remainingMinutes + "min";
    }

    /**
     * Met à jour les horaires
     */
    public void updateTimeSlot(String startTime, String endTime) {
        if (startTime != null && !startTime.isEmpty()) {
            this.startTime = startTime;
        }
        if (endTime != null && !endTime.isEmpty()) {
            this.endTime = endTime;
        }
        validateTimes();
    }

    /**
     * Retourne la description complète du créneau
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDayOfWeekLabel()).append(" ");
        sb.append(getTimeSlot());
        if (subject != null) {
            sb.append(" - ").append(subject);
        }
        if (teacherId != null) {
            sb.append(" (Enseignant: ").append(teacherId).append(")");
        }
        return sb.toString();
    }
}