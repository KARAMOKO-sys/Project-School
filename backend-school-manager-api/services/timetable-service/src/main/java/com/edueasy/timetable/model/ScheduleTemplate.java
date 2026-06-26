package com.edueasy.timetable.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "schedule_templates",
        indexes = {
                @Index(name = "idx_schedule_template_school", columnList = "school_id"),
                @Index(name = "idx_schedule_template_name", columnList = "name"),
                @Index(name = "idx_schedule_template_active", columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "school_id")
    private String schoolId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @ElementCollection
    @Builder.Default
    private List<WeeklySlot> weeklySlots = new ArrayList<>();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "is_system")
    @Builder.Default
    private Boolean isSystem = false;

    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "applied_count")
    @Builder.Default
    private Integer appliedCount = 0;

    @Column(name = "last_applied_at")
    private LocalDateTime lastAppliedAt;

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
        if (isActive == null) {
            isActive = true;
        }
        if (isDefault == null) {
            isDefault = false;
        }
        if (isSystem == null) {
            isSystem = false;
        }
        if (version == null) {
            version = 1;
        }
        if (appliedCount == null) {
            appliedCount = 0;
        }
        if (weeklySlots == null) {
            weeklySlots = new ArrayList<>();
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si le template est actif
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Vérifie si le template est par défaut
     */
    public boolean isDefault() {
        return isDefault != null && isDefault;
    }

    /**
     * Vérifie si le template est système
     */
    public boolean isSystem() {
        return isSystem != null && isSystem;
    }

    /**
     * Vérifie si le template est valide
     */
    public boolean isValid() {
        if (validFrom != null && validUntil != null) {
            LocalDateTime now = LocalDateTime.now();
            return !now.isBefore(validFrom) && !now.isAfter(validUntil);
        }
        return true;
    }

    /**
     * Vérifie si le template a des créneaux
     */
    public boolean hasSlots() {
        return weeklySlots != null && !weeklySlots.isEmpty();
    }

    /**
     * Retourne le nombre de créneaux
     */
    public int getSlotsCount() {
        return weeklySlots != null ? weeklySlots.size() : 0;
    }

    /**
     * Active le template
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Désactive le template
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Définit comme template par défaut
     */
    public void setAsDefault() {
        this.isDefault = true;
    }

    /**
     * Retire le statut par défaut
     */
    public void removeDefault() {
        this.isDefault = false;
    }

    /**
     * Ajoute un créneau
     */
    public void addSlot(WeeklySlot slot) {
        if (weeklySlots == null) {
            weeklySlots = new ArrayList<>();
        }
        if (!weeklySlots.contains(slot)) {
            weeklySlots.add(slot);
        }
    }

    /**
     * Supprime un créneau
     */
    public void removeSlot(WeeklySlot slot) {
        if (weeklySlots != null) {
            weeklySlots.remove(slot);
        }
    }

    /**
     * Supprime tous les créneaux
     */
    public void clearSlots() {
        if (weeklySlots != null) {
            weeklySlots.clear();
        }
    }

    /**
     * Incrémente le compteur d'applications
     */
    public void incrementApplied() {
        if (appliedCount == null) {
            appliedCount = 0;
        }
        appliedCount++;
        this.lastAppliedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si le template est nouveau (créé il y a moins de 7 jours)
     */
    public boolean isNew() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * Vérifie si le template est populaire (appliqué plus de 10 fois)
     */
    public boolean isPopular() {
        return appliedCount != null && appliedCount > 10;
    }

    /**
     * Retourne le statut du template
     */
    public String getStatusLabel() {
        if (!isActive()) {
            return "Inactif";
        }
        if (!isValid()) {
            return "Expiré";
        }
        if (isDefault()) {
            return "Par défaut";
        }
        return "Actif";
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (!isActive()) {
            return "gray";
        }
        if (!isValid()) {
            return "red";
        }
        if (isDefault()) {
            return "gold";
        }
        return "green";
    }

    /**
     * Retourne le nom du template avec le statut
     */
    public String getDisplayName() {
        if (isDefault()) {
            return name + " (Défaut)";
        }
        if (isSystem()) {
            return name + " (Système)";
        }
        return name;
    }

    /**
     * Vérifie si le template est complet (a au moins 5 créneaux)
     */
    public boolean isComplete() {
        return getSlotsCount() >= 5;
    }

    /**
     * Vérifie si le template peut être dupliqué
     */
    public boolean isDuplicable() {
        return isActive() && hasSlots();
    }

    /**
     * Crée une copie du template
     */
    public ScheduleTemplate createCopy(String newName) {
        ScheduleTemplate copy = new ScheduleTemplate();
        copy.setUuid(UUID.randomUUID().toString());
        copy.setSchoolId(this.schoolId);
        copy.setName(newName != null ? newName : "Copie de " + this.name);
        copy.setDescription("Copie de " + this.name);
        copy.setWeeklySlots(new ArrayList<>(this.weeklySlots));
        copy.setIsActive(false);
        copy.setIsDefault(false);
        copy.setIsSystem(false);
        copy.setVersion(1);
        copy.setValidFrom(this.validFrom);
        copy.setValidUntil(this.validUntil);
        return copy;
    }

    /**
     * Met à jour la version
     */
    public void incrementVersion() {
        if (version == null) {
            version = 1;
        }
        version++;
    }
}