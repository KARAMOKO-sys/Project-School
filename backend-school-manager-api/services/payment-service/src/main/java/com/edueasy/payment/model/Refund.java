package com.edueasy.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "refunds",
        indexes = {
                @Index(name = "idx_refund_payment", columnList = "payment_id"),
                @Index(name = "idx_refund_invoice", columnList = "invoice_id"),
                @Index(name = "idx_refund_status", columnList = "status"),
                @Index(name = "idx_refund_requested", columnList = "requested_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "invoice_id")
    private String invoiceId;

    private Double amount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "is_full_refund")
    @Builder.Default
    private Boolean isFullRefund = true;

    @Column(name = "refund_reference")
    private String refundReference;

    @Column(name = "processed_by")
    private String processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = RefundStatus.PENDING;
        }
        if (isFullRefund == null) {
            isFullRefund = true;
        }
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
    }

    // ===== Méthodes métier =====

    /**
     * Vérifie si le remboursement est en attente
     */
    public boolean isPending() {
        return status == RefundStatus.PENDING;
    }

    /**
     * Vérifie si le remboursement est approuvé
     */
    public boolean isApproved() {
        return status == RefundStatus.APPROVED;
    }

    /**
     * Vérifie si le remboursement est rejeté
     */
    public boolean isRejected() {
        return status == RefundStatus.REJECTED;
    }

    /**
     * Vérifie si le remboursement est en cours
     */
    public boolean isProcessing() {
        return status == RefundStatus.PROCESSING;
    }

    /**
     * Vérifie si le remboursement est terminé
     */
    public boolean isCompleted() {
        return status == RefundStatus.COMPLETED;
    }

    /**
     * Vérifie si le remboursement a échoué
     */
    public boolean isFailed() {
        return status == RefundStatus.FAILED;
    }

    /**
     * Vérifie si le remboursement est annulé
     */
    public boolean isCancelled() {
        return status == RefundStatus.CANCELLED;
    }

    /**
     * Vérifie si le remboursement est complet
     */
    public boolean isFullRefund() {
        return isFullRefund != null && isFullRefund;
    }

    /**
     * Vérifie si le remboursement est partiel
     */
    public boolean isPartialRefund() {
        return isFullRefund != null && !isFullRefund;
    }

    /**
     * Vérifie si le remboursement a une raison
     */
    public boolean hasReason() {
        return reason != null && !reason.isEmpty();
    }

    /**
     * Vérifie si le remboursement a des notes
     */
    public boolean hasNotes() {
        return notes != null && !notes.isEmpty();
    }

    /**
     * Vérifie si le remboursement a été approuvé
     */
    public boolean hasBeenApproved() {
        return approvedBy != null && approvedAt != null;
    }

    /**
     * Vérifie si le remboursement a été traité
     */
    public boolean hasBeenProcessed() {
        return processedBy != null && processedAt != null;
    }

    /**
     * Approuve le remboursement
     */
    public void approve(String approvedBy) {
        this.status = RefundStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Rejette le remboursement
     */
    public void reject(String approvedBy, String reason) {
        this.status = RefundStatus.REJECTED;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        this.failureReason = reason;
    }

    /**
     * Annule le remboursement
     */
    public void cancel() {
        this.status = RefundStatus.CANCELLED;
    }

    /**
     * Traite le remboursement
     */
    public void process(String processedBy) {
        this.status = RefundStatus.PROCESSING;
        this.processedBy = processedBy;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Marque le remboursement comme terminé
     */
    public void complete(String refundReference) {
        this.status = RefundStatus.COMPLETED;
        this.refundReference = refundReference;
    }

    /**
     * Marque le remboursement comme échoué
     */
    public void fail(String reason) {
        this.status = RefundStatus.FAILED;
        this.failureReason = reason;
    }

    /**
     * Ajoute des notes
     */
    public void addNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Retourne le statut en français
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case PENDING -> "En attente";
            case APPROVED -> "Approuvé";
            case REJECTED -> "Rejeté";
            case PROCESSING -> "En cours";
            case COMPLETED -> "Terminé";
            case FAILED -> "Échoué";
            case CANCELLED -> "Annulé";
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
            case PENDING -> "orange";
            case APPROVED -> "blue";
            case REJECTED -> "red";
            case PROCESSING -> "purple";
            case COMPLETED -> "green";
            case FAILED -> "darkred";
            case CANCELLED -> "gray";
        };
    }

    /**
     * Retourne le montant formaté
     */
    public String getFormattedAmount() {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f", amount);
    }

    /**
     * Vérifie si le remboursement est valide
     */
    public boolean isValid() {
        return (paymentId != null || invoiceId != null)
                && amount != null && amount > 0
                && reason != null && !reason.isEmpty();
    }

    /**
     * Vérifie si le remboursement peut être traité
     */
    public boolean isProcessable() {
        return isApproved() || isPending();
    }

    /**
     * Vérifie si le remboursement est récent (moins de 7 jours)
     */
    public boolean isRecent() {
        if (requestedAt == null) {
            return false;
        }
        return requestedAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * Retourne le type de remboursement
     */
    public String getRefundTypeLabel() {
        return isFullRefund() ? "Remboursement total" : "Remboursement partiel";
    }

    /**
     * Vérifie si le remboursement est urgent (plus de 3 jours en attente)
     */
    public boolean isUrgent() {
        if (!isPending() || requestedAt == null) {
            return false;
        }
        return requestedAt.isBefore(LocalDateTime.now().minusDays(3));
    }
}