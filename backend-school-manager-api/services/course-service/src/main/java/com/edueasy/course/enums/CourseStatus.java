package com.edueasy.course.enums;

import lombok.Getter;

@Getter
public enum CourseStatus {
    DRAFT("Brouillon", "gray", "fa-pencil"),
    ACTIVE("Actif", "green", "fa-play"),
    IN_PROGRESS("En cours", "blue", "fa-spinner"),
    COMPLETED("Terminé", "purple", "fa-check-circle"),
    ARCHIVED("Archivé", "gray", "fa-archive"),
    PUBLISHED("Publié", "green", "fa-globe");

    private final String label;
    private final String color;
    private final String icon;

    CourseStatus(String label, String color, String icon) {
        this.label = label;
        this.color = color;
        this.icon = icon;
    }

    public boolean isActive() {
        return this == ACTIVE || this == IN_PROGRESS || this == PUBLISHED;
    }

    public boolean isFinished() {
        return this == COMPLETED || this == ARCHIVED;
    }

    public boolean isEditable() {
        return this == DRAFT || this == ACTIVE;
    }

    public boolean isPublished() {
        return this == PUBLISHED || this == ACTIVE;
    }

    public static CourseStatus fromString(String value) {
        try {
            return CourseStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}