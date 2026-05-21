package com.pizzaco.order.core.domain.model;

import java.util.List;
import java.util.Objects;

public record PizzaLineItem(
        PizzaSize size,
        CrustType crustType,
        List<Topping> toppings,
        Money priceAtPurchase,
        int quantity) {
    public PizzaLineItem {
        Objects.requireNonNull(size, "Pizza size is required");
        Objects.requireNonNull(crustType, "Crust type is required");
        Objects.requireNonNull(toppings, "Toppings list is required");
        Objects.requireNonNull(priceAtPurchase, "Price at purchase is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        toppings = List.copyOf(toppings);
    }

    public Money lineTotal() {
        return priceAtPurchase.multiply(quantity);
    }
}
