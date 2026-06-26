package com.edueasy.common.dto;

import com.edueasy.common.enums.LevelTeacher;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.enums.UserStatus;
import com.edueasy.common.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponseDTO {

    private String id;  // Changé de Long à String
    private String uuid;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String teacherNumber;
    private LocalDate birthDate;
    private String phoneNumber;
    private Address address;
    private UserStatus status;
    private StatutUserSimple statutUserSimple;
    private LevelTeacher levelTeacher;
    private String profilePictureUrl;
    private String locale;
    private String timezone;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case ACTIVE -> "Actif";
            case INACTIVE -> "Inactif";
            case PENDING -> "En attente";
            case SUSPENDED -> "Suspendu";
            case BANNED -> "Banni";
            case DELETED -> "Supprimé";
            case ARCHIVED -> "Archivé";
        };
    }

    public String getLevelLabel() {
        if (levelTeacher == null) {
            return "Non défini";
        }
        return levelTeacher.getLabel();
    }

    public String getSimpleStatusLabel() {
        if (statutUserSimple == null) {
            return "Non défini";
        }
        return statutUserSimple.getLibelle();
    }
}