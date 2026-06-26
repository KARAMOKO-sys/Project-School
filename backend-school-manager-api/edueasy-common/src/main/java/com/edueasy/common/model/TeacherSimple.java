package com.edueasy.common.model;

import com.edueasy.common.enums.LevelTeacher;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "teachers_simple")
@JsonTypeName("TEACHER_SIMPLE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TeacherSimple extends User {

    @Enumerated(EnumType.STRING)
    private StatutUserSimple statutUserSimple;

    @Enumerated(EnumType.STRING)
    private LevelTeacher levelTeacher;

    @Column(unique = true, nullable = false)
    private String teacherNumber;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (statutUserSimple == null) {
            statutUserSimple = StatutUserSimple.EN_ATTENTE;
        }
        if (levelTeacher == null) {
            levelTeacher = LevelTeacher.DEBUTANT;
        }
        if (teacherNumber == null) {
            generateTeacherNumber();
        }
    }

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.TEACHER_SIMPLE;
    }

    /**
     * Génère un numéro d'enseignant
     */
    public void generateTeacherNumber() {
        if (teacherNumber == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            this.teacherNumber = "TCH-" + timestamp + "-" + random;
        }
    }

    /**
     * Vérifie si l'enseignant est actif
     */
    public boolean isActive() {
        return statutUserSimple == StatutUserSimple.ACTIF;
    }

    /**
     * Vérifie si l'enseignant est en attente
     */
    public boolean isPending() {
        return statutUserSimple == StatutUserSimple.EN_ATTENTE;
    }

    /**
     * Vérifie si l'enseignant est suspendu
     */
    public boolean isSuspended() {
        return statutUserSimple == StatutUserSimple.SUSPENDU;
    }

    /**
     * Vérifie si l'enseignant est supprimé
     */
    public boolean isDeleted() {
        return statutUserSimple == StatutUserSimple.SUPPRIME;
    }

    /**
     * Vérifie si l'enseignant est en brouillon
     */
    public boolean isDraft() {
        return statutUserSimple == StatutUserSimple.BROUILLON;
    }

    /**
     * Vérifie si l'enseignant est inactif
     */
    public boolean isInactive() {
        return statutUserSimple == StatutUserSimple.INACTIF;
    }

    /**
     * Active l'enseignant
     */
    public void activate() {
        this.statutUserSimple = StatutUserSimple.ACTIF;
    }

    /**
     * Suspend l'enseignant
     */
    public void suspend() {
        this.statutUserSimple = StatutUserSimple.SUSPENDU;
    }

    /**
     * Met l'enseignant en attente
     */
    public void setPending() {
        this.statutUserSimple = StatutUserSimple.EN_ATTENTE;
    }

    /**
     * Supprime l'enseignant
     */
    public void delete() {
        this.statutUserSimple = StatutUserSimple.SUPPRIME;
    }

    /**
     * Met l'enseignant en brouillon
     */
    public void setDraft() {
        this.statutUserSimple = StatutUserSimple.BROUILLON;
    }

    /**
     * Met l'enseignant en inactif
     */
    public void setInactive() {
        this.statutUserSimple = StatutUserSimple.INACTIF;
    }

    /**
     * Vérifie si l'enseignant a un niveau spécifique
     */
    public boolean hasLevel(LevelTeacher level) {
        return this.levelTeacher == level;
    }

    /**
     * Vérifie si l'enseignant est de niveau débutant
     */
    public boolean isBeginner() {
        return levelTeacher == LevelTeacher.DEBUTANT;
    }

    /**
     * Vérifie si l'enseignant est de niveau intermédiaire
     */
    public boolean isIntermediate() {
        return levelTeacher == LevelTeacher.INTERMEDIAIRE;
    }

    /**
     * Vérifie si l'enseignant est de niveau confirmé
     */
    public boolean isConfirmed() {
        return levelTeacher == LevelTeacher.CONFIRME;
    }

    /**
     * Vérifie si l'enseignant est de niveau expérimenté
     */
    public boolean isExperienced() {
        return levelTeacher == LevelTeacher.EXPERIMENTE;
    }

    /**
     * Vérifie si l'enseignant est de niveau sénior
     */
    public boolean isSenior() {
        return levelTeacher == LevelTeacher.SENIOR;
    }

    /**
     * Vérifie si l'enseignant est de niveau expert
     */
    public boolean isExpert() {
        return levelTeacher == LevelTeacher.EXPERT;
    }

    /**
     * Retourne la description du niveau
     */
    public String getLevelDescription() {
        if (levelTeacher == null) {
            return "Niveau non défini";
        }
        return switch (levelTeacher) {
            case DEBUTANT -> "Débutant - En formation";
            case INTERMEDIAIRE -> "Intermédiaire - Autonome";
            case CONFIRME -> "Confirmé - Expérimenté";
            case EXPERIMENTE -> "Expérimenté - Très compétent";
            case SENIOR -> "Sénior - Très expérimenté";
            case EXPERT -> "Expert - Référence";
        };
    }

    /**
     * Retourne l'icône du niveau
     */
    public String getLevelIcon() {
        if (levelTeacher == null) {
            return "fa-question-circle";
        }
        return switch (levelTeacher) {
            case DEBUTANT -> "fa-star";
            case INTERMEDIAIRE -> "fa-star-half-alt";
            case CONFIRME -> "fa-star";
            case EXPERIMENTE -> "fa-star";
            case SENIOR -> "fa-star";
            case EXPERT -> "fa-crown";
        };
    }

    /**
     * Retourne la couleur du niveau
     */
    public String getLevelColor() {
        if (levelTeacher == null) {
            return "gray";
        }
        return switch (levelTeacher) {
            case DEBUTANT -> "blue";
            case INTERMEDIAIRE -> "cyan";
            case CONFIRME -> "green";
            case EXPERIMENTE -> "teal";
            case SENIOR -> "purple";
            case EXPERT -> "gold";
        };
    }

    /**
     * Retourne le libellé du statut - CORRIGÉ
     */
    public String getStatusLabel() {
        if (statutUserSimple == null) {
            return "Statut non défini";
        }
        return switch (statutUserSimple) {
            case ACTIF -> "Actif";
            case EN_ATTENTE -> "En attente";
            case SUSPENDU -> "Suspendu";
            case SUPPRIME -> "Supprimé";
            case BROUILLON -> "Brouillon";
            case INACTIF -> "Inactif";
            case ENSEIGNANT -> null;
        };
    }

    /**
     * Retourne la couleur du statut - CORRIGÉ
     public String getStatusColor() {
     if (statutUserSimple == null) {
     return "gray";
     }
     return switch (statutUserSimple) {
     case ACTIF -> "green";
     case EN_ATTENTE -> "yellow";
     case SUSPENDU -> "orange";
     case SUPPRIME -> "red";
     case BROUILLON -> "blue";
     case INACTIF -> "gray";
     };
     }
     */


    /**
     * Retourne l'icône du statut
     */
    public String getStatusIcon() {
        if (statutUserSimple == null) {
            return "fa-question-circle";
        }
        return statutUserSimple.getIcon();
    }

    /**
     * Vérifie si l'enseignant peut être assigné à un cours
     */
    public boolean isAvailableForAssignment() {
        return isActive() && levelTeacher != null && levelTeacher != LevelTeacher.DEBUTANT;
    }

    /**
     * Vérifie si les informations sont complètes
     */
    public boolean isProfileComplete() {
        return teacherNumber != null && !teacherNumber.isEmpty()
                && statutUserSimple != null
                && levelTeacher != null;
    }

    /**
     * Retourne le nom complet formaté
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Vérifie si l'enseignant a un numéro d'enseignant
     */
    public boolean hasTeacherNumber() {
        return teacherNumber != null && !teacherNumber.isEmpty();
    }

    /**
     * Vérifie si le statut est modifiable
     */
    public boolean isStatusEditable() {
        return statutUserSimple != StatutUserSimple.SUPPRIME;
    }

    /**
     * Vérifie si l'enseignant peut se connecter
     */
    public boolean canLogin() {
        return statutUserSimple != null && statutUserSimple.canLogin();
    }

    /**
     * Met à jour le niveau de l'enseignant
     */
    public void updateLevel(LevelTeacher newLevel) {
        if (newLevel != null) {
            this.levelTeacher = newLevel;
        }
    }

    /**
     * Retourne la plage d'expérience du niveau
     */
    public String getExperienceRange() {
        if (levelTeacher == null) {
            return "Non défini";
        }
        return levelTeacher.getRangeDisplay();
    }

    /**
     * Vérifie si l'enseignant a un niveau supérieur ou égal à un niveau donné
     */
    public boolean hasLevelAtLeast(LevelTeacher level) {
        if (levelTeacher == null || level == null) {
            return false;
        }
        int currentLevel = getLevelOrder(levelTeacher);
        int requiredLevel = getLevelOrder(level);
        return currentLevel >= requiredLevel;
    }

    /**
     * Retourne l'ordre du niveau - CORRIGÉ
     */
    private int getLevelOrder(LevelTeacher level) {
        return switch (level) {
            case DEBUTANT -> 0;
            case INTERMEDIAIRE -> 1;
            case CONFIRME -> 2;
            case EXPERIMENTE -> 3;
            case SENIOR -> 4;
            case EXPERT -> 5;
        };
    }

    /**
     * Retourne le prochain niveau - CORRIGÉ
     */
    public LevelTeacher getNextLevel() {
        if (levelTeacher == null) {
            return LevelTeacher.DEBUTANT;
        }
        return switch (levelTeacher) {
            case DEBUTANT -> LevelTeacher.INTERMEDIAIRE;
            case INTERMEDIAIRE -> LevelTeacher.CONFIRME;
            case CONFIRME -> LevelTeacher.EXPERIMENTE;
            case EXPERIMENTE -> LevelTeacher.SENIOR;
            case SENIOR -> LevelTeacher.EXPERT;
            case EXPERT -> null;
        };
    }

    /**
     * Vérifie si l'enseignant peut être promu
     */
    public boolean canBePromoted() {
        return getNextLevel() != null && isActive();
    }

    /**
     * Promouvoir l'enseignant au niveau supérieur
     */
    public boolean promote() {
        LevelTeacher next = getNextLevel();
        if (next != null && isActive()) {
            this.levelTeacher = next;
            return true;
        }
        return false;
    }
}