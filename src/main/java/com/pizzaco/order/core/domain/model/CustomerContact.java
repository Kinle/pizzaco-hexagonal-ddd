package com.pizzaco.order.core.domain.model;

public record CustomerContact(String value) {
    public CustomerContact {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Customer contact is required");
        }
    }
}
