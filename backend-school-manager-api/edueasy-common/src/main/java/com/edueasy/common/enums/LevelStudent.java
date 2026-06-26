package com.edueasy.common.enums;

import lombok.Getter;

@Getter
public enum LevelStudent {
    DEBUTANT("Débutant", 1, "Aucune connaissance requise"),
    INTERMEDIAIRE("Intermédiaire", 2, "Connaissances de base requises"),
    AVANCE("Avancé", 3, "Bonne maîtrise requise"),
    EXPERT("Expert", 4, "Maîtrise avancée requise"),
    TOUS_NIVEAUX("Tous niveaux", 0, "Accessible à tous");

    private final String label;
    private final int level;
    private final String description;

    LevelStudent(String label, int level, String description) {
        this.label = label;
        this.level = level;
        this.description = description;
    }

    public boolean isBeginnerFriendly() {
        return this == DEBUTANT || this == TOUS_NIVEAUX;
    }

    public boolean isAdvanced() {
        return this == AVANCE || this == EXPERT;
    }

    public boolean isIntermediate() {
        return this == INTERMEDIAIRE;
    }

    public static LevelStudent fromString(String value) {
        try {
            return LevelStudent.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static LevelStudent fromLevel(int level) {
        for (LevelStudent l : values()) {
            if (l.level == level) {
                return l;
            }
        }
        return null;
    }
}