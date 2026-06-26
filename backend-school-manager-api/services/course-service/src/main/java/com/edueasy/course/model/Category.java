package com.edueasy.course.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "categories",
        indexes = {
                @Index(name = "idx_category_name", columnList = "name"),
                @Index(name = "idx_category_parent", columnList = "parent_id"),
                @Index(name = "idx_category_active", columnList = "active"),
                @Index(name = "idx_category_display_order", columnList = "display_order")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String icon;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    @Builder.Default
    private Boolean active = true;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "color")
    private String color;

    @Column(name = "level")
    @Builder.Default
    private Integer level = 0;

    @Column(name = "full_path")
    private String fullPath;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
        if (active == null) {
            active = true;
        }
        if (level == null) {
            level = 0;
        }
        generateSlug();
        updateFullPath();
    }

    @PreUpdate
    protected void onUpdate() {
        generateSlug();
        updateFullPath();
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si la catégorie est active
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Vérifie si la catégorie est une racine (sans parent)
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Vérifie si la catégorie a des enfants
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * Retourne le nombre d'enfants
     */
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }

    /**
     * Vérifie si la catégorie a un parent
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Vérifie si la catégorie a une description
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Vérifie si la catégorie a une icône
     */
    public boolean hasIcon() {
        return icon != null && !icon.isEmpty();
    }

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
     * Met à jour le chemin complet
     */
    public void updateFullPath() {
        if (parent == null) {
            this.fullPath = "/" + (slug != null ? slug : "uncategorized");
            this.level = 0;
        } else {
            String parentPath = parent.getFullPath() != null ? parent.getFullPath() : "";
            this.fullPath = parentPath + "/" + (slug != null ? slug : "uncategorized");
            this.level = parent.getLevel() + 1;
        }
    }

    /**
     * Ajoute un enfant
     */
    public void addChild(Category child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
            child.updateFullPath();
        }
    }

    /**
     * Supprime un enfant
     */
    public void removeChild(Category child) {
        if (children != null) {
            children.remove(child);
            child.setParent(null);
            child.updateFullPath();
        }
    }

    /**
     * Active la catégorie
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Désactive la catégorie
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Vérifie si la catégorie est une sous-catégorie directe
     */
    public boolean isDirectChildOf(Category category) {
        return parent != null && parent.equals(category);
    }

    /**
     * Vérifie si la catégorie est une sous-catégorie (directe ou indirecte)
     */
    public boolean isDescendantOf(Category category) {
        if (parent == null) {
            return false;
        }
        if (parent.equals(category)) {
            return true;
        }
        return parent.isDescendantOf(category);
    }

    /**
     * Retourne le nom complet avec le chemin
     */
    public String getFullName() {
        if (parent == null) {
            return name;
        }
        return parent.getFullName() + " > " + name;
    }

    /**
     * Retourne le niveau de profondeur
     */
    public int getDepth() {
        if (parent == null) {
            return 0;
        }
        return parent.getDepth() + 1;
    }

    /**
     * Vérifie si la catégorie est au premier niveau
     */
    public boolean isTopLevel() {
        return getDepth() == 0;
    }

    /**
     * Vérifie si la catégorie est au deuxième niveau
     */
    public boolean isSecondLevel() {
        return getDepth() == 1;
    }

    /**
     * Met à jour le nom et génère un nouveau slug
     */
    public void updateName(String newName) {
        if (newName != null && !newName.trim().isEmpty()) {
            this.name = newName.trim();
            generateSlug();
            updateFullPath();
        }
    }

    /**
     * Met à jour l'ordre d'affichage
     */
    public void updateDisplayOrder(int order) {
        this.displayOrder = order;
    }
}