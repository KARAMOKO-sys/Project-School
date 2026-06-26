package com.edueasy.school.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "departments",
        indexes = {
                @Index(name = "idx_department_school", columnList = "school_id"),
                @Index(name = "idx_department_name", columnList = "name"),
                @Index(name = "idx_department_head", columnList = "head_teacher_id"),
                @Index(name = "idx_department_active", columnList = "active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "school_id", nullable = false)
    private String schoolId;

    @Column(nullable = false)
    private String name;

    @Column(name = "head_teacher_id")
    private String headTeacherId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "department_head_name")
    private String departmentHeadName;

    @Column(name = "department_head_email")
    private String departmentHeadEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "office_location")
    private String officeLocation;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "budget")
    @Builder.Default
    private Double budget = 0.0;

    @Column(name = "color")
    private String color;

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
        if (budget == null) {
            budget = 0.0;
        }
        generateCode();
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si le département est actif
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Génère le code du département
     */
    public void generateCode() {
        if (code == null && name != null && schoolId != null) {
            String prefix = schoolId.length() > 3 ? schoolId.substring(0, 3).toUpperCase() : schoolId.toUpperCase();
            String namePart = name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            if (namePart.length() > 4) {
                namePart = namePart.substring(0, 4);
            }
            this.code = prefix + "-DEP-" + namePart;
        }
    }

    /**
     * Vérifie si le département a un chef
     */
    public boolean hasHeadTeacher() {
        return headTeacherId != null && !headTeacherId.isEmpty();
    }

    /**
     * Vérifie si le département a une description
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Vérifie si le département a un code
     */
    public boolean hasCode() {
        return code != null && !code.isEmpty();
    }

    /**
     * Vérifie si le département a un budget
     */
    public boolean hasBudget() {
        return budget != null && budget > 0;
    }

    /**
     * Active le département
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Désactive le département
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Définit le chef de département
     */
    public void setDepartmentHead(String teacherId, String headName, String headEmail) {
        this.headTeacherId = teacherId;
        this.departmentHeadName = headName;
        this.departmentHeadEmail = headEmail;
    }

    /**
     * Retourne le nom complet du département
     */
    public String getFullName() {
        if (faculty != null && !faculty.isEmpty()) {
            return faculty + " - " + name;
        }
        return name;
    }

    /**
     * Retourne le statut du département
     */
    public String getStatusLabel() {
        return isActive() ? "Actif" : "Inactif";
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        return isActive() ? "green" : "gray";
    }

    /**
     * Vérifie si le département est valide
     */
    public boolean isValid() {
        return schoolId != null && !schoolId.isEmpty()
                && name != null && !name.isEmpty();
    }

    /**
     * Met à jour le budget
     */
    public void updateBudget(double newBudget) {
        if (newBudget >= 0) {
            this.budget = newBudget;
        }
    }

    /**
     * Ajoute au budget
     */
    public void addBudget(double amount) {
        if (amount > 0 && budget != null) {
            this.budget += amount;
        }
    }

    /**
     * Retire du budget
     */
    public void deductBudget(double amount) {
        if (amount > 0 && budget != null) {
            this.budget = Math.max(0, this.budget - amount);
        }
    }

    /**
     * Vérifie si le département a des fonds suffisants
     */
    public boolean hasSufficientBudget(double amount) {
        return budget != null && budget >= amount;
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
}