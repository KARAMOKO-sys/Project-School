package com.edueasy.common.model;

//import com.edueasy.common.enums.NotificationFrequency;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification_preferences",
        indexes = {
                @Index(name = "idx_notification_preference_user", columnList = "user_uuid")
        }
)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationPreference extends AuditTimestamps implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    // 🔥 Remplacer la relation OneToOne par un simple champ UUID
    @Column(name = "user_uuid", nullable = false, length = 36)
    private String userUuid;

    @Column(name = "email_notifications")
    @Builder.Default
    private boolean emailNotifications = true;

    @Column(name = "push_notifications")
    @Builder.Default
    private boolean pushNotifications = true;

    @Column(name = "sms_notifications")
    @Builder.Default
    private boolean smsNotifications = false;

    @Column(name = "marketing_emails")
    @Builder.Default
    private boolean marketingEmails = false;

    @Column(name = "assignment_reminders")
    @Builder.Default
    private boolean assignmentReminders = true;

    @Column(name = "grade_notifications")
    @Builder.Default
    private boolean gradeNotifications = true;

    @Column(name = "event_reminders")
    @Builder.Default
    private boolean eventReminders = true;

    @Column(name = "system_updates")
    @Builder.Default
    private boolean systemUpdates = true;

    @Column(name = "message_notifications")
    @Builder.Default
    private boolean messageNotifications = true;

    @Column(name = "attendance_notifications")
    @Builder.Default
    private boolean attendanceNotifications = true;

    @Column(name = "payment_notifications")
    @Builder.Default
    private boolean paymentNotifications = true;

    @Column(name = "newsletter")
    @Builder.Default
    private boolean newsletter = false;

    /*
    @Column(name = "notification_frequency")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationFrequency frequency = NotificationFrequency.INSTANT;

     */

    @Column(name = "quiet_hours_start")
    private String quietHoursStart;

    @Column(name = "quiet_hours_end")
    private String quietHoursEnd;

    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "UTC";

    @Column(name = "last_notification_sent_at")
    private LocalDateTime lastNotificationSentAt;

    @Column(name = "preferred_language")
    @Builder.Default
    private String preferredLanguage = "fr";

    // ===== Méthodes de cycle de vie =====

    /*
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (frequency == null) {
            frequency = NotificationFrequency.INSTANT;
        }
        if (timezone == null) {
            timezone = "UTC";
        }
        if (preferredLanguage == null) {
            preferredLanguage = "fr";
        }
    }

     */

    // ===== Méthodes métier =====

    /**
     * Vérifie si les notifications par email sont activées
     */
    public boolean isEmailEnabled() {
        return emailNotifications;
    }

    /**
     * Vérifie si les notifications push sont activées
     */
    public boolean isPushEnabled() {
        return pushNotifications;
    }

    /**
     * Vérifie si les notifications SMS sont activées
     */
    public boolean isSmsEnabled() {
        return smsNotifications;
    }

    /**
     * Active les notifications par email
     */
    public void enableEmail() {
        this.emailNotifications = true;
    }

    /**
     * Désactive les notifications par email
     */
    public void disableEmail() {
        this.emailNotifications = false;
    }

    /**
     * Active les notifications push
     */
    public void enablePush() {
        this.pushNotifications = true;
    }

    /**
     * Désactive les notifications push
     */
    public void disablePush() {
        this.pushNotifications = false;
    }

    /**
     * Active les notifications SMS
     */
    public void enableSms() {
        this.smsNotifications = true;
    }

    /**
     * Désactive les notifications SMS
     */
    public void disableSms() {
        this.smsNotifications = false;
    }

    /**
     * Active toutes les notifications
     */
    public void enableAll() {
        this.emailNotifications = true;
        this.pushNotifications = true;
        this.smsNotifications = true;
        this.assignmentReminders = true;
        this.gradeNotifications = true;
        this.eventReminders = true;
        this.systemUpdates = true;
        this.messageNotifications = true;
        this.attendanceNotifications = true;
        this.paymentNotifications = true;
    }

    /**
     * Désactive toutes les notifications
     */
    public void disableAll() {
        this.emailNotifications = false;
        this.pushNotifications = false;
        this.smsNotifications = false;
        this.assignmentReminders = false;
        this.gradeNotifications = false;
        this.eventReminders = false;
        this.systemUpdates = false;
        this.messageNotifications = false;
        this.attendanceNotifications = false;
        this.paymentNotifications = false;
    }

    /**
     * Vérifie si les notifications de devoirs sont activées
     */
    public boolean isAssignmentRemindersEnabled() {
        return assignmentReminders;
    }

    /**
     * Vérifie si les notifications de notes sont activées
     */
    public boolean isGradeNotificationsEnabled() {
        return gradeNotifications;
    }

    /**
     * Vérifie si les rappels d'événements sont activés
     */
    public boolean isEventRemindersEnabled() {
        return eventReminders;
    }

    /**
     * Vérifie si les mises à jour système sont activées
     */
    public boolean isSystemUpdatesEnabled() {
        return systemUpdates;
    }

    /**
     * Vérifie si les notifications de messages sont activées
     */
    public boolean isMessageNotificationsEnabled() {
        return messageNotifications;
    }

    /**
     * Vérifie si les notifications de présence sont activées
     */
    public boolean isAttendanceNotificationsEnabled() {
        return attendanceNotifications;
    }

    /**
     * Vérifie si les notifications de paiement sont activées
     */
    public boolean isPaymentNotificationsEnabled() {
        return paymentNotifications;
    }

    /**
     * Vérifie si la newsletter est activée
     */
    public boolean isNewsletterEnabled() {
        return newsletter;
    }

    /**
     * Active la newsletter
     */
    public void enableNewsletter() {
        this.newsletter = true;
    }

    /**
     * Désactive la newsletter
     */
    public void disableNewsletter() {
        this.newsletter = false;
    }

    /**
     * Met à jour la fréquence des notifications
     */
    /*
    public void updateFrequency(NotificationFrequency newFrequency) {
        if (newFrequency != null) {
            this.frequency = newFrequency;
        }
    }

     */

    /**
     * Met à jour les heures de silence
     */
    public void updateQuietHours(String start, String end) {
        this.quietHoursStart = start;
        this.quietHoursEnd = end;
    }

    /**
     * Vérifie si les heures de silence sont définies
     */
    public boolean hasQuietHours() {
        return quietHoursStart != null && !quietHoursStart.isEmpty()
                && quietHoursEnd != null && !quietHoursEnd.isEmpty();
    }

    /**
     * Vérifie si l'utilisateur est en heures de silence
     */
    public boolean isInQuietHours() {
        if (!hasQuietHours()) {
            return false;
        }
        try {
            LocalDateTime now = LocalDateTime.now();
            String[] startParts = quietHoursStart.split(":");
            String[] endParts = quietHoursEnd.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            LocalDateTime start = now.withHour(startHour).withMinute(startMinute).withSecond(0).withNano(0);
            LocalDateTime end = now.withHour(endHour).withMinute(endMinute).withSecond(0).withNano(0);

            if (end.isBefore(start)) {
                end = end.plusDays(1);
            }
            return !now.isBefore(start) && !now.isAfter(end);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Met à jour la date du dernier envoi de notification
     */
    public void updateLastSent() {
        this.lastNotificationSentAt = LocalDateTime.now();
    }

    /**
     * Vérifie si une notification peut être envoyée
     */
    public boolean canSendNotification() {
        return !isInQuietHours();
    }

    /**
     * Retourne le résumé des préférences
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
       // sb.append("Fréquence: ").append(frequency != null ? frequency.getLabel() : "Non définie");
        sb.append(" | Email: ").append(emailNotifications ? "✅" : "❌");
        sb.append(" | Push: ").append(pushNotifications ? "✅" : "❌");
        sb.append(" | SMS: ").append(smsNotifications ? "✅" : "❌");
        sb.append(" | Devoirs: ").append(assignmentReminders ? "✅" : "❌");
        sb.append(" | Notes: ").append(gradeNotifications ? "✅" : "❌");
        return sb.toString();
    }

    // ===== Nouvelles méthodes pour la gestion de l'UUID =====

    /**
     * Associe les préférences à un utilisateur par son UUID
     */
    public void associateWithUser(String userUuid) {
        if (userUuid != null && !userUuid.isEmpty()) {
            this.userUuid = userUuid;
        } else {
            throw new IllegalArgumentException("L'UUID de l'utilisateur ne peut pas être null ou vide");
        }
    }

    /**
     * Vérifie si les préférences sont associées à un utilisateur
     */
    public boolean hasUser() {
        return userUuid != null && !userUuid.isEmpty();
    }

    /**
     * Récupère l'UUID de l'utilisateur associé
     */
    public String getUserUuid() {
        return userUuid;
    }

    /**
     * Dissocie les préférences de l'utilisateur
     */
    public void disassociateFromUser() {
        this.userUuid = null;
    }

    // ===== Méthodes statiques de factory =====

    /**
     * Crée des préférences par défaut pour un utilisateur
     */
    public static NotificationPreference createDefault(String userUuid) {
        NotificationPreference preferences = createDefault();
        preferences.associateWithUser(userUuid);
        return preferences;
    }

    /**
     * Crée des préférences par défaut
     */
    public static NotificationPreference createDefault() {
        return NotificationPreference.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .marketingEmails(false)
                .assignmentReminders(true)
                .gradeNotifications(true)
                .eventReminders(true)
                .systemUpdates(true)
                .messageNotifications(true)
                .attendanceNotifications(true)
                .paymentNotifications(true)
                .newsletter(false)
               // .frequency(NotificationFrequency.INSTANT)
                .preferredLanguage("fr")
                .timezone("UTC")
                .build();
    }

    /**
     * Crée des préférences minimales (uniquement les essentielles)
     */
    public static NotificationPreference createMinimal(String userUuid) {
        NotificationPreference preferences = createMinimal();
        preferences.associateWithUser(userUuid);
        return preferences;
    }

    /**
     * Crée des préférences minimales (uniquement les essentielles)
     */
    public static NotificationPreference createMinimal() {
        return NotificationPreference.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .marketingEmails(false)
                .assignmentReminders(true)
                .gradeNotifications(true)
                .eventReminders(true)
                .systemUpdates(false)
                .messageNotifications(true)
                .attendanceNotifications(false)
                .paymentNotifications(false)
                .newsletter(false)
                //.frequency(NotificationFrequency.DAILY)
                .preferredLanguage("fr")
                .timezone("UTC")
                .build();
    }

    /**
     * Crée des préférences pour un enseignant
     */
    public static NotificationPreference createTeacherDefault(String userUuid) {
        NotificationPreference preferences = createTeacherDefault();
        preferences.associateWithUser(userUuid);
        return preferences;
    }

    /**
     * Crée des préférences pour un enseignant
     */
    public static NotificationPreference createTeacherDefault() {
        return NotificationPreference.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .marketingEmails(false)
                .assignmentReminders(true)
                .gradeNotifications(true)
                .eventReminders(true)
                .systemUpdates(true)
                .messageNotifications(true)
                .attendanceNotifications(true)
                .paymentNotifications(false)
                .newsletter(false)
                //.frequency(NotificationFrequency.INSTANT)
                .preferredLanguage("fr")
                .timezone("UTC")
                .build();
    }

    /**
     * Crée des préférences pour un étudiant
     */
    public static NotificationPreference createStudentDefault(String userUuid) {
        NotificationPreference preferences = createStudentDefault();
        preferences.associateWithUser(userUuid);
        return preferences;
    }

    /**
     * Crée des préférences pour un étudiant
     */
    public static NotificationPreference createStudentDefault() {
        return NotificationPreference.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .marketingEmails(false)
                .assignmentReminders(true)
                .gradeNotifications(true)
                .eventReminders(true)
                .systemUpdates(false)
                .messageNotifications(true)
                .attendanceNotifications(true)
                .paymentNotifications(false)
                .newsletter(false)
               // .frequency(NotificationFrequency.INSTANT)
                .preferredLanguage("fr")
                .timezone("UTC")
                .build();
    }
}