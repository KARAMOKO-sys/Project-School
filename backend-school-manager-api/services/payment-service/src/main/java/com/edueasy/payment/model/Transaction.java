package com.edueasy.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_transaction_payment", columnList = "payment_id"),
                @Index(name = "idx_transaction_gateway", columnList = "gateway_transaction_id"),
                @Index(name = "idx_transaction_status", columnList = "status"),
                @Index(name = "idx_transaction_created", columnList = "created_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    private Double amount;

    private String currency;

    private String status;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "response_message")
    private String responseMessage;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "gateway_name")
    private String gatewayName;

    @Column(name = "reference")
    private String reference;

    @Column(name = "is_success")
    @Builder.Default
    private Boolean isSuccess = false;

    @Column(name = "fees")
    @Builder.Default
    private Double fees = 0.0;

    @Column(name = "net_amount")
    private Double netAmount;

    @Column(name = "merchant_data", columnDefinition = "TEXT")
    private String merchantData;

    @Column(name = "customer_data", columnDefinition = "TEXT")
    private String customerData;

    @Column(name = "webhook_data", columnDefinition = "TEXT")
    private String webhookData;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "callback_url")
    private String callbackUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ===== Méthodes de cycle de vie =====

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (currency == null) {
            currency = "XOF";
        }
        if (status == null) {
            status = "PENDING";
        }
        if (isSuccess == null) {
            isSuccess = false;
        }
        if (fees == null) {
            fees = 0.0;
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
     * Vérifie si la transaction est réussie
     */
    public boolean isSuccessful() {
        return isSuccess != null && isSuccess;
    }

    /**
     * Vérifie si la transaction est en attente
     */
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    /**
     * Vérifie si la transaction a échoué
     */
    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(status);
    }

    /**
     * Vérifie si la transaction est annulée
     */
    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }

    /**
     * Vérifie si la transaction est en cours
     */
    public boolean isProcessing() {
        return "PROCESSING".equalsIgnoreCase(status);
    }

    /**
     * Vérifie si la transaction a été initiée
     */
    public boolean isInitiated() {
        return "INITIATED".equalsIgnoreCase(status);
    }

    /**
     * Vérifie si la transaction est terminée
     */
    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    /**
     * Vérifie si la transaction est remboursée
     */
    public boolean isRefunded() {
        return "REFUNDED".equalsIgnoreCase(status);
    }

    /**
     * Vérifie si la transaction est terminée avec succès
     */
    public boolean isCompletedSuccessfully() {
        return isSuccessful() && isCompleted();
    }

    /**
     * Vérifie si la transaction a un ID de passerelle
     */
    public boolean hasGatewayTransactionId() {
        return gatewayTransactionId != null && !gatewayTransactionId.isEmpty();
    }

    /**
     * Vérifie si la transaction a une référence
     */
    public boolean hasReference() {
        return reference != null && !reference.isEmpty();
    }

    /**
     * Vérifie si la transaction a un code de réponse
     */
    public boolean hasResponseCode() {
        return responseCode != null && !responseCode.isEmpty();
    }

    /**
     * Vérifie si la transaction a un message de réponse
     */
    public boolean hasResponseMessage() {
        return responseMessage != null && !responseMessage.isEmpty();
    }

    /**
     * Vérifie si la transaction a un request ID
     */
    public boolean hasRequestId() {
        return requestId != null && !requestId.isEmpty();
    }

    /**
     * Vérifie si la transaction a un callback URL
     */
    public boolean hasCallbackUrl() {
        return callbackUrl != null && !callbackUrl.isEmpty();
    }

    /**
     * Marque la transaction comme réussie
     */
    public void markAsSuccess() {
        this.status = "SUCCESS";
        this.isSuccess = true;
    }

    /**
     * Marque la transaction comme échouée
     */
    public void markAsFailed(String code, String message) {
        this.status = "FAILED";
        this.isSuccess = false;
        this.responseCode = code;
        this.responseMessage = message;
    }

    /**
     * Marque la transaction comme en cours
     */
    public void markAsProcessing() {
        this.status = "PROCESSING";
    }

    /**
     * Marque la transaction comme terminée
     */
    public void markAsCompleted() {
        this.status = "COMPLETED";
        this.isSuccess = true;
    }

    /**
     * Marque la transaction comme annulée
     */
    public void markAsCancelled() {
        this.status = "CANCELLED";
        this.isSuccess = false;
    }

    /**
     * Marque la transaction comme remboursée
     */
    public void markAsRefunded() {
        this.status = "REFUNDED";
        this.isSuccess = true;
    }

    /**
     * Met à jour les données du webhook
     */
    public void updateWebhookData(String webhookData) {
        this.webhookData = webhookData;
    }

    /**
     * Met à jour la réponse de la passerelle
     */
    public void updateGatewayResponse(String code, String message) {
        this.responseCode = code;
        this.responseMessage = message;
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
        if (status == null || status.isEmpty()) {
            return "Non défini";
        }
        return switch (status.toUpperCase()) {
            case "PENDING" -> "En attente";
            case "INITIATED" -> "Initié";
            case "PROCESSING" -> "En cours";
            case "SUCCESS" -> "Réussi";
            case "FAILED" -> "Échoué";
            case "CANCELLED" -> "Annulé";
            case "COMPLETED" -> "Terminé";
            case "REFUNDED" -> "Remboursé";
            default -> status;
        };
    }

    /**
     * Retourne la couleur associée au statut
     */
    public String getStatusColor() {
        if (status == null || status.isEmpty()) {
            return "gray";
        }
        return switch (status.toUpperCase()) {
            case "PENDING" -> "orange";
            case "INITIATED" -> "blue";
            case "PROCESSING" -> "purple";
            case "SUCCESS" -> "green";
            case "FAILED" -> "red";
            case "CANCELLED" -> "gray";
            case "COMPLETED" -> "teal";
            case "REFUNDED" -> "purple";
            default -> "gray";
        };
    }

    /**
     * Vérifie si la transaction est valide
     */
    public boolean isValid() {
        return amount != null && amount > 0
                && paymentId != null && !paymentId.isEmpty()
                && currency != null && !currency.isEmpty()
                && status != null && !status.isEmpty();
    }

    /**
     * Vérifie si la transaction est récente (moins de 24h)
     */
    public boolean isRecent() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * Vérifie si la transaction peut être retentée
     */
    public boolean canRetry() {
        return isFailed() || isCancelled();
    }

    /**
     * Retourne le résumé de la transaction
     */
    public String getSummary() {
        return String.format("%s - %s (%s)",
                status != null ? status : "N/A",
                getFormattedAmount(),
                reference != null ? reference : "Sans référence");
    }

    /**
     * Vérifie si la transaction est une transaction de test
     */
    public boolean isTestTransaction() {
        return "TEST".equalsIgnoreCase(transactionType);
    }

    /**
     * Vérifie si la transaction est une transaction réelle
     */
    public boolean isLiveTransaction() {
        return !isTestTransaction();
    }

    /**
     * Retourne le statut complet de la transaction
     */
    public String getFullStatus() {
        if (isSuccessful()) {
            return "SUCCESS";
        } else if (isPending()) {
            return "PENDING";
        } else if (isProcessing()) {
            return "PROCESSING";
        } else if (isFailed()) {
            return "FAILED";
        } else if (isCancelled()) {
            return "CANCELLED";
        } else if (isCompleted()) {
            return "COMPLETED";
        } else if (isRefunded()) {
            return "REFUNDED";
        }
        return status != null ? status : "UNKNOWN";
    }
}