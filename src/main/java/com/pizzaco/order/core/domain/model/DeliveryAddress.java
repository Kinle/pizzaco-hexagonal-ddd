package com.pizzaco.order.core.domain.model;

public record DeliveryAddress(String value) {
    public DeliveryAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Delivery address is required");
        }
    }
}
