package com.edueasy.common.model;

import com.edueasy.common.enums.AdminPermission;
import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "admins")
@JsonTypeName("ADMINISTRATOR")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Admin extends User {

    @Column(unique = true, nullable = false)
    private String employeeNumber;

    private String department;

    private String position;

    private LocalDate hireDate;

    private Integer accessLevel;

    // ===== RELATION AVEC USER - AJOUTÉE =====
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "admin_permissions",
            joinColumns = @JoinColumn(name = "admin_id")
    )
    @Column(name = "permission")
    @Builder.Default
    private List<AdminPermission> permissions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "admin_managed_schools",
            joinColumns = @JoinColumn(name = "admin_id")
    )
    @Column(name = "school_id")
    @Builder.Default
    private List<String> managedSchoolIds = new ArrayList<>();

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        if (managedSchoolIds == null) {
            managedSchoolIds = new ArrayList<>();
        }
        if (accessLevel == null) {
            accessLevel = 1;
        }
        if (employeeNumber == null) {
            generateEmployeeNumber();
        }
    }

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.ADMINISTRATOR;
    }

    /**
     * Génère un numéro d'employé
     */
    public void generateEmployeeNumber() {
        if (employeeNumber == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            this.employeeNumber = "ADM-" + timestamp + "-" + random;
        }
    }

    /**
     * Vérifie si l'admin a une permission spécifique
     */
    public boolean hasPermission(AdminPermission permission) {
        return permissions != null && permissions.contains(permission);
    }

    /**
     * Vérifie si l'admin a l'une des permissions requises
     */
    public boolean hasAnyPermission(List<AdminPermission> requiredPermissions) {
        if (permissions == null || requiredPermissions == null) {
            return false;
        }
        return requiredPermissions.stream().anyMatch(permissions::contains);
    }

    /**
     * Vérifie si l'admin a toutes les permissions requises
     */
    public boolean hasAllPermissions(List<AdminPermission> requiredPermissions) {
        if (permissions == null || requiredPermissions == null) {
            return false;
        }
        return permissions.containsAll(requiredPermissions);
    }

    /**
     * Vérifie si l'admin est un super admin
     */
    public boolean isSuperAdmin() {
        return accessLevel != null && accessLevel >= 5;
    }

    /**
     * Vérifie si l'admin est un admin principal
     */
    public boolean isPrimaryAdmin() {
        return accessLevel != null && accessLevel >= 3;
    }

    /**
     * Ajoute une permission
     */
    public void addPermission(AdminPermission permission) {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    /**
     * Supprime une permission
     */
    public void removePermission(AdminPermission permission) {
        if (permissions != null) {
            permissions.remove(permission);
        }
    }

    /**
     * Ajoute une école à gérer
     */
    public void addManagedSchool(String schoolId) {
        if (managedSchoolIds == null) {
            managedSchoolIds = new ArrayList<>();
        }
        if (!managedSchoolIds.contains(schoolId)) {
            managedSchoolIds.add(schoolId);
        }
    }

    /**
     * Supprime une école de la liste des écoles gérées
     */
    public void removeManagedSchool(String schoolId) {
        if (managedSchoolIds != null) {
            managedSchoolIds.remove(schoolId);
        }
    }

    /**
     * Vérifie si l'admin gère une école spécifique
     */
    public boolean managesSchool(String schoolId) {
        return managedSchoolIds != null && managedSchoolIds.contains(schoolId);
    }

    /**
     * Retourne la liste des écoles gérées
     */
    public List<String> getManagedSchools() {
        return managedSchoolIds != null ? managedSchoolIds : new ArrayList<>();
    }

    /**
     * Retourne le nombre d'écoles gérées
     */
    public int getManagedSchoolsCount() {
        return managedSchoolIds != null ? managedSchoolIds.size() : 0;
    }

    /**
     * Vérifie si l'admin a des écoles à gérer
     */
    public boolean hasManagedSchools() {
        return managedSchoolIds != null && !managedSchoolIds.isEmpty();
    }

    /**
     * Calcule l'ancienneté en années
     */
    public long getYearsOfService() {
        if (hireDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.YEARS.between(hireDate, LocalDate.now());
    }

    /**
     * Vérifie si l'admin est récent (moins d'un an)
     */
    public boolean isNewAdmin() {
        return getYearsOfService() < 1;
    }

    /**
     * Vérifie si l'admin est expérimenté (plus de 5 ans)
     */
    public boolean isExperienced() {
        return getYearsOfService() >= 5;
    }

    /**
     * Retourne le niveau d'accès en texte
     */
    public String getAccessLevelLabel() {
        if (accessLevel == null) {
            return "Non défini";
        }
        return switch (accessLevel) {
            case 1 -> "Basique";
            case 2 -> "Standard";
            case 3 -> "Avancé";
            case 4 -> "Senior";
            case 5 -> "Super Admin";
            default -> "Niveau " + accessLevel;
        };
    }

    /**
     * Vérifie si l'admin a accès à toutes les écoles
     */
    public boolean hasAllSchoolsAccess() {
        return isSuperAdmin() || (accessLevel != null && accessLevel >= 4);
    }

    /**
     * Vérifie si l'admin peut gérer les utilisateurs
     */
    public boolean canManageUsers() {
        return hasPermission(AdminPermission.MANAGE_USERS) || isSuperAdmin();
    }

    /**
     * Vérifie si l'admin peut gérer les écoles
     */
    public boolean canManageSchools() {
        return hasPermission(AdminPermission.MANAGE_SCHOOLS) || isSuperAdmin();
    }

    /**
     * Vérifie si l'admin peut gérer les cours
     */
    public boolean canManageCourses() {
        return hasPermission(AdminPermission.MANAGE_COURSES) || isSuperAdmin();
    }

    /**
     * Vérifie si l'admin peut gérer les paiements
     */
    public boolean canManagePayments() {
        return hasPermission(AdminPermission.MANAGE_PAYMENTS) || isSuperAdmin();
    }

    /**
     * Vérifie si l'admin peut voir les rapports
     */
    public boolean canViewReports() {
        return hasPermission(AdminPermission.VIEW_REPORTS) || isSuperAdmin();
    }

    /**
     * Retourne le nom complet
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Vérifie si l'admin a un département
     */
    public boolean hasDepartment() {
        return department != null && !department.isEmpty();
    }

    /**
     * Vérifie si l'admin a une position
     */
    public boolean hasPosition() {
        return position != null && !position.isEmpty();
    }

    /**
     * Vérifie si l'admin a des permissions
     */
    public boolean hasPermissions() {
        return permissions != null && !permissions.isEmpty();
    }

    /**
     * Retourne le nombre total de permissions
     */
    public int getPermissionsCount() {
        return permissions != null ? permissions.size() : 0;
    }

    /**
     * Réinitialise les permissions
     */
    public void clearPermissions() {
        if (permissions != null) {
            permissions.clear();
        }
    }

    /**
     * Réinitialise les écoles gérées
     */
    public void clearManagedSchools() {
        if (managedSchoolIds != null) {
            managedSchoolIds.clear();
        }
    }
}