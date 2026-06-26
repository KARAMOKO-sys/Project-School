package com.edueasy.communication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_user", columnList = "user_id"),
                @Index(name = "idx_notification_type", columnList = "type"),
                @Index(name = "idx_notification_created", columnList = "created_at"),
                @Index(name = "idx_notification_read", columnList = "is_read"),
                @Index(name = "idx_notification_related", columnList = "related_id, related_type")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Column(name = "related_id")
    private String relatedId;

    @Column(name = "related_type")
    private String relatedType;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "is_dismissed")
    @Builder.Default
    private Boolean isDismissed = false;

    @Column(name = "dismissed_at")
    private LocalDateTime dismissedAt;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "action_label")
    private String actionLabel;

    @Column(name = "icon")
    private String icon;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (isRead == null) {
            isRead = false;
        }
        if (isDismissed == null) {
            isDismissed = false;
        }
        if (priority == null) {
            priority = 0;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si la notification est lue
     */
    public boolean isRead() {
        return isRead != null && isRead;
    }

    /**
     * Vérifie si la notification est ignorée
     */
    public boolean isDismissed() {
        return isDismissed != null && isDismissed;
    }

    /**
     * Vérifie si la notification a un corps
     */
    public boolean hasBody() {
        return body != null && !body.isEmpty();
    }

    /**
     * Vérifie si la notification a une action
     */
    public boolean hasAction() {
        return actionUrl != null && !actionUrl.isEmpty() &&
                actionLabel != null && !actionLabel.isEmpty();
    }

    /**
     * Vérifie si la notification a une icône
     */
    public boolean hasIcon() {
        return icon != null && !icon.isEmpty();
    }

    /**
     * Vérifie si la notification est liée à un élément
     */
    public boolean hasRelated() {
        return relatedId != null && !relatedId.isEmpty() &&
                relatedType != null && !relatedType.isEmpty();
    }

    /**
     * Marque la notification comme lue
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Marque la notification comme non lue
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    /**
     * Ignore la notification
     */
    public void dismiss() {
        this.isDismissed = true;
        this.dismissedAt = LocalDateTime.now();
    }

    /**
     * Réactive la notification
     */
    public void undismiss() {
        this.isDismissed = false;
        this.dismissedAt = null;
    }

    /**
     * Vérifie si la notification est un avertissement
     */
    public boolean isWarning() {
        return type == NotificationType.WARNING;
    }

    /**
     * Vérifie si la notification est une erreur
     */
    public boolean isError() {
        return type == NotificationType.ERROR;
    }

    /**
     * Vérifie si la notification est un succès
     */
    public boolean isSuccess() {
        return type == NotificationType.SUCCESS;
    }

    /**
     * Vérifie si la notification est une information
     */
    public boolean isInfo() {
        return type == NotificationType.INFO;
    }

    /**
     * Vérifie si la notification est un rappel
     */
    public boolean isReminder() {
        return type == NotificationType.REMINDER;
    }

    /**
     * Vérifie si la notification concerne une note
     */
    public boolean isGrade() {
        return type == NotificationType.GRADE;
    }

    /**
     * Vérifie si la notification concerne une absence
     */
    public boolean isAbsence() {
        return type == NotificationType.ABSENCE;
    }

    /**
     * Vérifie si la notification est un message
     */
    public boolean isMessage() {
        return type == NotificationType.MESSAGE;
    }

    /**
     * Vérifie si la notification est une annonce
     */
    public boolean isAnnouncement() {
        return type == NotificationType.ANNOUNCEMENT;
    }

    /**
     * Vérifie si la notification concerne une date limite
     */
    public boolean isDeadline() {
        return type == NotificationType.DEADLINE;
    }

    /**
     * Vérifie si la notification concerne un paiement
     */
    public boolean isPayment() {
        return type == NotificationType.PAYMENT;
    }

    /**
     * Vérifie si la notification concerne une présence
     */
    public boolean isAttendance() {
        return type == NotificationType.ATTENDANCE;
    }

    /**
     * Retourne la couleur associée au type - VERSION CORRIGÉE
     */
    public String getTypeColor() {
        if (type == null) {
            return "gray";
        }
        return switch (type) {
            case INFO -> "blue";
            case SUCCESS -> "green";
            case WARNING -> "orange";
            case ERROR -> "red";
            case REMINDER -> "purple";
            case GRADE -> "indigo";
            case ABSENCE -> "red";
            case MESSAGE -> "cyan";
            case ANNOUNCEMENT -> "teal";
            case DEADLINE -> "orange";
            case PAYMENT -> "green";
            case ATTENDANCE -> "blue";
        };
    }

    /**
     * Retourne l'icône par défaut en fonction du type - VERSION CORRIGÉE
     */
    public String getDefaultIcon() {
        if (type == null) {
            return "bell";
        }
        return switch (type) {
            case INFO -> "info-circle";
            case SUCCESS -> "check-circle";
            case WARNING -> "exclamation-triangle";
            case ERROR -> "times-circle";
            case REMINDER -> "clock";
            case GRADE -> "star";
            case ABSENCE -> "user-slash";
            case MESSAGE -> "envelope";
            case ANNOUNCEMENT -> "bullhorn";
            case DEADLINE -> "calendar-check";
            case PAYMENT -> "credit-card";
            case ATTENDANCE -> "clipboard-check";
        };
    }

    /**
     * Retourne le libellé du type en français
     */
    public String getTypeLabel() {
        if (type == null) {
            return "Inconnu";
        }
        return switch (type) {
            case INFO -> "Information";
            case SUCCESS -> "Succès";
            case WARNING -> "Avertissement";
            case ERROR -> "Erreur";
            case REMINDER -> "Rappel";
            case GRADE -> "Note";
            case ABSENCE -> "Absence";
            case MESSAGE -> "Message";
            case ANNOUNCEMENT -> "Annonce";
            case DEADLINE -> "Date limite";
            case PAYMENT -> "Paiement";
            case ATTENDANCE -> "Présence";
        };
    }

    /**
     * Retourne le temps depuis la création
     */
    public String getTimeSinceCreated() {
        if (createdAt == null) {
            return "N/A";
        }
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();

        if (minutes < 1) {
            return "À l'instant";
        }
        if (minutes < 60) {
            return minutes + " min";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " h";
        }
        long days = hours / 24;
        if (days < 30) {
            return days + " j";
        }
        long months = days / 30;
        if (months < 12) {
            return months + " mois";
        }
        return (months / 12) + " an" + (months > 12 ? "s" : "");
    }

    /**
     * Retourne le libellé du statut
     */
    public String getStatusLabel() {
        if (isDismissed()) {
            return "Ignorée";
        }
        if (isRead()) {
            return "Lue";
        }
        return "Non lue";
    }

    /**
     * Retourne la couleur du statut
     */
    public String getStatusColor() {
        if (isDismissed()) {
            return "gray";
        }
        if (isRead()) {
            return "green";
        }
        return "red";
    }

    /**
     * Vérifie si la notification est prioritaire
     */
    public boolean isHighPriority() {
        return priority != null && priority >= 5;
    }

    /**
     * Vérifie si la notification est très prioritaire
     */
    public boolean isCriticalPriority() {
        return priority != null && priority >= 8;
    }

    /**
     * Vérifie si la notification est récente (moins d'une heure)
     */
    public boolean isRecent() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusHours(1));
    }

    /**
     * Vérifie si la notification est non lue
     */
    public boolean isUnread() {
        return !isRead();
    }

    /**
     * Met à jour le contenu de la notification
     */
    public void updateContent(String title, String body) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (body != null && !body.trim().isEmpty()) {
            this.body = body;
        }
    }

    /**
     * Ajoute une action à la notification
     */
    public void setAction(String actionUrl, String actionLabel) {
        this.actionUrl = actionUrl;
        this.actionLabel = actionLabel;
    }

    /**
     * Vérifie si la notification peut être envoyée par un canal spécifique
     */
    public boolean canBeSentVia(NotificationChannel channel) {
        if (channel == null) {
            return false;
        }
        // Toutes les notifications peuvent être envoyées par tous les canaux par défaut
        return true;
    }

    /**
     * Retourne le niveau de priorité en texte
     */
    public String getPriorityLabel() {
        if (priority == null) {
            return "Basse";
        }
        if (priority >= 8) {
            return "Critique";
        }
        if (priority >= 5) {
            return "Élevée";
        }
        if (priority >= 3) {
            return "Moyenne";
        }
        return "Basse";
    }

    /**
     * Crée une notification de type INFO
     */
    public static Notification createInfo(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.INFO)
                .build();
    }

    /**
     * Crée une notification de type SUCCESS
     */
    public static Notification createSuccess(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.SUCCESS)
                .build();
    }

    /**
     * Crée une notification de type WARNING
     */
    public static Notification createWarning(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.WARNING)
                .build();
    }

    /**
     * Crée une notification de type ERROR
     */
    public static Notification createError(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.ERROR)
                .build();
    }

    /**
     * Crée une notification de type REMINDER
     */
    public static Notification createReminder(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.REMINDER)
                .build();
    }

    /**
     * Crée une notification de type GRADE
     */
    public static Notification createGradeNotification(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.GRADE)
                .icon("star")
                .build();
    }

    /**
     * Crée une notification de type ABSENCE
     */
    public static Notification createAbsenceNotification(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.ABSENCE)
                .icon("user-slash")
                .build();
    }

    /**
     * Crée une notification de type MESSAGE
     */
    public static Notification createMessageNotification(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.MESSAGE)
                .icon("envelope")
                .build();
    }

    /**
     * Crée une notification de type ANNOUNCEMENT
     */
    public static Notification createAnnouncement(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.ANNOUNCEMENT)
                .icon("bullhorn")
                .build();
    }

    /**
     * Crée une notification de type DEADLINE
     */
    public static Notification createDeadlineNotification(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.DEADLINE)
                .icon("calendar-check")
                .build();
    }

    /**
     * Crée une notification de type PAYMENT
     */
    public static Notification createPaymentNotification(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.PAYMENT)
                .icon("credit-card")
                .build();
    }

    /**
     * Crée une notification de type ATTENDANCE
     */
    public static Notification createAttendanceNotification(String userId, String title, String body) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(NotificationType.ATTENDANCE)
                .icon("clipboard-check")
                .build();
    }
}