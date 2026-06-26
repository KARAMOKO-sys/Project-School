package com.edueasy.common.dto;

import com.edueasy.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateDTO {

    private UserStatus status;
    private String reason;

    public boolean isValid() {
        return status != null;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isInactive() {
        return status == UserStatus.INACTIVE;
    }

    public boolean isSuspended() {
        return status == UserStatus.SUSPENDED;
    }

    public boolean isBanned() {
        return status == UserStatus.BANNED;
    }

    public boolean isPending() {
        return status == UserStatus.PENDING;
    }

    public boolean isDeleted() {
        return status == UserStatus.DELETED;
    }

    public boolean isArchived() {
        return status == UserStatus.ARCHIVED;
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
}