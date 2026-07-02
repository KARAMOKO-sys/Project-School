package com.edueasy.common.enums;

import lombok.Getter;

@Getter
public enum LevelTeacher {
    DEBUTANT("Débutant", 0, 2, "En formation", "fa-star"),
    INTERMEDIAIRE("Intermédiaire", 3, 5, "Autonome", "fa-star-half-alt"),
    CONFIRME("Confirmé", 6, 10, "Expérimenté", "fa-star"),
    EXPERIMENTE("Expérimenté", 6, 10, "Très expérimenté", "fa-star"),
    SENIOR("Sénior", 11, 15, "Très expérimenté", "fa-star"),
    EXPERT("Expert", 16, 99, "Référence", "fa-crown"),

    // 🔥 AJOUT DES VALEURS POUR LA COMPATIBILITÉ FRONTEND
    MIDDLE("Intermédiaire", 3, 5, "Niveau intermédiaire", "fa-star-half-alt"),
    ASSISTANT("Assistant", 0, 2, "En tant qu'assistant", "fa-user-plus"),
    JUNIOR("Junior", 0, 2, "Débutant dans le métier", "fa-user-graduate"),
    PROFESSOR("Professeur", 6, 15, "Professeur confirmé", "fa-chalkboard-teacher"),
    OTHER("Autre", 0, 99, "Autre niveau", "fa-question-circle");

    private final String label;
    private final int minYearsExperience;
    private final int maxYearsExperience;
    private final String description;
    private final String icon;

    LevelTeacher(String label, int minYearsExperience, int maxYearsExperience, String description, String icon) {
        this.label = label;
        this.minYearsExperience = minYearsExperience;
        this.maxYearsExperience = maxYearsExperience;
        this.description = description;
        this.icon = icon;
    }

    public boolean isWithinRange(int years) {
        return years >= this.minYearsExperience && years <= this.maxYearsExperience;
    }

    public String getRangeDisplay() {
        if (maxYearsExperience >= 99) {
            return minYearsExperience + "+ ans";
        }
        return minYearsExperience + " - " + maxYearsExperience + " ans";
    }

    public static LevelTeacher fromYearsExperience(int years) {
        if (years <= 2) return DEBUTANT;
        if (years <= 5) return INTERMEDIAIRE;
        if (years <= 10) return EXPERIMENTE;
        if (years <= 15) return SENIOR;
        return EXPERT;
    }

    public static LevelTeacher fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LevelTeacher.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Mapping des valeurs du frontend vers les valeurs existantes
            return switch (value.toUpperCase()) {
                case "MIDDLE" -> INTERMEDIAIRE;
                case "ASSISTANT" -> DEBUTANT;
                case "JUNIOR" -> DEBUTANT;
                case "PROFESSOR" -> EXPERT;
                case "OTHER" -> DEBUTANT;
                default -> null;
            };
        }
    }

    /**
     * Vérifie si le niveau est valide
     */
    public static boolean isValid(String value) {
        if (value == null) return false;
        try {
            LevelTeacher.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retourne le niveau par défaut
     */
    public static LevelTeacher getDefault() {
        return DEBUTANT;
    }
}