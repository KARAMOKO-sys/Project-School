package com.edueasy.school.model;

import lombok.Getter;

@Getter
public enum SchoolType {
    PRIMARY("École primaire"),
    MIDDLE("Collège"),
    HIGH("Lycée"),
    UNIVERSITY("Université"),
    INSTITUTE("Institut"),
    LANGUAGE_SCHOOL("École de langues"),
    VOCATIONAL("École professionnelle"),
    ONLINE("École en ligne"),
    SPECIALIZED("École spécialisée"),
    OTHER("Autre");

    private final String label;

    SchoolType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isPrimary() {
        return this == PRIMARY;
    }

    public boolean isSecondary() {
        return this == MIDDLE || this == HIGH;
    }

    public boolean isHigherEducation() {
        return this == UNIVERSITY || this == INSTITUTE;
    }

    public boolean isVocational() {
        return this == VOCATIONAL || this == SPECIALIZED;
    }

    public static SchoolType fromString(String value) {
        try {
            return SchoolType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}