package com.edueasy.common.model;

import com.edueasy.common.enums.TicketStatus;
import com.edueasy.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "support_agents")
@JsonTypeName("SUPPORT_AGENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SupportAgent extends User {

    @Column(unique = true, nullable = false)
    private String staffNumber;

    private String department;

    private String shift;

    private LocalDate hireDate;

    private String qualification;

    @Builder.Default
    private Boolean isOnCall = false;

    @ElementCollection
    @CollectionTable(
        name = "agent_responsibilities",
        joinColumns = @JoinColumn(name = "agent_id")
    )
    @Column(name = "responsibility")
    @Builder.Default
    private List<String> responsibilities = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "agent_assigned_tickets",
        joinColumns = @JoinColumn(name = "agent_id")
    )
    @Column(name = "ticket_id")
    @Builder.Default
    private List<String> assignedTickets = new ArrayList<>();

    // ===== Méthodes métier =====

    @Override
    public UserRole getRole() {
        return UserRole.SUPPORT_AGENT;
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
    public boolean isNewAgent() {
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
     * Retourne la liste des tickets actifs avec leur statut
     */
    public List<TicketStatus> getTicketsWithStatus() {
        // À implémenter selon votre modèle de données
        return new ArrayList<>();
    }
}
