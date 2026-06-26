package com.edueasy.payment.model;

public enum PaymentStatus {
    PROCESSING,
    SUCCESS,
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    CANCELLED;

    private PaymentStatus() {
    }
}