package com.edueasy.common.enums;

import lombok.Getter;

@Getter
public enum EnrollmentStatus {
    ENROLLED("Inscrit", "blue", "fa-check-circle"),
    ACTIVE("Actif", "green", "fa-user-check"),
    SUSPENDED("Suspendu", "orange", "fa-pause-circle"),
    WITHDRAWN("Retiré", "red", "fa-times-circle"),
    GRADUATED("Diplômé", "purple", "fa-graduation-cap"),
    TRANSFERRED("Transféré", "gray", "fa-exchange-alt"),
    EXPELLED("Exclu", "dark", "fa-ban"),
    DROPPED_OUT("Abandonné", "red", "fa-user-slash"),
    ON_LEAVE("En congé", "yellow", "fa-clock"),
    DEFERRED("Différé", "teal", "fa-hourglass-half"),
    PENDING("En attente", "yellow", "fa-clock"),
    IN_PROGRESS("En cours", "cyan", "fa-spinner"),
    COMPLETED("Terminé", "green", "fa-check-double"),
    CANCELLED("Annulé", "red", "fa-times-circle"),
    DROPPED("Abandonné", "orange", "fa-user-slash");

    private final String label;
    private final String color;
    private final String icon;

    EnrollmentStatus(String label, String color, String icon) {
        this.label = label;
        this.color = color;
        this.icon = icon;
    }

    public boolean isActive() {
        return this == ENROLLED || this == ACTIVE || this == IN_PROGRESS;
    }

    public boolean isInactive() {
        return this == SUSPENDED || this == WITHDRAWN || this == DROPPED_OUT || this == EXPELLED;
    }

    public boolean isCompleted() {
        return this == GRADUATED || this == TRANSFERRED || this == COMPLETED;
    }

    public boolean isTemporary() {
        return this == ON_LEAVE || this == DEFERRED || this == SUSPENDED;
    }

    public boolean canEnroll() {
        return this == ENROLLED || this == ACTIVE || this == DEFERRED || this == PENDING;
    }

    public boolean canAccessCourses() {
        return this == ENROLLED || this == ACTIVE || this == IN_PROGRESS;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isDropped() {
        return this == DROPPED;
    }

    public static EnrollmentStatus fromString(String value) {
        try {
            return EnrollmentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}