package com.edueasy.common.enums;

import lombok.Getter;

@Getter
public enum StatutUserSimple {
    BROUILLON("Brouillon", "blue", "fa-pencil"),
    ACTIF("Actif", "green", "fa-check-circle"),
    INACTIF("Inactif", "gray", "fa-circle"),
    EN_ATTENTE("En attente", "yellow", "fa-clock"),
    SUSPENDU("Suspendu", "orange", "fa-pause-circle"),
    SUPPRIME("Supprimé", "red", "fa-times-circle"),
    ENSEIGNANT("Enseignant", "green", "fa-chalkboard-teacher");

    private final String libelle;
    private final String color;
    private final String icon;

    StatutUserSimple(String libelle, String color, String icon) {
        this.libelle = libelle;
        this.color = color;
        this.icon = icon;
    }

    public boolean isActive() {
        return this == ACTIF || this == ENSEIGNANT;
    }

    public boolean isInactive() {
        return this == INACTIF || this == SUPPRIME;
    }

    public boolean isPending() {
        return this == EN_ATTENTE;
    }

    public boolean isSuspended() {
        return this == SUSPENDU;
    }

    public boolean isEditable() {
        return this != SUPPRIME;
    }

    public boolean canLogin() {
        return this == ACTIF || this == EN_ATTENTE || this == ENSEIGNANT;
    }

    public boolean isTeacher() {
        return this == ENSEIGNANT;
    }

    public static StatutUserSimple fromUserStatus(UserStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case ACTIVE -> ACTIF;
            case INACTIVE -> INACTIF;
            case SUSPENDED -> SUSPENDU;
            case BANNED -> SUPPRIME;
            case PENDING -> EN_ATTENTE;
            case DELETED -> SUPPRIME;
            case ARCHIVED -> SUPPRIME;
        };
    }

    /**
     * Convertit StatutUserSimple en UserStatus
     * Tous les cas sont couverts
     */
    public UserStatus toUserStatus() {
        if (this == ACTIF || this == ENSEIGNANT) {
            return UserStatus.ACTIVE;
        }
        if (this == INACTIF) {
            return UserStatus.INACTIVE;
        }
        if (this == EN_ATTENTE || this == BROUILLON) {
            return UserStatus.PENDING;
        }
        if (this == SUSPENDU) {
            return UserStatus.SUSPENDED;
        }
        if (this == SUPPRIME) {
            return UserStatus.DELETED;
        }
        return UserStatus.PENDING;
    }

    public static StatutUserSimple fromString(String value) {
        try {
            return StatutUserSimple.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}