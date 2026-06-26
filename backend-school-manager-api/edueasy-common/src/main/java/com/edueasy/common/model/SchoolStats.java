package com.edueasy.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private String schoolId;

    private String schoolName;

    private LocalDateTime generatedAt;

    private Integer totalStudents;

    private Integer totalTeachers;

    private Integer totalStaff;

    private Integer totalClasses;

    private Integer totalDepartments;

    @Builder.Default
    private Map<String, Integer> studentsByLevel = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> studentsByClass = new HashMap<>();

    private Double attendanceRate;

    private Double graduationRate;

    private Double dropoutRate;

    private Double overallAverage;

    private Double bestAverage;

    private Double worstAverage;

    @Builder.Default
    private Map<String, Double> averageByLevel = new HashMap<>();

    @Builder.Default
    private Map<String, Double> averageBySubject = new HashMap<>();

    private Double totalBudget;

    private Double spentBudget;

    private Double remainingBudget;

    private Double totalFeesCollected;

    private Double pendingFees;

    private Integer activeTeachers;

    private Integer teachersOnLeave;

    private Integer newTeachersThisYear;

    private Integer totalRooms;

    private Integer occupiedRooms;

    private Integer availableRooms;

    // ===== Méthodes métier =====

    /**
     * Calcule le taux d'utilisation du budget
     */
    public Double getBudgetUtilizationRate() {
        if (totalBudget == null || totalBudget == 0.0) {
            return 0.0;
        }
        return (
            ((spentBudget != null ? spentBudget : 0.0) / totalBudget) * 100.0
        );
    }

    /**
     * Calcule le taux de recouvrement des frais
     */
    public Double getFeesCollectionRate() {
        Double total = getTotalFees();
        if (total == 0.0) {
            return 0.0;
        }
        Double collected =
            totalFeesCollected != null ? totalFeesCollected : 0.0;
        return (collected / total) * 100.0;
    }

    /**
     * Calcule le ratio élèves/enseignants
     */
    public Double getStudentTeacherRatio() {
        if (totalTeachers == null || totalTeachers == 0) {
            return 0.0;
        }
        return (
            (double) (totalStudents != null ? totalStudents : 0) / totalTeachers
        );
    }

    /**
     * Retourne le total des frais (collectés + en attente)
     */
    public Double getTotalFees() {
        Double collected =
            totalFeesCollected != null ? totalFeesCollected : 0.0;
        Double pending = pendingFees != null ? pendingFees : 0.0;
        return collected + pending;
    }

    /**
     * Vérifie si les statistiques sont valides
     */
    public boolean isValid() {
        return (
            schoolId != null &&
            !schoolId.isEmpty() &&
            generatedAt != null &&
            (totalStudents == null || totalStudents >= 0) &&
            (totalTeachers == null || totalTeachers >= 0) &&
            (totalStaff == null || totalStaff >= 0) &&
            (totalClasses == null || totalClasses >= 0) &&
            (attendanceRate == null ||
                (attendanceRate >= 0 && attendanceRate <= 100)) &&
            (graduationRate == null ||
                (graduationRate >= 0 && graduationRate <= 100)) &&
            (dropoutRate == null || (dropoutRate >= 0 && dropoutRate <= 100))
        );
    }

    /**
     * Calcule le nombre total d'enseignants
     */
    public Integer getTotalTeachers() {
        if (totalTeachers == null) {
            return 0;
        }
        return totalTeachers;
    }

    /**
     * Calcule le nombre total de salles
     */
    public Integer getTotalRooms() {
        if (totalRooms == null) {
            return 0;
        }
        return totalRooms;
    }

    /**
     * Calcule le nombre de salles disponibles
     */
    public Integer getAvailableRooms() {
        Integer total = getTotalRooms();
        Integer occupied = occupiedRooms != null ? occupiedRooms : 0;
        return Math.max(0, total - occupied);
    }

    /**
     * Calcule le taux d'occupation des salles
     */
    public Double getRoomOccupancyRate() {
        Integer total = getTotalRooms();
        if (total == 0) {
            return 0.0;
        }
        Integer occupied = occupiedRooms != null ? occupiedRooms : 0;
        return ((double) occupied / total) * 100.0;
    }

    /**
     * Retourne une instance vide pour une école
     */
    public static SchoolStats empty(String schoolId) {
        return builder()
            .schoolId(schoolId)
            .generatedAt(LocalDateTime.now())
            .totalStudents(0)
            .totalTeachers(0)
            .totalStaff(0)
            .totalClasses(0)
            .attendanceRate(0.0)
            .graduationRate(0.0)
            .dropoutRate(0.0)
            .overallAverage(0.0)
            .totalBudget(0.0)
            .spentBudget(0.0)
            .remainingBudget(0.0)
            .totalFeesCollected(0.0)
            .pendingFees(0.0)
            .build();
    }

    /**
     * Méthode utilitaire pour ajouter une statistique par niveau
     */
    public void addStudentsByLevel(String level, Integer count) {
        if (studentsByLevel == null) {
            studentsByLevel = new HashMap<>();
        }
        studentsByLevel.put(level, count);
    }

    /**
     * Méthode utilitaire pour ajouter une statistique par classe
     */
    public void addStudentsByClass(String className, Integer count) {
        if (studentsByClass == null) {
            studentsByClass = new HashMap<>();
        }
        studentsByClass.put(className, count);
    }

    /**
     * Méthode utilitaire pour ajouter une moyenne par niveau
     */
    public void addAverageByLevel(String level, Double average) {
        if (averageByLevel == null) {
            averageByLevel = new HashMap<>();
        }
        averageByLevel.put(level, average);
    }

    /**
     * Méthode utilitaire pour ajouter une moyenne par matière
     */
    public void addAverageBySubject(String subject, Double average) {
        if (averageBySubject == null) {
            averageBySubject = new HashMap<>();
        }
        averageBySubject.put(subject, average);
    }
}
