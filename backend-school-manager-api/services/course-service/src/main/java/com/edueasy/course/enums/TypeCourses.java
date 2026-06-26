package com.edueasy.course.enums;

import lombok.Getter;

@Getter
public enum TypeCourses {
    THEORIQUE("Théorique", "fa-book", "Cours théorique"),
    PRATIQUE("Pratique", "fa-tools", "Cours pratique"),
    MIXTE("Mixte", "fa-layer-group", "Cours théorique et pratique"),
    ATELIER("Atelier", "fa-users-cog", "Atelier pratique"),
    LABORATOIRE("Laboratoire", "fa-flask", "Travaux en laboratoire"),
    EN_LIGNE("En ligne", "fa-globe", "Cours à distance");

    private final String label;
    private final String icon;
    private final String description;

    TypeCourses(String label, String icon, String description) {
        this.label = label;
        this.icon = icon;
        this.description = description;
    }

    public boolean isPractical() {
        return this == PRATIQUE || this == ATELIER || this == LABORATOIRE;
    }

    public boolean isTheoretical() {
        return this == THEORIQUE;
    }

    public boolean isOnline() {
        return this == EN_LIGNE;
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