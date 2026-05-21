package com.pizzaco.order.core.domain.model;

import java.util.Objects;

public record Topping(String name, Money extraCost) {
    public Topping {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Topping name is required");
        }
        Objects.requireNonNull(extraCost, "Topping extra cost is required");
    }
}
