package com.edueasy.common.model;

import com.edueasy.common.enums.LevelStudent;
import com.edueasy.common.enums.StatutUserSimple;
import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "students_simple")
@JsonTypeName("STUDENT_SIMPLE")
@Data
@EqualsAndHashCode(callSuper = true)  // AJOUTÉ
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentSimple extends User {

    @Enumerated(EnumType.STRING)
    private StatutUserSimple statutUserSimple;

    @Enumerated(EnumType.STRING)
    private LevelStudent levelStudent;

    // ===== Méthodes de cycle de vie =====

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        super.onCreate();
        if (statutUserSimple == null) {
            statutUserSimple = StatutUserSimple.EN_ATTENTE;
        }
        if (levelStudent == null) {
            levelStudent = LevelStudent.DEBUTANT;
        }
    }

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.STUDENT_SIMPLE;
    }

    public StatutUserSimple getStatutUserSimple() {
        return this.statutUserSimple;
    }

    public LevelStudent getLevelStudent() {
        return this.levelStudent;
    }

    public void setStatutUserSimple(final StatutUserSimple statutUserSimple) {
        this.statutUserSimple = statutUserSimple;
    }

    public void setLevelStudent(final LevelStudent levelStudent) {
        this.levelStudent = levelStudent;
    }

    public String toString() {
        StatutUserSimple var10000 = this.getStatutUserSimple();
        return "StudentSimple(statutUserSimple=" + var10000 + ", levelStudent=" + this.getLevelStudent() + ")";
    }
}