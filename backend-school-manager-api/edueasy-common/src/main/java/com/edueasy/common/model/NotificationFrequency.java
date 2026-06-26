package com.edueasy.common.model;

import lombok.Getter;

@Getter
public enum NotificationFrequency {
    INSTANT("Instantané", "Notifications en temps réel"),
    DAILY("Quotidien", "Résumé quotidien"),
    WEEKLY("Hebdomadaire", "Résumé hebdomadaire"),
    NEVER("Jamais", "Désactivé");

    private final String label;
    private final String description;

    NotificationFrequency(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public boolean isRealtime() {
        return this == INSTANT;
    }

    public boolean isSummary() {
        return this == DAILY || this == WEEKLY;
    }

    public boolean isDisabled() {
        return this == NEVER;
    }

    public String getFrequencyCode() {
        return this.name();
    }

    public static NotificationFrequency fromString(String value) {
        try {
            return NotificationFrequency.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static NotificationFrequency fromLabel(String label) {
        for (NotificationFrequency freq : values()) {
            if (freq.label.equalsIgnoreCase(label)) {
                return freq;
            }
        }
        return null;
    }
}