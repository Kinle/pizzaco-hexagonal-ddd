package com.pizzaco.order.core.domain.model;

public record PaymentReference(String value) {
    public PaymentReference {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment reference is required");
        }
    }
}
