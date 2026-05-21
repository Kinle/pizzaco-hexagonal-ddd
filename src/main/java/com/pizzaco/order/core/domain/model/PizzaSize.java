package com.pizzaco.order.core.domain.model;

public record PizzaSize(String value) {
    public PizzaSize {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("PizzaSize cannot be blank");
    }
}
