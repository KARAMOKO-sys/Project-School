package com.edueasy.payment.model;

public enum InvoiceStatus {
    UNPAID,
    DRAFT,
    PENDING,
    PARTIALLY_PAID,
    PAID,
    OVERDUE,
    CANCELLED;

    private InvoiceStatus() {
    }
}