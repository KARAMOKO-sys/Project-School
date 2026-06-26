package com.edueasy.common.enums;

import lombok.Getter;

@Getter
public enum LevelTeacher {
    DEBUTANT("Débutant", 0, 2, "En formation", "fa-star"),
    INTERMEDIAIRE("Intermédiaire", 3, 5, "Autonome", "fa-star-half-alt"),
    CONFIRME("Confirmé", 6, 10, "Expérimenté", "fa-star"),
    EXPERIMENTE("Expérimenté", 6, 10, "Très expérimenté", "fa-star"),
    SENIOR("Sénior", 11, 15, "Très expérimenté", "fa-star"),
    EXPERT("Expert", 16, 99, "Référence", "fa-crown");

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
        try {
            return LevelTeacher.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}