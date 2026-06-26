package com.edueasy.common.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Exception levée lorsqu'une tentative de création d'une ressource
 * en double est détectée (violation de contrainte d'unicité).
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DuplicateResourceException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructeur principal
     */
    public DuplicateResourceException(
        String resourceName,
        String fieldName,
        Object fieldValue
    ) {
        super(buildMessage(resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructeur avec message personnalisé
     */
    public DuplicateResourceException(
        String resourceName,
        String fieldName,
        Object fieldValue,
        String customMessage
    ) {
        super(customMessage);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructeur avec cause
     */
    public DuplicateResourceException(
        String resourceName,
        String fieldName,
        Object fieldValue,
        Throwable cause
    ) {
        super(buildMessage(resourceName, fieldName, fieldValue), cause);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Construit le message d'erreur
     */
    private static String buildMessage(
        String resourceName,
        String fieldName,
        Object fieldValue
    ) {
        return String.format(
            "%s already exists with %s: '%s'",
            resourceName,
            fieldName,
            fieldValue
        );
    }

    /**
     * Retourne la valeur du champ sous forme de chaîne
     */
    public String getFieldValueAsString() {
        return fieldValue != null ? fieldValue.toString() : "null";
    }

    /**
     * Vérifie si la valeur du champ est nulle
     */
    public boolean hasNullFieldValue() {
        return fieldValue == null;
    }

    /**
     * Vérifie si le champ est une chaîne vide
     */
    public boolean hasEmptyFieldValue() {
        return (
            fieldValue instanceof String &&
            ((String) fieldValue).trim().isEmpty()
        );
    }

    /**
     * Retourne le nom complet de la ressource (capitalisé)
     */
    public String getCapitalizedResourceName() {
        if (resourceName == null || resourceName.isEmpty()) {
            return "Resource";
        }
        return (
            resourceName.substring(0, 1).toUpperCase() +
            resourceName.substring(1)
        );
    }
}
