package com.edueasy.common.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.*;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String teacherNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String qualification;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    // Champs supplémentaires optionnels
    @Column(name = "specialization")
    private String specialization;

    @Column(name = "department")
    private String department;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_full_time")
    @Builder.Default
    private Boolean isFullTime = true;

    @Column(name = "office_hours")
    private String officeHours;

    @Column(name = "biography", length = 1000)
    private String biography;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // ===== Méthodes métier =====

    /**
     * Retourne le nom complet de l'enseignant
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Retourne le nom complet avec titre
     */
    public String getFullNameWithTitle() {
        String title = qualification != null ? qualification : "M.";
        return title + " " + firstName + " " + lastName;
    }

    /**
     * Vérifie si l'enseignant est actif
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Vérifie si l'enseignant est à temps plein
     */
    public boolean isFullTime() {
        return isFullTime != null && isFullTime;
    }

    /**
     * Calcule l'ancienneté en années
     */
    public long getYearsOfExperience() {
        if (hireDate == null) {
            return 0;
        }
        return ChronoUnit.YEARS.between(hireDate, LocalDate.now());
    }

    /**
     * Vérifie si l'enseignant est nouveau (moins d'un an)
     */
    public boolean isNewTeacher() {
        return getYearsOfExperience() < 1;
    }

    /**
     * Vérifie si l'enseignant est sénior (plus de 10 ans)
     */
    public boolean isSeniorTeacher() {
        return getYearsOfExperience() >= 10;
    }

    /**
     * Vérifie si l'enseignant a un email valide
     */
    public boolean hasValidEmail() {
        return email != null && !email.isEmpty() && email.contains("@");
    }

    /**
     * Vérifie si l'enseignant a un numéro de téléphone
     */
    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isEmpty();
    }

    /**
     * Vérifie si l'enseignant a une qualification
     */
    public boolean hasQualification() {
        return qualification != null && !qualification.isEmpty();
    }

    /**
     * Active l'enseignant
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Désactive l'enseignant
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Met à jour les informations de contact
     */
    public void updateContactInfo(String email, String phoneNumber) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            this.phoneNumber = phoneNumber;
        }
    }
}
