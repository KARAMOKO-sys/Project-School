package com.edueasy.communication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "conversations",
        indexes = {
                @Index(name = "idx_conversation_type", columnList = "type"),
                @Index(name = "idx_conversation_last_message", columnList = "last_message_at"),
                @Index(name = "idx_conversation_updated", columnList = "updated_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @ElementCollection
    @CollectionTable(
            name = "conversation_participants",
            joinColumns = @JoinColumn(name = "conversation_id")
    )
    @Column(name = "participant_id")
    @Builder.Default
    private List<String> participantIds = new ArrayList<>();

    private String title;

    @Enumerated(EnumType.STRING)
    private ConversationType type;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_archived")
    @Builder.Default
    private Boolean isArchived = false;

    @Column(name = "message_count")
    @Builder.Default
    private Integer messageCount = 0;

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
        if (participantIds == null) {
            participantIds = new ArrayList<>();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (isArchived == null) {
            isArchived = false;
        }
        if (messageCount == null) {
            messageCount = 0;
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si la conversation est une conversation privée (1-1)
     */
    public boolean isPrivate() {
        return type == ConversationType.PRIVATE;
    }

    /**
     * Vérifie si la conversation est un groupe
     */
    public boolean isGroup() {
        return type == ConversationType.GROUP;
    }

    /**
     * Vérifie si la conversation est un canal
     */
    public boolean isChannel() {
        return type == ConversationType.CHANNEL;
    }

    /**
     * Vérifie si la conversation est active
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Vérifie si la conversation est archivée
     */
    public boolean isArchived() {
        return isArchived != null && isArchived;
    }

    /**
     * Vérifie si la conversation a un titre
     */
    public boolean hasTitle() {
        return title != null && !title.isEmpty();
    }

    /**
     * Vérifie si la conversation a des participants
     */
    public boolean hasParticipants() {
        return participantIds != null && !participantIds.isEmpty();
    }

    /**
     * Retourne le nombre de participants
     */
    public int getParticipantsCount() {
        return participantIds != null ? participantIds.size() : 0;
    }

    /**
     * Ajoute un participant
     */
    public void addParticipant(String participantId) {
        if (participantIds == null) {
            participantIds = new ArrayList<>();
        }
        if (!participantIds.contains(participantId)) {
            participantIds.add(participantId);
        }
    }

    /**
     * Ajoute plusieurs participants
     */
    public void addParticipants(List<String> participantIds) {
        if (participantIds == null) {
            this.participantIds = new ArrayList<>();
        }
        for (String participantId : participantIds) {
            addParticipant(participantId);
        }
    }

    /**
     * Supprime un participant
     */
    public void removeParticipant(String participantId) {
        if (participantIds != null) {
            participantIds.remove(participantId);
        }
    }

    /**
     * Vérifie si un participant est dans la conversation
     */
    public boolean hasParticipant(String participantId) {
        return participantIds != null && participantIds.contains(participantId);
    }

    /**
     * Met à jour le dernier message
     */
    public void updateLastMessage() {
        this.lastMessageAt = LocalDateTime.now();
    }

    /**
     * Incrémente le compteur de messages
     */
    public void incrementMessageCount() {
        if (messageCount == null) {
            messageCount = 0;
        }
        messageCount++;
        updateLastMessage();
    }

    /**
     * Archive la conversation
     */
    public void archive() {
        this.isArchived = true;
    }

    /**
     * Désarchive la conversation
     */
    public void unarchive() {
        this.isArchived = false;
    }

    /**
     * Active la conversation
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Désactive la conversation
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Retourne le nom d'affichage de la conversation
     */
    public String getDisplayName() {
        if (title != null && !title.isEmpty()) {
            return title;
        }
        if (isPrivate() && participantIds != null && participantIds.size() == 2) {
            // Pour une conversation privée, on peut retourner le nom de l'autre participant
            return "Conversation privée";
        }
        if (isGroup()) {
            return "Groupe (" + getParticipantsCount() + " participants)";
        }
        return "Conversation";
    }

    /**
     * Vérifie si la conversation est récente (moins de 7 jours depuis le dernier message)
     */
    public boolean isRecent() {
        if (lastMessageAt == null) {
            return false;
        }
        return lastMessageAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * Vérifie si la conversation est ancienne (plus de 30 jours sans message)
     */
    public boolean isOld() {
        if (lastMessageAt == null) {
            return true;
        }
        return lastMessageAt.isBefore(LocalDateTime.now().minusDays(30));
    }

    /**
     * Retourne le temps depuis le dernier message
     */
    public String getTimeSinceLastMessage() {
        if (lastMessageAt == null) {
            return "Aucun message";
        }
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(lastMessageAt, now).toMinutes();

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
     * Met à jour le titre
     */
    public void updateTitle(String newTitle) {
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            this.title = newTitle.trim();
        }
    }
}