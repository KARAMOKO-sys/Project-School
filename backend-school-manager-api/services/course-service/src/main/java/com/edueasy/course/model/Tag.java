package com.edueasy.course.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "tags",
        indexes = {
                @Index(name = "idx_tag_name", columnList = "name")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 20)
    @Builder.Default
    private String color = "#6B7280";

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "icon")
    private String icon;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 0;

    @Column(name = "slug", unique = true)
    private String slug;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (color == null || color.isEmpty()) {
            color = "#6B7280";
        }
        if (isActive == null) {
            isActive = true;
        }
        if (usageCount == null) {
            usageCount = 0;
        }
        generateSlug();
    }

    @PreUpdate
    protected void onUpdate() {
        generateSlug();
    }

    // ===== Méthodes métier =====

    /**
     * Génère le slug à partir du nom
     */
    public void generateSlug() {
        if (name != null && !name.isEmpty()) {
            this.slug = name.toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-")
                    .trim();
        }
    }

    /**
     * Vérifie si le tag est actif
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Vérifie si le tag a une description
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Vérifie si le tag a une icône
     */
    public boolean hasIcon() {
        return icon != null && !icon.isEmpty();
    }

    /**
     * Vérifie si le tag a une couleur
     */
    public boolean hasColor() {
        return color != null && !color.isEmpty();
    }

    /**
     * Active le tag
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Désactive le tag
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Incrémente le compteur d'utilisation
     */
    public void incrementUsage() {
        if (usageCount == null) {
            usageCount = 0;
        }
        usageCount++;
    }

    /**
     * Décrémente le compteur d'utilisation
     */
    public void decrementUsage() {
        if (usageCount != null && usageCount > 0) {
            usageCount--;
        }
    }

    /**
     * Met à jour le nom et génère un nouveau slug
     */
    public void updateName(String newName) {
        if (newName != null && !newName.trim().isEmpty()) {
            this.name = newName.trim();
            generateSlug();
        }
    }

    /**
     * Met à jour la couleur
     */
    public void updateColor(String newColor) {
        if (newColor != null && !newColor.isEmpty()) {
            this.color = newColor;
        }
    }

    /**
     * Vérifie si le tag est valide
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty()
                && color != null && !color.isEmpty();
    }

    /**
     * Retourne le nom formaté (première lettre en majuscule)
     */
    public String getFormattedName() {
        if (name == null) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    /**
     * Vérifie si le tag est populaire (utilisé plus de 10 fois)
     */
    public boolean isPopular() {
        return usageCount != null && usageCount > 10;
    }

    /**
     * Vérifie si le tag est très populaire (utilisé plus de 50 fois)
     */
    public boolean isVeryPopular() {
        return usageCount != null && usageCount > 50;
    }

    /**
     * Retourne le niveau de popularité
     */
    public String getPopularityLevel() {
        if (usageCount == null) {
            return "N/A";
        }
        if (usageCount > 50) {
            return "Très populaire";
        }
        if (usageCount > 10) {
            return "Populaire";
        }
        if (usageCount > 0) {
            return "Utilisé";
        }
        return "Non utilisé";
    }

    /**
     * Vérifie si le tag a un slug
     */
    public boolean hasSlug() {
        return slug != null && !slug.isEmpty();
    }
}