package com.edueasy.course.enums;

import lombok.Getter;

@Getter
public enum EnrollmentStatus {
    PENDING("En attente", "yellow", "fa-clock"),
    ENROLLED("Inscrit", "blue", "fa-check-circle"),
    IN_PROGRESS("En cours", "cyan", "fa-spinner"),
    COMPLETED("Terminé", "green", "fa-check-double"),
    CANCELLED("Annulé", "red", "fa-times-circle"),
    DROPPED("Abandonné", "orange", "fa-user-slash"),
    SUSPENDED("Suspendu", "gray", "fa-pause-circle");

    private final String label;
    private final String color;
    private final String icon;

    EnrollmentStatus(String label, String color, String icon) {
        this.label = label;
        this.color = color;
        this.icon = icon;
    }

    public boolean isActive() {
        return this == ENROLLED || this == IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isTerminated() {
        return this == CANCELLED || this == DROPPED || this == SUSPENDED;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean canResume() {
        return this == SUSPENDED || this == PENDING;
    }

    public boolean isInactive() {
        return isTerminated() || isCompleted();
    }

    public static EnrollmentStatus fromString(String value) {
        try {
            return EnrollmentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}