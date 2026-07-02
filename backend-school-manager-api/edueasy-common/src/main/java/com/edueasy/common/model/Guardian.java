package com.edueasy.common.model;

import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guardians")
@JsonTypeName("GUARDIAN")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Guardian extends User {

    // 🔥 Solution 1: Supprimer la relation directe avec User
    // et utiliser un champ simple pour stocker l'UUID de l'utilisateur associé
    @Column(name = "associated_user_uuid", length = 36)
    private String associatedUserUuid;

    // 🔥 Solution alternative: Si vous voulez garder une relation,
    // utilisez une classe concrète comme Student ou TeacherSimple
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "student_id")
    // private Student student;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "relationship")
    private String relationship;

    @ElementCollection
    @CollectionTable(
            name = "guardian_wards",
            joinColumns = @JoinColumn(name = "guardian_id")
    )
    @Column(name = "ward_id")
    @Builder.Default
    private List<String> wardIds = new ArrayList<>();

    @Column(name = "is_primary_contact")
    @Builder.Default
    private Boolean isPrimaryContact = false;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (associatedUserUuid == null && getUuid() != null) {
            // Par défaut, l'utilisateur associé est le guardian lui-même
            this.associatedUserUuid = getUuid();
        }
    }

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.GUARDIAN;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public boolean hasAssociatedUser() {
        return associatedUserUuid != null && !associatedUserUuid.isEmpty();
    }

    public void associateWithUser(String userUuid) {
        this.associatedUserUuid = userUuid;
    }

    public void associateWithSelf() {
        this.associatedUserUuid = getUuid();
    }

    public void addWard(String wardId) {
        if (wardIds == null) {
            wardIds = new ArrayList<>();
        }
        if (!wardIds.contains(wardId)) {
            wardIds.add(wardId);
        }
    }

    public void removeWard(String wardId) {
        if (wardIds != null) {
            wardIds.remove(wardId);
        }
    }

    public boolean isWard(String wardId) {
        return wardIds != null && wardIds.contains(wardId);
    }

    public int getWardCount() {
        return wardIds != null ? wardIds.size() : 0;
    }

    public boolean hasWards() {
        return wardIds != null && !wardIds.isEmpty();
    }

    public void setPrimaryContact(boolean primary) {
        this.isPrimaryContact = primary;
    }

    public boolean isPrimaryContact() {
        return isPrimaryContact != null && isPrimaryContact;
    }

    public String getRelationshipLabel() {
        if (relationship == null || relationship.isEmpty()) {
            return "Non spécifié";
        }
        return switch (relationship.toLowerCase()) {
            case "father" -> "Père";
            case "mother" -> "Mère";
            case "brother" -> "Frère";
            case "sister" -> "Sœur";
            case "uncle" -> "Oncle";
            case "aunt" -> "Tante";
            case "grandfather" -> "Grand-père";
            case "grandmother" -> "Grand-mère";
            case "cousin" -> "Cousin/Cousine";
            case "guardian" -> "Tuteur légal";
            case "other" -> "Autre";
            default -> relationship;
        };
    }

    public String getOccupationLabel() {
        if (occupation == null || occupation.isEmpty()) {
            return "Profession non spécifiée";
        }
        return occupation;
    }

    public boolean isEmployed() {
        return occupation != null && !occupation.isEmpty();
    }

    public String getContactInfo() {
        StringBuilder info = new StringBuilder();
        info.append(getFullName());
        if (relationship != null && !relationship.isEmpty()) {
            info.append(" (").append(getRelationshipLabel()).append(")");
        }
        if (isPrimaryContact()) {
            info.append(" - Contact principal");
        }
        return info.toString();
    }

    public boolean canBeAssociated() {
        return associatedUserUuid != null && !associatedUserUuid.isEmpty()
                && !associatedUserUuid.equals(getUuid());
    }

    public void disassociateFromUser() {
        this.associatedUserUuid = null;
    }

    public boolean hasValidAssociation() {
        return associatedUserUuid != null && !associatedUserUuid.isEmpty();
    }

    // Si vous voulez garder une relation avec Student (recommandé)
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    public void associateWithStudent(Student student) {
        this.student = student;
    }

    public boolean hasStudentAssociation() {
        return student != null;
    }
    */
}