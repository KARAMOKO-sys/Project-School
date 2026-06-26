package com.edueasy.common.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleConstraints implements Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<LocalDate> daysOff = new ArrayList<>();

    @Builder.Default
    private Map<LocalDate, List<String>> unavailability = new HashMap<>();

    @Builder.Default
    private Map<Integer, List<String>> weeklyAvailability = new HashMap<>();

    @Builder.Default
    private Integer maxHoursPerDay = 8;

    @Builder.Default
    private Integer minHoursPerDay = 0;

    @Builder.Default
    private String preferredStartTime = "08:00";

    @Builder.Default
    private String preferredEndTime = "18:00";

    @Builder.Default
    private List<DayOfWeek> weeklyDaysOff = new ArrayList<>();

    private String specialConstraints;

    // ===== Méthodes métier =====

    /**
     * Vérifie si un créneau horaire est disponible pour une date donnée
     */
    public boolean isAvailable(LocalDate date, String timeSlot) {
        if (date == null) {
            return true;
        }

        // Vérifier si c'est un jour off
        if (daysOff != null && daysOff.contains(date)) {
            return false;
        }

        // Vérifier les indisponibilités spécifiques
        if (unavailability != null && unavailability.containsKey(date)) {
            List<String> unavailableSlots = unavailability.get(date);
            if (
                unavailableSlots != null && unavailableSlots.contains(timeSlot)
            ) {
                return false;
            }
        }

        // Vérifier les jours off hebdomadaires
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (weeklyDaysOff != null && weeklyDaysOff.contains(dayOfWeek)) {
            return false;
        }

        // Vérifier la disponibilité hebdomadaire
        int dayIndex = dayOfWeek.getValue() - 1;
        if (
            weeklyAvailability != null &&
            weeklyAvailability.containsKey(dayIndex)
        ) {
            List<String> availableSlots = weeklyAvailability.get(dayIndex);
            return availableSlots != null && availableSlots.contains(timeSlot);
        }

        return true;
    }

    /**
     * Vérifie si une date est disponible (sans vérifier le créneau)
     */
    public boolean isAvailableOnDate(LocalDate date) {
        if (date == null) {
            return true;
        }

        if (daysOff != null && daysOff.contains(date)) {
            return false;
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return weeklyDaysOff == null || !weeklyDaysOff.contains(dayOfWeek);
    }

    /**
     * Retourne la liste des créneaux disponibles pour une date donnée
     */
    public List<String> getAvailableSlots(LocalDate date) {
        if (!isAvailableOnDate(date)) {
            return new ArrayList<>();
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayIndex = dayOfWeek.getValue() - 1;

        List<String> slots = new ArrayList<>();
        if (
            weeklyAvailability != null &&
            weeklyAvailability.containsKey(dayIndex)
        ) {
            slots = new ArrayList<>(weeklyAvailability.get(dayIndex));
        }

        // Filtrer les indisponibilités spécifiques
        if (unavailability != null && unavailability.containsKey(date)) {
            List<String> unavailableSlots = unavailability.get(date);
            if (unavailableSlots != null) {
                slots.removeAll(unavailableSlots);
            }
        }

        return slots;
    }

    /**
     * Ajoute un jour off
     */
    public void addDayOff(LocalDate date) {
        if (daysOff == null) {
            daysOff = new ArrayList<>();
        }
        if (!daysOff.contains(date)) {
            daysOff.add(date);
        }
    }

    /**
     * Supprime un jour off
     */
    public void removeDayOff(LocalDate date) {
        if (daysOff != null) {
            daysOff.remove(date);
        }
    }

    /**
     * Ajoute une indisponibilité pour une date et un créneau spécifique
     */
    public void addUnavailability(LocalDate date, String timeSlot) {
        if (unavailability == null) {
            unavailability = new HashMap<>();
        }
        unavailability
            .computeIfAbsent(date, k -> new ArrayList<>())
            .add(timeSlot);
    }

    /**
     * Supprime une indisponibilité
     */
    public void removeUnavailability(LocalDate date, String timeSlot) {
        if (unavailability != null && unavailability.containsKey(date)) {
            List<String> slots = unavailability.get(date);
            if (slots != null) {
                slots.remove(timeSlot);
                if (slots.isEmpty()) {
                    unavailability.remove(date);
                }
            }
        }
    }

    /**
     * Définit la disponibilité hebdomadaire (1 = Lundi, 7 = Dimanche)
     */
    public void setWeeklyAvailability(int dayOfWeek, List<String> timeSlots) {
        if (weeklyAvailability == null) {
            weeklyAvailability = new HashMap<>();
        }
        weeklyAvailability.put(dayOfWeek - 1, timeSlots);
    }

    /**
     * Ajoute un jour off hebdomadaire
     */
    public void addWeeklyDayOff(DayOfWeek dayOfWeek) {
        if (weeklyDaysOff == null) {
            weeklyDaysOff = new ArrayList<>();
        }
        if (!weeklyDaysOff.contains(dayOfWeek)) {
            weeklyDaysOff.add(dayOfWeek);
        }
    }

    /**
     * Supprime un jour off hebdomadaire
     */
    public void removeWeeklyDayOff(DayOfWeek dayOfWeek) {
        if (weeklyDaysOff != null) {
            weeklyDaysOff.remove(dayOfWeek);
        }
    }

    /**
     * Vérifie si un jour est un jour off
     */
    public boolean isDayOff(LocalDate date) {
        if (date == null) {
            return false;
        }
        return (
            (daysOff != null && daysOff.contains(date)) ||
            (weeklyDaysOff != null &&
                weeklyDaysOff.contains(date.getDayOfWeek()))
        );
    }

    /**
     * Vérifie si la date est dans la plage horaire préférée
     */
    public boolean isWithinPreferredHours(String timeSlot) {
        if (
            timeSlot == null ||
            preferredStartTime == null ||
            preferredEndTime == null
        ) {
            return true;
        }
        // Comparer les heures (format "HH:MM")
        return (
            timeSlot.compareTo(preferredStartTime) >= 0 &&
            timeSlot.compareTo(preferredEndTime) <= 0
        );
    }

    /**
     * Vérifie si les contraintes sont valides
     */
    public boolean isValid() {
        if (maxHoursPerDay != null && minHoursPerDay != null) {
            return maxHoursPerDay >= minHoursPerDay;
        }
        return true;
    }

    /**
     * Nettoie les dates passées
     */
    public void cleanPastDates() {
        LocalDate today = LocalDate.now();
        if (daysOff != null) {
            daysOff.removeIf(date -> date.isBefore(today));
        }
        if (unavailability != null) {
            unavailability.keySet().removeIf(date -> date.isBefore(today));
        }
    }
}
