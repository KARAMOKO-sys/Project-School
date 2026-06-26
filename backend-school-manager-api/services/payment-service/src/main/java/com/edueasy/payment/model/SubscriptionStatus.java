package com.edueasy.payment.model;

public enum SubscriptionStatus {
    PAUSED,
    ACTIVE,
    CANCELLED,
    EXPIRED,
    PENDING;

    private SubscriptionStatus() {
    }
}