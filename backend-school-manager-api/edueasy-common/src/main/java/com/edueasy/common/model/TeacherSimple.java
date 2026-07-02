// TeacherSimple.java - TOUT EN UNE SEULE TABLE
package com.edueasy.common.model;

import com.edueasy.common.enums.LevelTeacher;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "teachers_simple")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TeacherSimple extends User {

    // 🔥 TOUS LES CHAMPS SONT DANS LA TABLE teachers_simple
    // Les champs communs (firstName, lastName, email, etc.) sont hérités de User

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_user_simple")
    private StatutUserSimple statutUserSimple;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_teacher")
    private LevelTeacher levelTeacher;

    @Column(unique = true, nullable = false, name = "teacher_number")
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

    public void generateTeacherNumber() {
        if (teacherNumber == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            this.teacherNumber = "TCH-" + timestamp + "-" + random;
        }
    }

    public boolean isActive() {
        return statutUserSimple == StatutUserSimple.ACTIF;
    }

    public boolean isPending() {
        return statutUserSimple == StatutUserSimple.EN_ATTENTE;
    }

    public boolean isSuspended() {
        return statutUserSimple == StatutUserSimple.SUSPENDU;
    }

    public boolean isDeleted() {
        return statutUserSimple == StatutUserSimple.SUPPRIME;
    }

    public boolean isDraft() {
        return statutUserSimple == StatutUserSimple.BROUILLON;
    }

    public boolean isInactive() {
        return statutUserSimple == StatutUserSimple.INACTIF;
    }

    public void activate() {
        this.statutUserSimple = StatutUserSimple.ACTIF;
    }

    public void suspend() {
        this.statutUserSimple = StatutUserSimple.SUSPENDU;
    }

    public void setPending() {
        this.statutUserSimple = StatutUserSimple.EN_ATTENTE;
    }

    public void delete() {
        this.statutUserSimple = StatutUserSimple.SUPPRIME;
    }

    public void setDraft() {
        this.statutUserSimple = StatutUserSimple.BROUILLON;
    }

    public void setInactive() {
        this.statutUserSimple = StatutUserSimple.INACTIF;
    }

    public boolean hasLevel(LevelTeacher level) {
        return this.levelTeacher == level;
    }

    public boolean isBeginner() {
        return levelTeacher == LevelTeacher.DEBUTANT;
    }

    public boolean isIntermediate() {
        return levelTeacher == LevelTeacher.INTERMEDIAIRE;
    }

    public boolean isConfirmed() {
        return levelTeacher == LevelTeacher.CONFIRME;
    }

    public boolean isExperienced() {
        return levelTeacher == LevelTeacher.EXPERIMENTE;
    }

    public boolean isSenior() {
        return levelTeacher == LevelTeacher.SENIOR;
    }

    public boolean isExpert() {
        return levelTeacher == LevelTeacher.EXPERT;
    }

    public boolean isAssistant() {
        return levelTeacher == LevelTeacher.ASSISTANT;
    }

    public boolean isJunior() {
        return levelTeacher == LevelTeacher.JUNIOR;
    }

    public boolean isProfessor() {
        return levelTeacher == LevelTeacher.PROFESSOR;
    }

    public boolean isMiddle() {
        return levelTeacher == LevelTeacher.MIDDLE;
    }

    public boolean isOther() {
        return levelTeacher == LevelTeacher.OTHER;
    }

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
            case MIDDLE -> "Niveau intermédiaire";
            case ASSISTANT -> "Assistant - En apprentissage";
            case JUNIOR -> "Junior - Débutant";
            case PROFESSOR -> "Professeur - Confirmé";
            case OTHER -> "Autre niveau";
        };
    }

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
            case MIDDLE -> "fa-star-half-alt";
            case ASSISTANT -> "fa-user-plus";
            case JUNIOR -> "fa-user-graduate";
            case PROFESSOR -> "fa-chalkboard-teacher";
            case OTHER -> "fa-question-circle";
        };
    }

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
            case MIDDLE -> "cyan";
            case ASSISTANT -> "light-blue";
            case JUNIOR -> "blue";
            case PROFESSOR -> "purple";
            case OTHER -> "gray";
        };
    }

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
            case ENSEIGNANT -> "Enseignant";
        };
    }

    public String getStatusIcon() {
        if (statutUserSimple == null) {
            return "fa-question-circle";
        }
        return statutUserSimple.getIcon();
    }

    public boolean isAvailableForAssignment() {
        return isActive() && levelTeacher != null &&
                levelTeacher != LevelTeacher.DEBUTANT &&
                levelTeacher != LevelTeacher.ASSISTANT &&
                levelTeacher != LevelTeacher.JUNIOR;
    }

    public boolean isProfileComplete() {
        return teacherNumber != null && !teacherNumber.isEmpty()
                && statutUserSimple != null
                && levelTeacher != null;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public boolean hasTeacherNumber() {
        return teacherNumber != null && !teacherNumber.isEmpty();
    }

    public boolean isStatusEditable() {
        return statutUserSimple != StatutUserSimple.SUPPRIME;
    }

    public boolean canLogin() {
        return statutUserSimple != null && statutUserSimple.canLogin();
    }

    public void updateLevel(LevelTeacher newLevel) {
        if (newLevel != null) {
            this.levelTeacher = newLevel;
        }
    }

    public String getExperienceRange() {
        if (levelTeacher == null) {
            return "Non défini";
        }
        return levelTeacher.getRangeDisplay();
    }

    public boolean hasLevelAtLeast(LevelTeacher level) {
        if (levelTeacher == null || level == null) {
            return false;
        }
        int currentLevel = getLevelOrder(levelTeacher);
        int requiredLevel = getLevelOrder(level);
        return currentLevel >= requiredLevel;
    }

    private int getLevelOrder(LevelTeacher level) {
        return switch (level) {
            case DEBUTANT -> 0;
            case ASSISTANT -> 0;
            case JUNIOR -> 0;
            case INTERMEDIAIRE -> 1;
            case MIDDLE -> 1;
            case CONFIRME -> 2;
            case EXPERIMENTE -> 3;
            case SENIOR -> 4;
            case PROFESSOR -> 4;
            case EXPERT -> 5;
            case OTHER -> 0;
        };
    }

    public LevelTeacher getNextLevel() {
        if (levelTeacher == null) {
            return LevelTeacher.DEBUTANT;
        }
        return switch (levelTeacher) {
            case DEBUTANT -> LevelTeacher.INTERMEDIAIRE;
            case ASSISTANT -> LevelTeacher.JUNIOR;
            case JUNIOR -> LevelTeacher.INTERMEDIAIRE;
            case INTERMEDIAIRE -> LevelTeacher.CONFIRME;
            case MIDDLE -> LevelTeacher.CONFIRME;
            case CONFIRME -> LevelTeacher.EXPERIMENTE;
            case EXPERIMENTE -> LevelTeacher.SENIOR;
            case SENIOR -> LevelTeacher.EXPERT;
            case PROFESSOR -> LevelTeacher.EXPERT;
            case EXPERT -> null;
            case OTHER -> null;
        };
    }

    public boolean canBePromoted() {
        return getNextLevel() != null && isActive();
    }

    public boolean promote() {
        LevelTeacher next = getNextLevel();
        if (next != null && isActive()) {
            this.levelTeacher = next;
            return true;
        }
        return false;
    }

    /**
     * Vérifie si l'enseignant est un niveau valide pour l'enseignement
     */
    public boolean isValidTeacher() {
        return levelTeacher != null &&
                levelTeacher != LevelTeacher.OTHER &&
                isActive();
    }

    /**
     * Vérifie si l'enseignant peut être mentor
     */
    public boolean canBeMentor() {
        return levelTeacher != null &&
                (levelTeacher == LevelTeacher.SENIOR ||
                        levelTeacher == LevelTeacher.EXPERT ||
                        levelTeacher == LevelTeacher.PROFESSOR) &&
                isActive();
    }

    /**
     * Vérifie si l'enseignant peut encadrer des stagiaires
     */
    public boolean canSuperviseInterns() {
        return levelTeacher != null &&
                (levelTeacher == LevelTeacher.EXPERIMENTE ||
                        levelTeacher == LevelTeacher.SENIOR ||
                        levelTeacher == LevelTeacher.EXPERT ||
                        levelTeacher == LevelTeacher.PROFESSOR) &&
                isActive();
    }

    /**
     * Obtient le niveau d'enseignement formaté
     */
    public String getFormattedLevel() {
        if (levelTeacher == null) {
            return "N/A";
        }
        return levelTeacher.getLabel() + " (" + levelTeacher.getRangeDisplay() + ")";
    }

    /**
     * Vérifie si l'enseignant est éligible pour une promotion
     */
    public boolean isEligibleForPromotion() {
        if (!isActive()) return false;
        if (levelTeacher == null) return false;
        return switch (levelTeacher) {
            case DEBUTANT, ASSISTANT, JUNIOR, INTERMEDIAIRE, MIDDLE, CONFIRME, EXPERIMENTE, SENIOR, PROFESSOR -> true;
            case EXPERT, OTHER -> false;
        };
    }
}