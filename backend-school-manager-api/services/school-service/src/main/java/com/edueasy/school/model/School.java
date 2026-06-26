package com.edueasy.school.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "schools",
        indexes = {
                @Index(name = "idx_school_code", columnList = "code"),
                @Index(name = "idx_school_name", columnList = "name"),
                @Index(name = "idx_school_type", columnList = "type"),
                @Index(name = "idx_school_city", columnList = "city"),
                @Index(name = "idx_school_country", columnList = "country"),
                @Index(name = "idx_school_active", columnList = "active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String country;

    private String region;

    private String city;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String phone;

    private String email;

    private String website;

    @Column(name = "logo_url")
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    private SchoolType type;

    @Column(name = "director_id")
    private String directorId;

    @Column(name = "total_students")
    @Builder.Default
    private Integer totalStudents = 0;

    @Column(name = "total_teachers")
    @Builder.Default
    private Integer totalTeachers = 0;

    @Column(name = "total_classes")
    @Builder.Default
    private Integer totalClasses = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> configuration = new HashMap<>();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "founding_year")
    private Integer foundingYear;

    @Column(name = "motto", length = 200)
    private String motto;

    @Column(name = "principal_id")
    private String principalId;

    @Column(name = "vice_principal_id")
    private String vicePrincipalId;

    @Column(name = "accreditation")
    private String accreditation;

    @Column(name = "school_level")
    private String schoolLevel;

    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "UTC";

    @Column(name = "currency")
    @Builder.Default
    private String currency = "XOF";

    @Column(name = "language")
    @Builder.Default
    private String language = "fr";

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (active == null) {
            active = true;
        }
        if (totalStudents == null) {
            totalStudents = 0;
        }
        if (totalTeachers == null) {
            totalTeachers = 0;
        }
        if (totalClasses == null) {
            totalClasses = 0;
        }
        if (configuration == null) {
            configuration = new HashMap<>();
        }
        if (timezone == null) {
            timezone = "UTC";
        }
        if (currency == null) {
            currency = "XOF";
        }
        if (language == null) {
            language = "fr";
        }
        generateCode();
    }

    // ===== Méthodes métier =====

    /**
     * Génère le code de l'école
     */
    public void generateCode() {
        if (code == null && name != null) {
            String namePart = name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            if (namePart.length() > 5) {
                namePart = namePart.substring(0, 5);
            }
            this.code = "SCH-" + namePart + "-" + System.currentTimeMillis() % 10000;
        }
    }

    /**
     * Vérifie si l'école est active
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Vérifie si l'école a un directeur
     */
    public boolean hasDirector() {
        return directorId != null && !directorId.isEmpty();
    }

    /**
     * Vérifie si l'école a un principal
     */
    public boolean hasPrincipal() {
        return principalId != null && !principalId.isEmpty();
    }

    /**
     * Vérifie si l'école a un vice-principal
     */
    public boolean hasVicePrincipal() {
        return vicePrincipalId != null && !vicePrincipalId.isEmpty();
    }

    /**
     * Vérifie si l'école a un logo
     */
    public boolean hasLogo() {
        return logoUrl != null && !logoUrl.isEmpty();
    }

    /**
     * Vérifie si l'école a un site web
     */
    public boolean hasWebsite() {
        return website != null && !website.isEmpty();
    }

    /**
     * Vérifie si l'école a des coordonnées GPS
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    /**
     * Active l'école
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Désactive l'école
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Incrémente le nombre total d'étudiants
     */
    public void incrementStudents() {
        if (totalStudents == null) {
            totalStudents = 0;
        }
        totalStudents++;
    }

    /**
     * Décrémente le nombre total d'étudiants
     */
    public void decrementStudents() {
        if (totalStudents != null && totalStudents > 0) {
            totalStudents--;
        }
    }

    /**
     * Incrémente le nombre total d'enseignants
     */
    public void incrementTeachers() {
        if (totalTeachers == null) {
            totalTeachers = 0;
        }
        totalTeachers++;
    }

    /**
     * Décrémente le nombre total d'enseignants
     */
    public void decrementTeachers() {
        if (totalTeachers != null && totalTeachers > 0) {
            totalTeachers--;
        }
    }

    /**
     * Incrémente le nombre total de classes
     */
    public void incrementClasses() {
        if (totalClasses == null) {
            totalClasses = 0;
        }
        totalClasses++;
    }

    /**
     * Décrémente le nombre total de classes
     */
    public void decrementClasses() {
        if (totalClasses != null && totalClasses > 0) {
            totalClasses--;
        }
    }

    /**
     * Met à jour le nom et le code
     */
    public void updateName(String newName) {
        if (newName != null && !newName.trim().isEmpty()) {
            this.name = newName.trim();
            generateCode();
        }
    }

    /**
     * Retourne le nom complet de l'école - VERSION CORRIGÉE
     */
    public String getFullName() {
        if (type != null) {
            return type.getLabel() + " " + name;
        }
        return name;
    }

    /**
     * Retourne le statut de l'école
     */
    public String getStatusLabel() {
        return isActive() ? "Active" : "Inactive";
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        return isActive() ? "green" : "gray";
    }

    /**
     * Vérifie si l'école est valide
     */
    public boolean isValid() {
        return code != null && !code.isEmpty()
                && name != null && !name.isEmpty()
                && email != null && !email.isEmpty()
                && type != null;
    }

    /**
     * Retourne le type d'école en français - VERSION CORRIGÉE
     */
    public String getTypeLabel() {
        if (type == null) {
            return "Non défini";
        }
        return switch (type) {
            case PRIMARY -> "École primaire";
            case MIDDLE -> "Collège";
            case HIGH -> "Lycée";
            case UNIVERSITY -> "Université";
            case INSTITUTE -> "Institut";
            case LANGUAGE_SCHOOL -> "École de langues";
            case VOCATIONAL -> "École professionnelle";
            case ONLINE -> "École en ligne";
            case SPECIALIZED -> "École spécialisée";
            case OTHER -> "Autre";
        };
    }

    /**
     * Retourne l'icône associée au type d'école
     */
    public String getTypeIcon() {
        if (type == null) {
            return "school";
        }
        return switch (type) {
            case PRIMARY -> "child";
            case MIDDLE -> "school";
            case HIGH -> "graduation-cap";
            case UNIVERSITY -> "university";
            case INSTITUTE -> "building-columns";
            case LANGUAGE_SCHOOL -> "language";
            case VOCATIONAL -> "tools";
            case ONLINE -> "laptop";
            case SPECIALIZED -> "microscope";
            case OTHER -> "building";
        };
    }

    /**
     * Retourne l'adresse complète formatée
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) {
            sb.append(address);
        }
        if (city != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (region != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(region);
        }
        if (country != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }

    /**
     * Vérifie si l'école a une configuration
     */
    public boolean hasConfiguration() {
        return configuration != null && !configuration.isEmpty();
    }

    /**
     * Ajoute une configuration
     */
    public void addConfiguration(String key, Object value) {
        if (configuration == null) {
            configuration = new HashMap<>();
        }
        configuration.put(key, value);
    }

    /**
     * Récupère une configuration
     */
    public Object getConfigurationValue(String key) {
        if (configuration == null) {
            return null;
        }
        return configuration.get(key);
    }

    /**
     * Supprime une configuration
     */
    public void removeConfiguration(String key) {
        if (configuration != null) {
            configuration.remove(key);
        }
    }

    /**
     * Met à jour les statistiques de l'école
     */
    public void updateStats(int students, int teachers, int classes) {
        this.totalStudents = students >= 0 ? students : 0;
        this.totalTeachers = teachers >= 0 ? teachers : 0;
        this.totalClasses = classes >= 0 ? classes : 0;
    }

    /**
     * Vérifie si l'école est une école primaire
     */
    public boolean isPrimarySchool() {
        return type == SchoolType.PRIMARY;
    }

    /**
     * Vérifie si l'école est un collège
     */
    public boolean isMiddleSchool() {
        return type == SchoolType.MIDDLE;
    }

    /**
     * Vérifie si l'école est un lycée
     */
    public boolean isHighSchool() {
        return type == SchoolType.HIGH;
    }

    /**
     * Vérifie si l'école est une université
     */
    public boolean isUniversity() {
        return type == SchoolType.UNIVERSITY;
    }

    /**
     * Vérifie si l'école est un institut
     */
    public boolean isInstitute() {
        return type == SchoolType.INSTITUTE;
    }

    /**
     * Vérifie si l'école est une école de langues
     */
    public boolean isLanguageSchool() {
        return type == SchoolType.LANGUAGE_SCHOOL;
    }

    /**
     * Vérifie si l'école est une école professionnelle
     */
    public boolean isVocational() {
        return type == SchoolType.VOCATIONAL;
    }

    /**
     * Vérifie si l'école est en ligne
     */
    public boolean isOnline() {
        return type == SchoolType.ONLINE;
    }

    /**
     * Vérifie si l'école est spécialisée
     */
    public boolean isSpecialized() {
        return type == SchoolType.SPECIALIZED;
    }
}