package com.edueasy.communication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_message_sender", columnList = "sender_id"),
                @Index(name = "idx_message_conversation", columnList = "conversation_id"),
                @Index(name = "idx_message_sent_at", columnList = "sent_at"),
                @Index(name = "idx_message_type", columnList = "type")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "sender_role")
    private String senderRole;

    private String subject;

    @Column(length = 5000)
    private String body;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ElementCollection
    @CollectionTable(
            name = "message_recipients",
            joinColumns = @JoinColumn(name = "message_id")
    )
    @Column(name = "recipient_id")
    @Builder.Default
    private List<String> recipientIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "message_read_by",
            joinColumns = @JoinColumn(name = "message_id")
    )
    @Column(name = "user_id")
    @Builder.Default
    private List<String> readBy = new ArrayList<>();

    @Column(name = "is_important")
    @Builder.Default
    private Boolean isImportant = false;

    @Column(name = "is_urgent")
    @Builder.Default
    private Boolean isUrgent = false;

    @Column(name = "is_draft")
    @Builder.Default
    private Boolean isDraft = false;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "reply_to_message_id")
    private String replyToMessageId;

    @Column(name = "parent_message_id")
    private String parentMessageId;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
        if (recipientIds == null) {
            recipientIds = new ArrayList<>();
        }
        if (readBy == null) {
            readBy = new ArrayList<>();
        }
        if (isImportant == null) {
            isImportant = false;
        }
        if (isUrgent == null) {
            isUrgent = false;
        }
        if (isDraft == null) {
            isDraft = false;
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si le message est important
     */
    public boolean isImportant() {
        return isImportant != null && isImportant;
    }

    /**
     * Vérifie si le message est urgent
     */
    public boolean isUrgent() {
        return isUrgent != null && isUrgent;
    }

    /**
     * Vérifie si le message est un brouillon
     */
    public boolean isDraft() {
        return isDraft != null && isDraft;
    }

    /**
     * Vérifie si le message a été envoyé
     */
    public boolean isSent() {
        return sentAt != null && !isDraft();
    }

    /**
     * Vérifie si le message a été délivré
     */
    public boolean isDelivered() {
        return deliveredAt != null;
    }

    /**
     * Vérifie si le message a été lu
     */
    public boolean isRead() {
        return readAt != null && !readBy.isEmpty();
    }

    /**
     * Vérifie si le message a un sujet
     */
    public boolean hasSubject() {
        return subject != null && !subject.isEmpty();
    }

    /**
     * Vérifie si le message a un corps
     */
    public boolean hasBody() {
        return body != null && !body.isEmpty();
    }

    /**
     * Vérifie si le message a des destinataires
     */
    public boolean hasRecipients() {
        return recipientIds != null && !recipientIds.isEmpty();
    }

    /**
     * Retourne le nombre de destinataires
     */
    public int getRecipientsCount() {
        return recipientIds != null ? recipientIds.size() : 0;
    }

    /**
     * Vérifie si le message a une pièce jointe
     */
    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.isEmpty();
    }

    /**
     * Vérifie si le message est une réponse
     */
    public boolean isReply() {
        return replyToMessageId != null && !replyToMessageId.isEmpty();
    }

    /**
     * Ajoute un destinataire
     */
    public void addRecipient(String recipientId) {
        if (recipientIds == null) {
            recipientIds = new ArrayList<>();
        }
        if (!recipientIds.contains(recipientId)) {
            recipientIds.add(recipientId);
        }
    }

    /**
     * Ajoute plusieurs destinataires
     */
    public void addRecipients(List<String> recipientIds) {
        if (recipientIds == null) {
            this.recipientIds = new ArrayList<>();
        }
        for (String recipientId : recipientIds) {
            addRecipient(recipientId);
        }
    }

    /**
     * Marque le message comme lu par un utilisateur
     */
    public void markAsRead(String userId) {
        if (readBy == null) {
            readBy = new ArrayList<>();
        }
        if (!readBy.contains(userId)) {
            readBy.add(userId);
        }
        this.readAt = LocalDateTime.now();
    }

    /**
     * Marque le message comme délivré
     */
    public void markAsDelivered() {
        this.deliveredAt = LocalDateTime.now();
    }

    /**
     * Envoie le message (passe de brouillon à envoyé)
     */
    public void send() {
        this.isDraft = false;
        this.sentAt = LocalDateTime.now();
    }

    /**
     * Retourne le nombre de lecteurs
     */
    public int getReadCount() {
        return readBy != null ? readBy.size() : 0;
    }

    /**
     * Vérifie si un utilisateur a lu le message
     */
    public boolean isReadBy(String userId) {
        return readBy != null && readBy.contains(userId);
    }

    /**
     * Vérifie si un utilisateur est destinataire
     */
    public boolean isRecipient(String userId) {
        return recipientIds != null && recipientIds.contains(userId);
    }

    /**
     * Retourne le temps depuis l'envoi
     */
    public String getTimeSinceSent() {
        if (sentAt == null) {
            return "Non envoyé";
        }
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(sentAt, now).toMinutes();

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
     * Retourne le statut du message
     */
    public String getStatus() {
        if (isDraft()) {
            return "BROUILLON";
        }
        if (!isSent()) {
            return "NON_ENVOYE";
        }
        if (isRead()) {
            return "LU";
        }
        if (isDelivered()) {
            return "DELIVRE";
        }
        return "ENVOYE";
    }

    /**
     * Retourne un résumé du message
     */
    public String getSummary() {
        String bodyPreview = body != null && body.length() > 100
                ? body.substring(0, 100) + "..."
                : body != null ? body : "";

        return String.format("%s - %s",
                subject != null ? subject : "Sans sujet",
                bodyPreview);
    }

    /**
     * Met à jour le sujet
     */
    public void updateSubject(String newSubject) {
        if (newSubject != null && !newSubject.trim().isEmpty()) {
            this.subject = newSubject.trim();
        }
    }

    /**
     * Met à jour le corps du message
     */
    public void updateBody(String newBody) {
        if (newBody != null && !newBody.trim().isEmpty()) {
            this.body = newBody;
        }
    }
}