package com.edueasy.assessment.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> options;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Builder.Default
    private Double points = 1.0;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "order_index")
    private Integer orderIndex;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (points == null) {
            points = 1.0;
        }
        if (orderIndex == null) {
            orderIndex = 0;
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si la réponse est correcte
     */
    public boolean isCorrect(String answer) {
        if (correctAnswer == null || answer == null) {
            return false;
        }
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }

    /**
     * Vérifie si la question est à choix multiple
     */
    public boolean isMultipleChoice() {
        return type == QuestionType.MULTIPLE_CHOICE;
    }

    /**
     * Vérifie si la question est à choix unique
     */
    public boolean isSingleChoice() {
        return type == QuestionType.SINGLE_CHOICE;
    }

    /**
     * Vérifie si la question est ouverte
     */
    public boolean isOpenEnded() {
        return type == QuestionType.OPEN_ENDED;
    }

    /**
     * Vérifie si la question est vrai/faux
     */
    public boolean isTrueFalse() {
        return type == QuestionType.TRUE_FALSE;
    }

    /**
     * Vérifie si la question a des options
     */
    public boolean hasOptions() {
        return options != null && !options.isEmpty();
    }

    /**
     * Vérifie si la question a une explication
     */
    public boolean hasExplanation() {
        return explanation != null && !explanation.isEmpty();
    }

    /**
     * Retourne le nombre d'options
     */
    public int getOptionsCount() {
        return options != null ? options.size() : 0;
    }

    /**
     * Vérifie si les points sont valides
     */
    public boolean hasValidPoints() {
        return points != null && points > 0;
    }

    /**
     * Vérifie si la question a une réponse correcte
     */
    public boolean hasCorrectAnswer() {
        return correctAnswer != null && !correctAnswer.isEmpty();
    }

    /**
     * Valide la question
     */
    public boolean isValid() {
        return text != null && !text.isEmpty()
                && type != null
                && hasValidPoints()
                && (hasOptions() || isOpenEnded())
                && (hasCorrectAnswer() || isOpenEnded());
    }

    /**
     * Met à jour l'ordre
     */
    public void setOrder(int order) {
        this.orderIndex = order;
    }

    /**
     * Calcule le score pour une réponse
     */
    public Double calculateScore(String answer) {
        if (!isValid()) {
            return 0.0;
        }
        if (isOpenEnded()) {
            // Pour les questions ouvertes, le score doit être attribué manuellement
            return null;
        }
        return isCorrect(answer) ? points : 0.0;
    }

    /**
     * Ajoute une option
     */
    public void addOption(String option) {
        if (options == null) {
            options = new java.util.ArrayList<>();
        }
        if (!options.contains(option)) {
            options.add(option);
        }
    }

    /**
     * Supprime une option
     */
    public void removeOption(String option) {
        if (options != null) {
            options.remove(option);
        }
    }

    /**
     * Supprime toutes les options
     */
    public void clearOptions() {
        if (options != null) {
            options.clear();
        }
    }

    /**
     * Vérifie si l'option existe
     */
    public boolean hasOption(String option) {
        return options != null && options.contains(option);
    }

    /**
     * Retourne l'index de l'option
     */
    public int getOptionIndex(String option) {
        return options != null ? options.indexOf(option) : -1;
    }

    /**
     * Retourne la question avec son index pour l'affichage
     */
    public String getDisplayText() {
        if (orderIndex != null) {
            return "Question " + (orderIndex + 1) + ": " + text;
        }
        return text;
    }

    /**
     * Vérifie si la question a été répondu
     */
    public boolean isAnswered(String answer) {
        return answer != null && !answer.trim().isEmpty();
    }
}