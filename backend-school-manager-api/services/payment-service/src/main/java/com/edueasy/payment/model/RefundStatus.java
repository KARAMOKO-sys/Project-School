package com.edueasy.payment.model;

public enum RefundStatus {
    CANCELLED,
    FAILED,
    PROCESSING,
    PENDING,
    APPROVED,
    REJECTED,
    COMPLETED;

    private RefundStatus() {
    }
}
