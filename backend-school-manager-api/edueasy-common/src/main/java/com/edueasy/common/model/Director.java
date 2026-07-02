// Director.java - TOUT EN UNE SEULE TABLE
package com.edueasy.common.model;

import com.edueasy.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "directors")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Director extends User {

    // 🔥 TOUS LES CHAMPS SONT DANS LA TABLE directors
    @Column(unique = true, nullable = false, name = "director_number")
    private String directorNumber;

    @Column(name = "department")
    private String department;

    @Column(name = "office")
    private String office;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (directorNumber == null) {
            generateDirectorNumber();
        }
    }

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.DIRECTOR;
    }

    public void generateDirectorNumber() {
        if (directorNumber == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            this.directorNumber = "DIR-" + timestamp + "-" + random;
        }
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public boolean hasDirectorNumber() {
        return directorNumber != null && !directorNumber.isEmpty();
    }

    public boolean isActive() {
        return appointmentDate != null
                && (endDate == null || LocalDate.now().isBefore(endDate));
    }

    public boolean isAppointed() {
        return appointmentDate != null && LocalDate.now().isAfter(appointmentDate);
    }

    public boolean isEnded() {
        return endDate != null && LocalDate.now().isAfter(endDate);
    }
}