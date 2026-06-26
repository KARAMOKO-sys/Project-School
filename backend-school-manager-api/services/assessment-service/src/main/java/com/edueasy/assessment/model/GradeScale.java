package com.edueasy.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeScale {

    private String name;
    private String description;
    private List<GradeThreshold> thresholds;

    /**
     * Crée une échelle de notation standard (A, B, C, D, F)
     */
    public static GradeScale createStandardScale() {
        List<GradeThreshold> thresholds = new ArrayList<>();
        thresholds.add(new GradeThreshold("A", 90.0, "Excellent"));
        thresholds.add(new GradeThreshold("B", 80.0, "Bien"));
        thresholds.add(new GradeThreshold("C", 70.0, "Moyen"));
        thresholds.add(new GradeThreshold("D", 60.0, "Passable"));
        thresholds.add(new GradeThreshold("F", 0.0, "Échec"));

        return GradeScale.builder()
                .name("Standard")
                .description("Échelle de notation standard (A, B, C, D, F)")
                .thresholds(thresholds)
                .build();
    }

    /**
     * Crée une échelle de notation européenne (A, B, C, D, E, F)
     */
    public static GradeScale createEuropeanScale() {
        List<GradeThreshold> thresholds = new ArrayList<>();
        thresholds.add(new GradeThreshold("A", 90.0, "Très bien"));
        thresholds.add(new GradeThreshold("B", 80.0, "Bien"));
        thresholds.add(new GradeThreshold("C", 70.0, "Assez bien"));
        thresholds.add(new GradeThreshold("D", 60.0, "Passable"));
        thresholds.add(new GradeThreshold("E", 50.0, "Insuffisant"));
        thresholds.add(new GradeThreshold("F", 0.0, "Échec"));

        return GradeScale.builder()
                .name("Européenne")
                .description("Échelle de notation européenne (A, B, C, D, E, F)")
                .thresholds(thresholds)
                .build();
    }

    /**
     * Crée une échelle de notation avec système de points (10/10)
     */
    public static GradeScale createPointsScale() {
        List<GradeThreshold> thresholds = new ArrayList<>();
        thresholds.add(new GradeThreshold("10", 90.0, "Parfait"));
        thresholds.add(new GradeThreshold("9", 80.0, "Très bien"));
        thresholds.add(new GradeThreshold("8", 70.0, "Bien"));
        thresholds.add(new GradeThreshold("7", 60.0, "Moyen"));
        thresholds.add(new GradeThreshold("6", 50.0, "Passable"));
        thresholds.add(new GradeThreshold("0", 0.0, "Insuffisant"));

        return GradeScale.builder()
                .name("Points")
                .description("Échelle de notation sur 10 points")
                .thresholds(thresholds)
                .build();
    }

    /**
     * Retourne le libellé de la note en fonction du pourcentage
     */
    public String getGradeLabel(double percentage) {
        for (GradeThreshold threshold : thresholds) {
            if (percentage >= threshold.getMinPercentage()) {
                return threshold.getLetterGrade();
            }
        }
        return "N/A";
    }

    /**
     * Retourne la description de la note en fonction du pourcentage
     */
    public String getGradeDescription(double percentage) {
        for (GradeThreshold threshold : thresholds) {
            if (percentage >= threshold.getMinPercentage()) {
                return threshold.getDescription();
            }
        }
        return "Non noté";
    }

    /**
     * Vérifie si un pourcentage est valide pour cette échelle
     */
    public boolean isValidPercentage(double percentage) {
        return percentage >= 0 && percentage <= 100;
    }

    /**
     * Retourne le seuil minimum pour une note donnée
     */
    public Double getMinimumPercentageForGrade(String grade) {
        for (GradeThreshold threshold : thresholds) {
            if (threshold.getLetterGrade().equalsIgnoreCase(grade)) {
                return threshold.getMinPercentage();
            }
        }
        return null;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradeThreshold {
        private String letterGrade;
        private Double minPercentage;
        private String description;
    }
}