package com.edueasy.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "invoices",
        indexes = {
                @Index(name = "idx_invoice_number", columnList = "invoice_number"),
                @Index(name = "idx_invoice_student", columnList = "student_id"),
                @Index(name = "idx_invoice_school", columnList = "school_id"),
                @Index(name = "idx_invoice_status", columnList = "status"),
                @Index(name = "idx_invoice_due_date", columnList = "due_date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "school_id")
    private String schoolId;

    private Double amount;

    @Column(name = "paid_amount")
    @Builder.Default
    private Double paidAmount = 0.0;

    @Column(name = "remaining_amount")
    private Double remainingAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> items = new HashMap<>();

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount")
    @Builder.Default
    private Double discount = 0.0;

    @Column(name = "tax")
    @Builder.Default
    private Double tax = 0.0;

    @Column(name = "subtotal")
    private Double subtotal;

    @Column(name = "currency")
    @Builder.Default
    private String currency = "XOF";

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "is_overdue")
    @Builder.Default
    private Boolean isOverdue = false;

    @Column(name = "overdue_days")
    private Integer overdueDays;

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
        if (paidAmount == null) {
            paidAmount = 0.0;
        }
        if (discount == null) {
            discount = 0.0;
        }
        if (tax == null) {
            tax = 0.0;
        }
        if (currency == null) {
            currency = "XOF";
        }
        if (items == null) {
            items = new HashMap<>();
        }
        if (isOverdue == null) {
            isOverdue = false;
        }
        if (status == null) {
            status = InvoiceStatus.UNPAID;
        }
        calculateRemaining();
        checkOverdue();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateRemaining();
        checkOverdue();
    }

    // ===== Méthodes métier =====

    /**
     * Calcule le montant restant
     */
    public void calculateRemaining() {
        if (amount != null && paidAmount != null) {
            this.remainingAmount = amount - paidAmount;
            if (this.remainingAmount < 0) {
                this.remainingAmount = 0.0;
            }
            updateStatus();
        }
    }

    /**
     * Met à jour le statut de la facture
     */
    public void updateStatus() {
        if (remainingAmount == null) {
            return;
        }
        // Ne pas modifier si le statut est déjà DRAFT, PENDING ou CANCELLED
        if (status == InvoiceStatus.DRAFT || status == InvoiceStatus.CANCELLED) {
            return;
        }
        if (remainingAmount <= 0.0) {
            this.status = InvoiceStatus.PAID;
        } else if (paidAmount != null && paidAmount > 0.0) {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        } else if (isOverdue != null && isOverdue) {
            this.status = InvoiceStatus.OVERDUE;
        } else {
            this.status = InvoiceStatus.UNPAID;
        }
    }

    /**
     * Vérifie si la facture est en retard
     */
    public void checkOverdue() {
        if (dueDate != null && status != InvoiceStatus.PAID && status != InvoiceStatus.CANCELLED) {
            if (LocalDateTime.now().isAfter(dueDate)) {
                this.isOverdue = true;
                long days = java.time.Duration.between(dueDate, LocalDateTime.now()).toDays();
                this.overdueDays = (int) days;
                if (status != InvoiceStatus.DRAFT && status != InvoiceStatus.PENDING) {
                    this.status = InvoiceStatus.OVERDUE;
                }
            } else {
                this.isOverdue = false;
                this.overdueDays = 0;
            }
        }
    }

    /**
     * Vérifie si la facture est payée
     */
    public boolean isPaid() {
        return status == InvoiceStatus.PAID;
    }

    /**
     * Vérifie si la facture est non payée
     */
    public boolean isUnpaid() {
        return status == InvoiceStatus.UNPAID;
    }

    /**
     * Vérifie si la facture est partiellement payée
     */
    public boolean isPartiallyPaid() {
        return status == InvoiceStatus.PARTIALLY_PAID;
    }

    /**
     * Vérifie si la facture est annulée
     */
    public boolean isCancelled() {
        return status == InvoiceStatus.CANCELLED;
    }

    /**
     * Vérifie si la facture est en brouillon
     */
    public boolean isDraft() {
        return status == InvoiceStatus.DRAFT;
    }

    /**
     * Vérifie si la facture est en attente
     */
    public boolean isPending() {
        return status == InvoiceStatus.PENDING;
    }

    /**
     * Vérifie si la facture est en retard
     */
    public boolean isOverdue() {
        return status == InvoiceStatus.OVERDUE;
    }

    /**
     * Vérifie si la facture est modifiable
     */
    public boolean isModifiable() {
        return status == InvoiceStatus.DRAFT || status == InvoiceStatus.UNPAID;
    }

    /**
     * Vérifie si la facture a des items
     */
    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    /**
     * Vérifie si la facture a une description
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * Vérifie si la facture a un paiement
     */
    public boolean hasPayment() {
        return paidAmount != null && paidAmount > 0;
    }

    /**
     * Ajoute un paiement
     */
    public void addPayment(double amount, String paymentMethod, String transactionId) {
        if (this.paidAmount == null) {
            this.paidAmount = 0.0;
        }
        this.paidAmount += amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentDate = LocalDateTime.now();
        calculateRemaining();
        checkOverdue();
    }

    /**
     * Soumet la facture (passe de DRAFT à UNPAID)
     */
    public void submit() {
        if (status == InvoiceStatus.DRAFT) {
            this.status = InvoiceStatus.UNPAID;
            checkOverdue();
        }
    }

    /**
     * Annule la facture
     */
    public void cancel() {
        this.status = InvoiceStatus.CANCELLED;
    }

    /**
     * Applique une remise
     */
    public void applyDiscount(double discount) {
        if (!isModifiable()) {
            return;
        }
        this.discount = discount;
        if (this.amount != null) {
            double newAmount = this.amount - discount;
            if (newAmount < 0) {
                this.amount = 0.0;
            } else {
                this.amount = newAmount;
            }
            calculateRemaining();
        }
    }

    /**
     * Met la facture en attente
     */
    public void setPending() {
        if (status == InvoiceStatus.DRAFT || status == InvoiceStatus.UNPAID) {
            this.status = InvoiceStatus.PENDING;
        }
    }

    /**
     * Retourne le montant total formaté
     */
    public String getFormattedAmount() {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f %s", amount, currency != null ? currency : "XOF");
    }

    /**
     * Retourne le montant restant formaté
     */
    public String getFormattedRemaining() {
        if (remainingAmount == null) {
            return "0.00";
        }
        return String.format("%.2f %s", remainingAmount, currency != null ? currency : "XOF");
    }

    /**
     * Retourne le statut en français
     */
    public String getStatusLabel() {
        if (status == null) {
            return "Non défini";
        }
        return switch (status) {
            case UNPAID -> "Non payée";
            case DRAFT -> "Brouillon";
            case PENDING -> "En attente";
            case PARTIALLY_PAID -> "Partiellement payée";
            case PAID -> "Payée";
            case OVERDUE -> "En retard";
            case CANCELLED -> "Annulée";
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
            case UNPAID -> "red";
            case DRAFT -> "gray";
            case PENDING -> "orange";
            case PARTIALLY_PAID -> "blue";
            case PAID -> "green";
            case OVERDUE -> "darkred";
            case CANCELLED -> "gray";
        };
    }

    /**
     * Vérifie si la facture est valide
     */
    public boolean isValid() {
        return invoiceNumber != null && !invoiceNumber.isEmpty()
                && amount != null && amount > 0
                && dueDate != null;
    }

    /**
     * Retourne le montant total avec les taxes
     */
    public double getTotalWithTax() {
        if (amount == null) {
            return 0.0;
        }
        double total = amount;
        if (tax != null) {
            total += amount * (tax / 100.0);
        }
        return total;
    }

    /**
     * Vérifie si la facture est échue depuis plus de X jours
     */
    public boolean isOverdueMoreThan(int days) {
        if (dueDate == null || isPaid()) {
            return false;
        }
        long daysOverdue = java.time.Duration.between(dueDate, LocalDateTime.now()).toDays();
        return daysOverdue > days;
    }

    /**
     * Vérifie si la facture peut être payée
     */
    public boolean isPayable() {
        return status == InvoiceStatus.UNPAID
                || status == InvoiceStatus.PARTIALLY_PAID
                || status == InvoiceStatus.OVERDUE
                || status == InvoiceStatus.PENDING;
    }
}