package com.pizzaco.order.domain.model;

import com.pizzaco.order.domain.exception.InvalidPizzaException;
import java.util.Collections;
import java.util.List;

/**
 * Entity representing a single pizza within an order.
 *
 * <p>Business Invariant: A HAWAIIAN pizza must always include pineapple. "Your core business
 * rules—like the fact that a Hawaiian pizza must always include pineapple—"
 *
 * <p>Decision Matrix: "Does it only need its own toppings and size? → Domain Entity (The Recipe)"
 * Price calculation lives here because it only needs the pizza's own type and toppings.
 */
public record Pizza(PizzaType type, List<Topping> toppings, int quantity) {

  /**
   * Creates a pizza, enforcing the Hawaiian invariant.
   *
   * @throws InvalidPizzaException if HAWAIIAN type does not include pineapple
   */
  public Pizza(PizzaType type, List<Topping> toppings, int quantity) {
    if (type == null) {
      throw new InvalidPizzaException("Pizza type cannot be null");
    }
    if (toppings == null) {
      throw new InvalidPizzaException("Toppings list cannot be null");
    }
    if (quantity < 1) {
      throw new InvalidPizzaException("Pizza quantity must be at least 1");
    }

    // Business Invariant: Hawaiian pizza must always include pineapple
    if (type == PizzaType.HAWAIIAN) {
      boolean hasPineapple =
          toppings.stream().anyMatch(t -> "Pineapple".equalsIgnoreCase(t.name()));
      if (!hasPineapple) {
        throw new InvalidPizzaException("A Hawaiian pizza must always include Pineapple topping");
      }
    }

    this.type = type;
    this.toppings = Collections.unmodifiableList(toppings);
    this.quantity = quantity;
  }

  /**
   * Calculates the price of this pizza line item. Price = (base price + sum of topping surcharges)
   * × quantity
   *
   * <p>This logic lives in the Entity because it "only needs its own toppings and size".
   */
  public Money calculatePrice() {
    Money toppingSurcharge =
        toppings.stream().map(Topping::surcharge).reduce(Money.ZERO, Money::add);

    Money unitPrice = type.getBasePrice().add(toppingSurcharge);
    return unitPrice.multiply(quantity);
  }

  @Override
  public String toString() {
    return quantity + "x " + type + " with " + toppings;
  }
}
