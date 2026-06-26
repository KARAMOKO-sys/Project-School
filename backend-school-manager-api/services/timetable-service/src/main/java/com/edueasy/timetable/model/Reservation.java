package com.edueasy.timetable.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(
        name = "reservations",
        indexes = {
                @Index(name = "idx_reservation_room", columnList = "room_id"),
                @Index(name = "idx_reservation_date", columnList = "date"),
                @Index(name = "idx_reservation_status", columnList = "status"),
                @Index(name = "idx_reservation_reserved_by", columnList = "reserved_by"),
                @Index(name = "idx_reservation_room_date", columnList = "room_id, date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "reserved_by", nullable = false)
    private String reservedBy;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "recurring")
    @Builder.Default
    private Boolean recurring = false;

    @Column(name = "recurrence_pattern")
    private String recurrencePattern;

    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    @Column(name = "attendees", columnDefinition = "TEXT")
    private String attendees;

    @Column(name = "color")
    private String color;

    @Column(name = "is_all_day")
    @Builder.Default
    private Boolean isAllDay = false;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = ReservationStatus.PENDING;
        }
        if (recurring == null) {
            recurring = false;
        }
        if (isAllDay == null) {
            isAllDay = false;
        }
        validateTimes();
    }

    @PreUpdate
    protected void onUpdate() {
        validateTimes();
    }

    // ===== Méthodes métier =====

    /**
     * Valide les horaires de réservation
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
     * Vérifie si la réservation est en attente
     */
    public boolean isPending() {
        return status == ReservationStatus.PENDING;
    }

    /**
     * Vérifie si la réservation est confirmée
     */
    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    /**
     * Vérifie si la réservation est annulée
     */
    public boolean isCancelled() {
        return status == ReservationStatus.CANCELLED;
    }

    /**
     * Vérifie si la réservation est terminée
     */
    public boolean isCompleted() {
        return status == ReservationStatus.COMPLETED;
    }

    /**
     * Vérifie si la réservation est en cours
     */
    public boolean isOngoing() {
        if (status != ReservationStatus.CONFIRMED) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return date.equals(today) && !now.isBefore(start) && !now.isAfter(end);
    }

    /**
     * Vérifie si la réservation est passée
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
     * Vérifie si la réservation est future
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
     * Vérifie si la réservation est récurrente
     */
    public boolean isRecurring() {
        return recurring != null && recurring;
    }

    /**
     * Vérifie si la réservation est pour toute la journée
     */
    public boolean isAllDay() {
        return isAllDay != null && isAllDay;
    }

    /**
     * Confirme la réservation
     */
    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    /**
     * Annule la réservation
     */
    public void cancel(String reason, String cancelledBy) {
        this.status = ReservationStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledBy = cancelledBy;
        this.cancelledAt = LocalDateTime.now();
    }

    /**
     * Marque la réservation comme terminée
     */
    public void complete() {
        this.status = ReservationStatus.COMPLETED;
    }

    /**
     * Vérifie si la réservation peut être modifiée
     */
    public boolean isModifiable() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
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
     * Retourne le statut en français
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case PENDING -> "En attente";
            case CONFIRMED -> "Confirmée";
            case CANCELLED -> "Annulée";
            case COMPLETED -> "Terminée";
        };
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (status == null) {
            return "gray";
        }
        return switch (status) {
            case PENDING -> "orange";
            case CONFIRMED -> "green";
            case CANCELLED -> "red";
            case COMPLETED -> "blue";
        };
    }

    /**
     * Vérifie si la réservation est valide
     */
    public boolean isValid() {
        return roomId != null && !roomId.isEmpty()
                && reservedBy != null && !reservedBy.isEmpty()
                && eventName != null && !eventName.isEmpty()
                && date != null
                && startTime != null && !startTime.isEmpty()
                && endTime != null && !endTime.isEmpty()
                && status != null;
    }

    /**
     * Vérifie si deux réservations se chevauchent
     */
    public boolean overlaps(Reservation other) {
        if (!roomId.equals(other.roomId) || !date.equals(other.date)) {
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
     * Vérifie si la réservation a des participants
     */
    public boolean hasAttendees() {
        return attendees != null && !attendees.isEmpty();
    }

    /**
     * Ajoute des participants
     */
    public void addAttendees(String attendees) {
        if (attendees != null && !attendees.isEmpty()) {
            if (this.attendees == null || this.attendees.isEmpty()) {
                this.attendees = attendees;
            } else {
                this.attendees = this.attendees + "," + attendees;
            }
        }
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
     * Met à jour la date
     */
    public void updateDate(LocalDate newDate) {
        if (newDate != null) {
            this.date = newDate;
        }
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
}