package com.edueasy.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(
        name = "subscriptions",
        indexes = {
                @Index(name = "idx_subscription_student", columnList = "student_id"),
                @Index(name = "idx_subscription_plan", columnList = "plan_id"),
                @Index(name = "idx_subscription_status", columnList = "status"),
                @Index(name = "idx_subscription_dates", columnList = "start_date, end_date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "plan_id")
    private String planId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "is_auto_renew")
    @Builder.Default
    private Boolean isAutoRenew = true;

    @Column(name = "renewal_count")
    @Builder.Default
    private Integer renewalCount = 0;

    @Column(name = "last_renewed_at")
    private LocalDateTime lastRenewedAt;

    @Column(name = "trial_ends_at")
    private LocalDateTime trialEndsAt;

    @Column(name = "is_on_trial")
    @Builder.Default
    private Boolean isOnTrial = false;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "payment_method_id")
    private String paymentMethodId;

    @Column(name = "price")
    private Double price;

    @Column(name = "currency")
    @Builder.Default
    private String currency = "XOF";

    @Column(name = "billing_cycle")
    private String billingCycle;

    @Column(name = "billing_interval")
    private String billingInterval;

    @Column(name = "discount_applied")
    @Builder.Default
    private Double discountApplied = 0.0;

    @Column(name = "total_paid")
    private Double totalPaid;

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
        if (status == null) {
            status = SubscriptionStatus.PENDING;
        }
        if (isAutoRenew == null) {
            isAutoRenew = true;
        }
        if (renewalCount == null) {
            renewalCount = 0;
        }
        if (isOnTrial == null) {
            isOnTrial = false;
        }
        if (currency == null) {
            currency = "XOF";
        }
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        if (discountApplied == null) {
            discountApplied = 0.0;
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si l'abonnement est actif
     */
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    /**
     * Vérifie si l'abonnement est expiré
     */
    public boolean isExpired() {
        return status == SubscriptionStatus.EXPIRED;
    }

    /**
     * Vérifie si l'abonnement est annulé
     */
    public boolean isCancelled() {
        return status == SubscriptionStatus.CANCELLED;
    }

    /**
     * Vérifie si l'abonnement est en pause
     */
    public boolean isPaused() {
        return status == SubscriptionStatus.PAUSED;
    }

    /**
     * Vérifie si l'abonnement est en attente
     */
    public boolean isPending() {
        return status == SubscriptionStatus.PENDING;
    }

    /**
     * Vérifie si l'abonnement est en période d'essai
     */
    public boolean isOnTrial() {
        return isOnTrial != null && isOnTrial;
    }

    /**
     * Vérifie si l'abonnement est en renouvellement automatique
     */
    public boolean isAutoRenew() {
        return isAutoRenew != null && isAutoRenew;
    }

    /**
     * Vérifie si l'abonnement est expiré
     */
    public boolean hasExpired() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    /**
     * Vérifie si l'abonnement est sur le point d'expirer (moins de 7 jours)
     */
    public boolean isExpiringSoon() {
        if (endDate == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysFromNow = now.plusDays(7);
        return now.isBefore(endDate) && endDate.isBefore(sevenDaysFromNow);
    }

    /**
     * Vérifie si l'abonnement peut être annulé
     */
    public boolean isCancellable() {
        return isActive() || isPaused() || isPending();
    }

    /**
     * Vérifie si l'abonnement peut être renouvelé
     */
    public boolean isRenewable() {
        return (isActive() || isPending()) && isAutoRenew();
    }

    /**
     * Vérifie si l'abonnement peut être activé
     */
    public boolean isActivable() {
        return isPending() || isPaused();
    }

    /**
     * Active l'abonnement
     */
    public void activate() {
        if (isPending() || isPaused()) {
            this.status = SubscriptionStatus.ACTIVE;
            if (startDate == null) {
                this.startDate = LocalDateTime.now();
            }
        }
    }

    /**
     * Annule l'abonnement
     */
    public void cancel(String reason) {
        this.status = SubscriptionStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.isAutoRenew = false;
    }

    /**
     * Renouvelle l'abonnement
     */
    public void renew(int durationDays) {
        if (endDate == null) {
            this.endDate = LocalDateTime.now().plusDays(durationDays);
        } else {
            this.endDate = this.endDate.plusDays(durationDays);
        }
        this.renewalCount = (renewalCount != null ? renewalCount : 0) + 1;
        this.lastRenewedAt = LocalDateTime.now();
        this.status = SubscriptionStatus.ACTIVE;
        calculateTotalPaid();
    }

    /**
     * Met l'abonnement en pause
     */
    public void pause() {
        if (isActive()) {
            this.status = SubscriptionStatus.PAUSED;
        }
    }

    /**
     * Réactive l'abonnement
     */
    public void resume() {
        if (isPaused()) {
            this.status = SubscriptionStatus.ACTIVE;
            if (endDate != null && endDate.isBefore(LocalDateTime.now())) {
                extendEndDate(30);
            }
        }
    }

    /**
     * Prolonge la date de fin
     */
    public void extendEndDate(int days) {
        if (endDate == null) {
            this.endDate = LocalDateTime.now().plusDays(days);
        } else {
            this.endDate = this.endDate.plusDays(days);
        }
    }

    /**
     * Active la période d'essai
     */
    public void startTrial(int trialDays) {
        this.isOnTrial = true;
        this.trialEndsAt = LocalDateTime.now().plusDays(trialDays);
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
        this.endDate = this.trialEndsAt;
    }

    /**
     * Désactive le renouvellement automatique
     */
    public void disableAutoRenew() {
        this.isAutoRenew = false;
    }

    /**
     * Active le renouvellement automatique
     */
    public void enableAutoRenew() {
        this.isAutoRenew = true;
    }

    /**
     * Calcule le montant total payé
     */
    public void calculateTotalPaid() {
        if (price != null) {
            this.totalPaid = price - (discountApplied != null ? discountApplied : 0.0);
            if (this.totalPaid < 0) {
                this.totalPaid = 0.0;
            }
        }
    }

    /**
     * Applique une remise
     */
    public void applyDiscount(double discount) {
        this.discountApplied = discount;
        calculateTotalPaid();
    }

    /**
     * Vérifie si l'abonnement est valide
     */
    public boolean isValid() {
        return studentId != null && !studentId.isEmpty()
                && planId != null && !planId.isEmpty()
                && status != null
                && startDate != null
                && price != null && price > 0;
    }

    /**
     * Retourne le nombre de jours restants
     */
    public long getDaysRemaining() {
        if (endDate == null) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(now, endDate);
    }

    /**
     * Retourne le statut en français
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case ACTIVE -> "Actif";
            case EXPIRED -> "Expiré";
            case CANCELLED -> "Annulé";
            case PAUSED -> "En pause";
            case PENDING -> "En attente";
        };
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (status == null) {
            return "gray";
        }
        return switch (status) {
            case ACTIVE -> "green";
            case EXPIRED -> "red";
            case CANCELLED -> "gray";
            case PAUSED -> "orange";
            case PENDING -> "yellow";
        };
    }

    /**
     * Retourne la durée restante formatée
     */
    public String getFormattedRemainingTime() {
        long days = getDaysRemaining();
        if (days == 0) {
            return "Expiré";
        }
        if (days < 30) {
            return days + " jour" + (days > 1 ? "s" : "");
        }
        long months = days / 30;
        long remainingDays = days % 30;
        if (remainingDays == 0) {
            return months + " mois";
        }
        return months + " mois et " + remainingDays + " jour" + (remainingDays > 1 ? "s" : "");
    }

    /**
     * Vérifie si l'abonnement est récent (moins de 30 jours)
     */
    public boolean isRecent() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    /**
     * Vérifie si l'abonnement a été renouvelé au moins une fois
     */
    public boolean hasBeenRenewed() {
        return renewalCount != null && renewalCount > 0;
    }

    /**
     * Vérifie si la période d'essai est terminée
     */
    public boolean isTrialExpired() {
        if (trialEndsAt == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(trialEndsAt);
    }

    /**
     * Marque l'abonnement comme expiré
     */
    public void markAsExpired() {
        this.status = SubscriptionStatus.EXPIRED;
        this.isAutoRenew = false;
    }

    /**
     * Vérifie si l'abonnement a un paiement associé
     */
    public boolean hasPaymentMethod() {
        return paymentMethodId != null && !paymentMethodId.isEmpty();
    }

    /**
     * Retourne le montant formaté
     */
    public String getFormattedPrice() {
        if (price == null) {
            return "0.00";
        }
        return String.format("%.2f %s", price, currency != null ? currency : "XOF");
    }

    /**
     * Retourne le montant total payé formaté
     */
    public String getFormattedTotalPaid() {
        if (totalPaid == null) {
            return "0.00";
        }
        return String.format("%.2f %s", totalPaid, currency != null ? currency : "XOF");
    }

    /**
     * Vérifie si l'abonnement est en cours de renouvellement
     */
    public boolean isRenewing() {
        return isActive() && lastRenewedAt != null
                && lastRenewedAt.isAfter(LocalDateTime.now().minusDays(1));
    }

    /**
     * Calcule la prochaine date de facturation
     */
    public LocalDateTime getNextBillingDate() {
        if (endDate == null) {
            return null;
        }
        if (isAutoRenew() && isActive()) {
            return endDate;
        }
        return null;
    }
}