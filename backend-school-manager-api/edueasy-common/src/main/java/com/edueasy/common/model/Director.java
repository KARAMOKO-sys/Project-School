package com.edueasy.common.model;

import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "directors")
@JsonTypeName("DIRECTOR")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Director extends User {

    // ===== RELATION AVEC USER =====
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false)
    private String directorNumber;

    private String department;

    private String office;

    private LocalDate appointmentDate;

    private LocalDate endDate;

    private String responsibilities;

    @Override
    public UserRole getRole() {
        return UserRole.DIRECTOR;
    }
}