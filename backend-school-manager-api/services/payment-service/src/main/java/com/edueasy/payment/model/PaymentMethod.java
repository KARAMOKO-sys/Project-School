package com.edueasy.payment.model;

public enum PaymentMethod {
    WALLET,
    CARD,
    BANK_TRANSFER,
    CASH,
    MOBILE_MONEY,
    CHECK,
    ONLINE;

    private PaymentMethod() {
    }
}