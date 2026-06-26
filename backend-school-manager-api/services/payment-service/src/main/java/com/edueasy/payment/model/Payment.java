package com.edueasy.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_invoice", columnList = "invoice_id"),
                @Index(name = "idx_payment_student", columnList = "student_id"),
                @Index(name = "idx_payment_status", columnList = "status"),
                @Index(name = "idx_payment_transaction", columnList = "transaction_id"),
                @Index(name = "idx_payment_payment_date", columnList = "payment_date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "parent_id")
    private String parentId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "currency")
    @Builder.Default
    private String currency = "XOF";

    @Column(name = "payment_channel")
    private String paymentChannel;

    @Column(name = "fees")
    @Builder.Default
    private Double fees = 0.0;

    @Column(name = "net_amount")
    private Double netAmount;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "check_number")
    private String checkNumber;

    @Column(name = "online_provider")
    private String onlineProvider;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (currency == null) {
            currency = "XOF";
        }
        if (fees == null) {
            fees = 0.0;
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (retryCount == null) {
            retryCount = 0;
        }
        calculateNetAmount();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateNetAmount();
    }

    // ===== Méthodes métier =====

    /**
     * Calcule le montant net (montant - frais)
     */
    public void calculateNetAmount() {
        if (amount != null && fees != null) {
            this.netAmount = amount - fees;
            if (this.netAmount < 0) {
                this.netAmount = 0.0;
            }
        }
    }

    /**
     * Vérifie si le paiement est en attente
     */
    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    /**
     * Vérifie si le paiement a réussi
     */
    public boolean isSuccessful() {
        return status == PaymentStatus.SUCCESS;
    }

    /**
     * Vérifie si le paiement est terminé
     */
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    /**
     * Vérifie si le paiement a échoué
     */
    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    /**
     * Vérifie si le paiement est annulé
     */
    public boolean isCancelled() {
        return status == PaymentStatus.CANCELLED;
    }

    /**
     * Vérifie si le paiement est remboursé
     */
    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED;
    }

    /**
     * Vérifie si le paiement est en cours de traitement
     */
    public boolean isProcessing() {
        return status == PaymentStatus.PROCESSING;
    }

    /**
     * Vérifie si le paiement est terminé avec succès
     */
    public boolean isSuccessfullyCompleted() {
        return status == PaymentStatus.COMPLETED || status == PaymentStatus.SUCCESS;
    }

    /**
     * Vérifie si le paiement est vérifié
     */
    public boolean isVerified() {
        return isVerified != null && isVerified;
    }

    /**
     * Vérifie si le paiement a un reçu
     */
    public boolean hasReceipt() {
        return receiptUrl != null && !receiptUrl.isEmpty();
    }

    /**
     * Vérifie si le paiement a une référence de transaction
     */
    public boolean hasTransactionId() {
        return transactionId != null && !transactionId.isEmpty();
    }

    /**
     * Vérifie si le paiement a une référence
     */
    public boolean hasPaymentReference() {
        return paymentReference != null && !paymentReference.isEmpty();
    }

    /**
     * Vérifie si le paiement est un chèque
     */
    public boolean isCheck() {
        return method == PaymentMethod.CHECK;
    }

    /**
     * Vérifie si le paiement est en ligne
     */
    public boolean isOnline() {
        return method == PaymentMethod.ONLINE;
    }

    /**
     * Vérifie si le paiement est en espèces
     */
    public boolean isCash() {
        return method == PaymentMethod.CASH;
    }

    /**
     * Vérifie si le paiement est par carte
     */
    public boolean isCard() {
        return method == PaymentMethod.CARD;
    }

    /**
     * Vérifie si le paiement est par Mobile Money
     */
    public boolean isMobileMoney() {
        return method == PaymentMethod.MOBILE_MONEY;
    }

    /**
     * Vérifie si le paiement est par virement bancaire
     */
    public boolean isBankTransfer() {
        return method == PaymentMethod.BANK_TRANSFER;
    }

    /**
     * Vérifie si le paiement est par portefeuille
     */
    public boolean isWallet() {
        return method == PaymentMethod.WALLET;
    }

    /**
     * Valide le paiement
     */
    public void verify(String verifiedBy) {
        this.isVerified = true;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = LocalDateTime.now();
        if (status == PaymentStatus.PENDING) {
            this.status = PaymentStatus.SUCCESS;
        }
    }

    /**
     * Marque le paiement comme réussi
     */
    public void markAsSuccess() {
        this.status = PaymentStatus.SUCCESS;
        this.paymentDate = LocalDateTime.now();
    }

    /**
     * Marque le paiement comme terminé
     */
    public void markAsCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
    }

    /**
     * Marque le paiement comme échoué
     */
    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    /**
     * Annule le paiement
     */
    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }

    /**
     * Rembourse le paiement
     */
    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }

    /**
     * Incrémente le compteur de tentatives
     */
    public void incrementRetry() {
        if (retryCount == null) {
            retryCount = 0;
        }
        retryCount++;
    }

    /**
     * Vérifie si le paiement peut être retenté
     */
    public boolean canRetry() {
        return retryCount != null && retryCount < 3 && isFailed();
    }

    /**
     * Retourne le montant formaté
     */
    public String getFormattedAmount() {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f %s", amount, currency != null ? currency : "XOF");
    }

    /**
     * Retourne le montant net formaté
     */
    public String getFormattedNetAmount() {
        if (netAmount == null) {
            return "0.00";
        }
        return String.format("%.2f %s", netAmount, currency != null ? currency : "XOF");
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
            case PROCESSING -> "En cours";
            case SUCCESS -> "Réussi";
            case COMPLETED -> "Terminé";
            case FAILED -> "Échoué";
            case CANCELLED -> "Annulé";
            case REFUNDED -> "Remboursé";
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
            case PROCESSING -> "blue";
            case SUCCESS -> "green";
            case COMPLETED -> "teal";
            case FAILED -> "red";
            case CANCELLED -> "gray";
            case REFUNDED -> "purple";
        };
    }

    /**
     * Retourne le nom de la méthode de paiement
     */
    public String getMethodLabel() {
        if (method == null) {
            return "Non défini";
        }
        return switch (method) {
            case CARD -> "Carte bancaire";
            case MOBILE_MONEY -> "Mobile Money";
            case BANK_TRANSFER -> "Virement bancaire";
            case WALLET -> "Portefeuille";
            case CASH -> "Espèces";
            case CHECK -> "Chèque";
            case ONLINE -> "En ligne";
        };
    }

    /**
     * Retourne l'icône associée à la méthode de paiement
     */
    public String getMethodIcon() {
        if (method == null) {
            return "question-circle";
        }
        return switch (method) {
            case CARD -> "credit-card";
            case MOBILE_MONEY -> "mobile-phone";
            case BANK_TRANSFER -> "bank";
            case WALLET -> "wallet";
            case CASH -> "money-bill";
            case CHECK -> "receipt";
            case ONLINE -> "globe";
        };
    }

    /**
     * Vérifie si le paiement est valide
     */
    public boolean isValid() {
        return amount != null && amount > 0
                && method != null
                && (studentId != null || parentId != null);
    }

    /**
     * Vérifie si le paiement est récent (moins de 24h)
     */
    public boolean isRecent() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * Vérifie si le paiement peut être remboursé
     */
    public boolean isRefundable() {
        return (isSuccessful() || isCompleted()) && !isRefunded();
    }

    /**
     * Vérifie si le paiement nécessite une vérification manuelle
     */
    public boolean requiresManualVerification() {
        return method == PaymentMethod.CHECK
                || method == PaymentMethod.BANK_TRANSFER
                || method == PaymentMethod.CASH;
    }

    /**
     * Retourne le type de paiement pour affichage
     */
    public String getPaymentType() {
        if (isCheck()) {
            return "Chèque " + (checkNumber != null ? "#" + checkNumber : "");
        }
        if (isOnline()) {
            return "Paiement en ligne " + (onlineProvider != null ? "via " + onlineProvider : "");
        }
        if (isMobileMoney()) {
            return "Mobile Money";
        }
        if (isCard()) {
            return "Carte bancaire";
        }
        if (isCash()) {
            return "Espèces";
        }
        if (isBankTransfer()) {
            return "Virement bancaire" + (bankName != null ? " - " + bankName : "");
        }
        if (isWallet()) {
            return "Portefeuille";
        }
        return method != null ? method.name() : "Non défini";
    }

    /**
     * Vérifie si le paiement est en attente de confirmation
     */
    public boolean isAwaitingConfirmation() {
        return status == PaymentStatus.PENDING
                || status == PaymentStatus.PROCESSING;
    }
}