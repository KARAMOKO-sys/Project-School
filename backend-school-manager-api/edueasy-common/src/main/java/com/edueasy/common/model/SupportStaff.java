package com.edueasy.common.model;

import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "support_staff")
@JsonTypeName("SUPPORT_STAFF")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SupportStaff extends User {

    @Column(unique = true, nullable = false)
    private String staffNumber;

    private String department;

    private String shift;

    @Column(name = "support_agent_uuid")
    private String supportAgentUuid;

    private LocalDate hireDate;

    private String qualification;

    @Builder.Default
    private Boolean isOnCall = false;

    @ElementCollection
    @CollectionTable(
        name = "staff_responsibilities",
        joinColumns = @JoinColumn(name = "staff_id")
    )
    @Column(name = "responsibility")
    @Builder.Default
    private List<String> responsibilities = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "staff_assigned_tickets",
        joinColumns = @JoinColumn(name = "staff_id")
    )
    @Column(name = "ticket_id")
    @Builder.Default
    private List<String> assignedTickets = new ArrayList<>();

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.SUPPORT_STAFF;
    }

    /**
     * Retourne la liste des tickets actuels
     */
    public List<String> getCurrentTickets() {
        return assignedTickets != null ? assignedTickets : new ArrayList<>();
    }

    /**
     * Vérifie si l'agent a des tickets actifs
     */
    public boolean hasActiveTickets() {
        return assignedTickets != null && !assignedTickets.isEmpty();
    }

    /**
     * Retourne le nombre de tickets actifs
     */
    public int getActiveTicketsCount() {
        return assignedTickets != null ? assignedTickets.size() : 0;
    }

    /**
     * Assigne un ticket à l'agent
     */
    public void assignTicket(String ticketId) {
        if (assignedTickets == null) {
            assignedTickets = new ArrayList<>();
        }
        if (!assignedTickets.contains(ticketId)) {
            assignedTickets.add(ticketId);
        }
    }

    /**
     * Résout un ticket (le retire de la liste)
     */
    public void resolveTicket(String ticketId) {
        if (assignedTickets != null) {
            assignedTickets.remove(ticketId);
        }
    }

    /**
     * Vérifie si l'agent a un ticket assigné
     */
    public boolean hasTicket(String ticketId) {
        return assignedTickets != null && assignedTickets.contains(ticketId);
    }

    /**
     * Ajoute une responsabilité
     */
    public void addResponsibility(String responsibility) {
        if (responsibilities == null) {
            responsibilities = new ArrayList<>();
        }
        if (!responsibilities.contains(responsibility)) {
            responsibilities.add(responsibility);
        }
    }

    /**
     * Supprime une responsabilité
     */
    public void removeResponsibility(String responsibility) {
        if (responsibilities != null) {
            responsibilities.remove(responsibility);
        }
    }

    /**
     * Vérifie si l'agent a une responsabilité spécifique
     */
    public boolean hasResponsibility(String responsibility) {
        return (
            responsibilities != null &&
            responsibilities.contains(responsibility)
        );
    }

    /**
     * Vérifie si l'agent est en service (on call)
     */
    public boolean isOnCall() {
        return isOnCall != null && isOnCall;
    }

    /**
     * Met l'agent en service
     */
    public void setOnCall() {
        this.isOnCall = true;
    }

    /**
     * Met l'agent hors service
     */
    public void setOffCall() {
        this.isOnCall = false;
    }

    /**
     * Vérifie si l'agent a un superviseur (support agent)
     */
    public boolean hasSupervisor() {
        return supportAgentUuid != null && !supportAgentUuid.isEmpty();
    }

    /**
     * Calcule l'ancienneté en années
     */
    public long getYearsOfService() {
        if (hireDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.YEARS.between(
            hireDate,
            LocalDate.now()
        );
    }

    /**
     * Vérifie si l'agent est récent (moins d'un an)
     */
    public boolean isNewStaff() {
        return getYearsOfService() < 1;
    }

    /**
     * Vérifie si l'agent est expérimenté (plus de 5 ans)
     */
    public boolean isExperienced() {
        return getYearsOfService() >= 5;
    }

    /**
     * Retourne le nombre total de responsabilités
     */
    public int getResponsibilitiesCount() {
        return responsibilities != null ? responsibilities.size() : 0;
    }

    /**
     * Calcule la charge de travail de l'agent
     * (nombre de tickets actifs vs capacité)
     */
    public double getWorkloadPercentage() {
        int activeTickets = getActiveTicketsCount();
        int maxCapacity = 10; // Capacité maximale par défaut
        return Math.min(((double) activeTickets / maxCapacity) * 100, 100.0);
    }

    /**
     * Vérifie si l'agent est disponible (en service ET pas surchargé)
     */
    public boolean isAvailable() {
        return isOnCall() && getWorkloadPercentage() < 80.0;
    }

    /**
     * Associe l'agent à un support agent superviseur
     */
    public void assignSupervisor(String supervisorUuid) {
        this.supportAgentUuid = supervisorUuid;
    }

    /**
     * Désassocie le superviseur
     */
    public void removeSupervisor() {
        this.supportAgentUuid = null;
    }

    /**
     * Vérifie si l'agent est sous la supervision d'un agent spécifique
     */
    public boolean isUnderSupervisionOf(String supervisorUuid) {
        return (
            supportAgentUuid != null && supportAgentUuid.equals(supervisorUuid)
        );
    }
}
