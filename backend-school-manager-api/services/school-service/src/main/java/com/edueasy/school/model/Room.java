package com.edueasy.school.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "rooms",
        indexes = {
                @Index(name = "idx_room_school", columnList = "school_id"),
                @Index(name = "idx_room_name", columnList = "name"),
                @Index(name = "idx_room_type", columnList = "type"),
                @Index(name = "idx_room_building", columnList = "building"),
                @Index(name = "idx_room_active", columnList = "active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "school_id", nullable = false)
    private String schoolId;

    @Column(nullable = false)
    private String name;

    private String building;

    private Integer floor;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column(name = "has_projector")
    @Builder.Default
    private Boolean hasProjector = false;

    @Column(name = "has_smart_board")
    @Builder.Default
    private Boolean hasSmartBoard = false;

    @Column(name = "has_computers")
    @Builder.Default
    private Boolean hasComputers = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "room_code", unique = true)
    private String roomCode;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "has_air_conditioning")
    @Builder.Default
    private Boolean hasAirConditioning = false;

    @Column(name = "has_wifi")
    @Builder.Default
    private Boolean hasWifi = false;

    @Column(name = "has_audio_system")
    @Builder.Default
    private Boolean hasAudioSystem = false;

    @Column(name = "maintenance_required")
    @Builder.Default
    private Boolean maintenanceRequired = false;

    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;

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
        if (isAvailable == null) {
            isAvailable = true;
        }
        if (hasProjector == null) {
            hasProjector = false;
        }
        if (hasSmartBoard == null) {
            hasSmartBoard = false;
        }
        if (hasComputers == null) {
            hasComputers = false;
        }
        if (hasAirConditioning == null) {
            hasAirConditioning = false;
        }
        if (hasWifi == null) {
            hasWifi = false;
        }
        if (hasAudioSystem == null) {
            hasAudioSystem = false;
        }
        if (maintenanceRequired == null) {
            maintenanceRequired = false;
        }
        generateRoomCode();
    }

    // ===== Méthodes métier =====

    /**
     * Génère le code de la salle
     */
    public void generateRoomCode() {
        if (roomCode == null && name != null && schoolId != null) {
            String prefix = schoolId.length() > 3 ? schoolId.substring(0, 3).toUpperCase() : schoolId.toUpperCase();
            String namePart = name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            String buildingPart = building != null ? building.replaceAll("[^a-zA-Z0-9]", "").toUpperCase() : "BLD";
            if (buildingPart.length() > 3) {
                buildingPart = buildingPart.substring(0, 3);
            }
            // Limiter la longueur du nom
            if (namePart.length() > 5) {
                namePart = namePart.substring(0, 5);
            }
            this.roomCode = prefix + "-" + buildingPart + "-" + namePart;
        }
    }

    /**
     * Vérifie si la salle est active
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Vérifie si la salle est disponible
     */
    public boolean isAvailable() {
        return isAvailable != null && isAvailable && isActive();
    }

    /**
     * Vérifie si la salle est en maintenance
     */
    public boolean isUnderMaintenance() {
        return maintenanceRequired != null && maintenanceRequired;
    }

    /**
     * Vérifie si la salle a un projecteur
     */
    public boolean hasProjector() {
        return hasProjector != null && hasProjector;
    }

    /**
     * Vérifie si la salle a un tableau interactif
     */
    public boolean hasSmartBoard() {
        return hasSmartBoard != null && hasSmartBoard;
    }

    /**
     * Vérifie si la salle a des ordinateurs
     */
    public boolean hasComputers() {
        return hasComputers != null && hasComputers;
    }

    /**
     * Vérifie si la salle a la climatisation
     */
    public boolean hasAirConditioning() {
        return hasAirConditioning != null && hasAirConditioning;
    }

    /**
     * Vérifie si la salle a le Wi-Fi
     */
    public boolean hasWifi() {
        return hasWifi != null && hasWifi;
    }

    /**
     * Vérifie si la salle a un système audio
     */
    public boolean hasAudioSystem() {
        return hasAudioSystem != null && hasAudioSystem;
    }

    /**
     * Vérifie si la salle est équipée
     */
    public boolean isEquipped() {
        return hasProjector() || hasSmartBoard() || hasComputers() || hasWifi();
    }

    /**
     * Vérifie si la salle est bien équipée
     */
    public boolean isWellEquipped() {
        int equipmentCount = getEquipmentCount();
        return equipmentCount >= 3;
    }

    /**
     * Active la salle
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Désactive la salle
     */
    public void deactivate() {
        this.active = false;
        this.isAvailable = false;
    }

    /**
     * Marque la salle comme disponible
     */
    public void markAsAvailable() {
        this.isAvailable = true;
        this.maintenanceRequired = false;
    }

    /**
     * Marque la salle comme indisponible
     */
    public void markAsUnavailable(String reason) {
        this.isAvailable = false;
        this.maintenanceRequired = true;
        this.maintenanceNotes = reason;
    }

    /**
     * Met à jour la capacité
     */
    public void updateCapacity(int newCapacity) {
        if (newCapacity > 0) {
            this.capacity = newCapacity;
        }
    }

    /**
     * Vérifie si la salle peut accueillir un certain nombre de personnes
     */
    public boolean canAccommodate(int numberOfPeople) {
        return capacity != null && numberOfPeople <= capacity;
    }

    /**
     * Retourne le nom complet de la salle
     */
    public String getFullName() {
        if (building != null && floor != null) {
            return building + " - " + getFloorLabel() + " - " + name;
        }
        if (building != null) {
            return building + " - " + name;
        }
        return name;
    }

    /**
     * Retourne le libellé de l'étage
     */
    public String getFloorLabel() {
        if (floor == null) {
            return "RDC";
        }
        if (floor == 0) {
            return "RDC";
        }
        if (floor == 1) {
            return "1er étage";
        }
        return floor + "ème étage";
    }

    /**
     * Retourne le statut de la salle
     */
    public String getStatusLabel() {
        if (!isActive()) {
            return "Inactive";
        }
        if (isUnderMaintenance()) {
            return "En maintenance";
        }
        if (!isAvailable()) {
            return "Indisponible";
        }
        return "Disponible";
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (!isActive()) {
            return "gray";
        }
        if (isUnderMaintenance()) {
            return "red";
        }
        if (!isAvailable()) {
            return "orange";
        }
        return "green";
    }

    /**
     * Vérifie si la salle est valide
     */
    public boolean isValid() {
        return schoolId != null && !schoolId.isEmpty()
                && name != null && !name.isEmpty()
                && type != null
                && capacity != null && capacity > 0;
    }

    /**
     * Retourne le type de salle en français - VERSION CORRIGÉE
     */
    public String getTypeLabel() {
        if (type == null) {
            return "Non défini";
        }
        return switch (type) {
            case CLASSROOM -> "Salle de classe";
            case LABORATORY -> "Laboratoire";
            case LIBRARY -> "Bibliothèque";
            case GYMNASIUM -> "Gymnase";
            case AUDITORIUM -> "Amphithéâtre";
            case COMPUTER_ROOM -> "Salle informatique";
            case TEACHERS_ROOM -> "Salle des professeurs";
            case ADMIN_OFFICE -> "Bureau administratif";
            case MEETING_ROOM -> "Salle de réunion";
            case STUDY_ROOM -> "Salle d'étude";
            case COMPUTER_LAB -> "Laboratoire informatique";
            case SPORTS_HALL -> "Salle de sport";
            case OTHER -> "Autre";
        };
    }

    /**
     * Retourne l'icône associée au type de salle - VERSION CORRIGÉE
     */
    public String getTypeIcon() {
        if (type == null) {
            return "building";
        }
        return switch (type) {
            case CLASSROOM -> "chalkboard-user";
            case LABORATORY -> "microscope";
            case LIBRARY -> "book-open";
            case GYMNASIUM -> "dumbbell";
            case AUDITORIUM -> "users";
            case COMPUTER_ROOM -> "computer";
            case TEACHERS_ROOM -> "user-tie";
            case ADMIN_OFFICE -> "building-columns";
            case MEETING_ROOM -> "people-group";
            case STUDY_ROOM -> "book-open-reader";
            case COMPUTER_LAB -> "laptop-code";
            case SPORTS_HALL -> "basketball";
            case OTHER -> "building";
        };
    }

    /**
     * Met à jour le nom et le code
     */
    public void updateName(String newName) {
        if (newName != null && !newName.trim().isEmpty()) {
            this.name = newName.trim();
            generateRoomCode();
        }
    }

    /**
     * Ajoute un équipement à la salle
     */
    public void addEquipment(EquipmentType equipmentType) {
        if (equipmentType == null) {
            return;
        }
        switch (equipmentType) {
            case PROJECTOR -> this.hasProjector = true;
            case SMART_BOARD -> this.hasSmartBoard = true;
            case COMPUTERS -> this.hasComputers = true;
            case AIR_CONDITIONING -> this.hasAirConditioning = true;
            case WIFI -> this.hasWifi = true;
            case AUDIO_SYSTEM -> this.hasAudioSystem = true;
        }
    }

    /**
     * Supprime un équipement de la salle
     */
    public void removeEquipment(EquipmentType equipmentType) {
        if (equipmentType == null) {
            return;
        }
        switch (equipmentType) {
            case PROJECTOR -> this.hasProjector = false;
            case SMART_BOARD -> this.hasSmartBoard = false;
            case COMPUTERS -> this.hasComputers = false;
            case AIR_CONDITIONING -> this.hasAirConditioning = false;
            case WIFI -> this.hasWifi = false;
            case AUDIO_SYSTEM -> this.hasAudioSystem = false;
        }
    }

    /**
     * Retourne le nombre d'équipements
     */
    public int getEquipmentCount() {
        int count = 0;
        if (hasProjector()) count++;
        if (hasSmartBoard()) count++;
        if (hasComputers()) count++;
        if (hasWifi()) count++;
        if (hasAirConditioning()) count++;
        if (hasAudioSystem()) count++;
        return count;
    }

    /**
     * Retourne la liste des équipements sous forme de texte
     */
    public String getEquipmentList() {
        StringBuilder sb = new StringBuilder();
        if (hasProjector()) sb.append("Projecteur, ");
        if (hasSmartBoard()) sb.append("Tableau interactif, ");
        if (hasComputers()) sb.append("Ordinateurs, ");
        if (hasWifi()) sb.append("Wi-Fi, ");
        if (hasAirConditioning()) sb.append("Climatisation, ");
        if (hasAudioSystem()) sb.append("Système audio, ");
        String result = sb.toString();
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2);
        }
        return result.isEmpty() ? "Aucun équipement" : result;
    }

    /**
     * Retourne le nombre de places disponibles
     */
    public int getAvailableSeats() {
        if (capacity == null) {
            return 0;
        }
        return capacity;
    }

    /**
     * Vérifie si la salle est un laboratoire
     */
    public boolean isLaboratory() {
        return type == RoomType.LABORATORY || type == RoomType.COMPUTER_LAB;
    }

    /**
     * Vérifie si la salle est une salle de classe
     */
    public boolean isClassroom() {
        return type == RoomType.CLASSROOM || type == RoomType.COMPUTER_ROOM;
    }

    /**
     * Vérifie si la salle est une salle de réunion
     */
    public boolean isMeetingRoom() {
        return type == RoomType.MEETING_ROOM || type == RoomType.TEACHERS_ROOM;
    }

    /**
     * Vérifie si la salle est sportive
     */
    public boolean isSportsFacility() {
        return type == RoomType.GYMNASIUM || type == RoomType.SPORTS_HALL;
    }

    /**
     * Marque la salle comme nécessitant une maintenance
     */
    public void requestMaintenance(String notes) {
        this.maintenanceRequired = true;
        this.maintenanceNotes = notes;
        this.isAvailable = false;
    }

    /**
     * Termine la maintenance
     */
    public void completeMaintenance() {
        this.maintenanceRequired = false;
        this.maintenanceNotes = null;
        this.isAvailable = true;
    }

    /**
     * Enum des types d'équipement
     */
    public enum EquipmentType {
        PROJECTOR,
        SMART_BOARD,
        COMPUTERS,
        AIR_CONDITIONING,
        WIFI,
        AUDIO_SYSTEM
    }
}