package com.edueasy.common.enums;

import lombok.Getter;

@Getter
public enum TicketStatus {
    OPEN("Ouvert", "blue", "fa-circle"),
    IN_PROGRESS("En cours", "yellow", "fa-spinner"),
    RESOLVED("Résolu", "green", "fa-check-circle"),
    CLOSED("Fermé", "gray", "fa-times-circle"),
    REOPENED("Réouvert", "orange", "fa-undo"),
    ESCALATED("Escaladé", "red", "fa-arrow-up"),
    ON_HOLD("En attente", "purple", "fa-pause"),
    CANCELLED("Annulé", "dark", "fa-ban");

    private final String label;
    private final String color;
    private final String icon;

    TicketStatus(String label, String color, String icon) {
        this.label = label;
        this.color = color;
        this.icon = icon;
    }

    public boolean isActive() {
        return this == OPEN || this == IN_PROGRESS || this == ESCALATED || this == ON_HOLD;
    }

    public boolean isResolved() {
        return this == RESOLVED || this == CLOSED;
    }

    public boolean isOpen() {
        return this == OPEN || this == REOPENED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }

    public boolean isOnHold() {
        return this == ON_HOLD;
    }

    public boolean isEscalated() {
        return this == ESCALATED;
    }

    public boolean isEditable() {
        return this == OPEN || this == IN_PROGRESS || this == ON_HOLD;
    }

    public static TicketStatus fromString(String value) {
        try {
            return TicketStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}