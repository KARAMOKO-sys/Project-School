package com.edueasy.common.enums;

public enum UserStatus {
    PENDING("En attente de validation"),
    ACTIVE("Actif"),
    INACTIVE("Inactif"),
    SUSPENDED("Suspendu"),
    BANNED("Banni"),
    DELETED("Supprimé"),
    ARCHIVED("Archivé");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isBlocked() {
        return this == SUSPENDED || this == BANNED;
    }

    public boolean canLogin() {
        return this == ACTIVE || this == PENDING;
    }

    public boolean isEditable() {
        return this != DELETED && this != ARCHIVED;
    }
}