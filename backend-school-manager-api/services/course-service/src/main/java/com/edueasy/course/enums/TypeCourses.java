package com.edueasy.course.enums;

import lombok.Getter;

@Getter
public enum TypeCourses {
    THEORIQUE("Théorique", "fa-book", "Cours théorique"),
    PRATIQUE("Pratique", "fa-tools", "Cours pratique"),
    MIXTE("Mixte", "fa-layer-group", "Cours théorique et pratique"),
    ATELIER("Atelier", "fa-users-cog", "Atelier pratique");

    // Supprimer LABORATOIRE et EN_LIGNE

    private final String label;
    private final String icon;
    private final String description;

    TypeCourses(String label, String icon, String description) {
        this.label = label;
        this.icon = icon;
        this.description = description;
    }

    public boolean isPractical() {
        return this == PRATIQUE || this == ATELIER;
    }

    public boolean isTheoretical() {
        return this == THEORIQUE;
    }

    public boolean isMixed() {
        return this == MIXTE;
    }

    public static TypeCourses fromString(String value) {
        try {
            return TypeCourses.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}