package com.pizzaco.order.domain.model;

/**
 * Value Object representing a pizza topping. Immutable — "Extra Cheese" doesn't need a unique ID;
 * if you swap one "Extra Cheese" for another, nothing changes.
 */
public record Topping(String name, Money surcharge) {

  public static final Topping EXTRA_CHEESE = new Topping("Extra Cheese", Money.of(1.50));
  public static final Topping PINEAPPLE = new Topping("Pineapple", Money.of(1.00));
  public static final Topping PEPPERONI = new Topping("Pepperoni", Money.of(2.00));
  public static final Topping MUSHROOM = new Topping("Mushroom", Money.of(1.25));
  public static final Topping OLIVES = new Topping("Olives", Money.of(1.00));
  public static final Topping BACON = new Topping("Bacon", Money.of(2.50));
  public static final Topping ONION = new Topping("Onion", Money.of(0.75));

  public Topping {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Topping name cannot be empty");
    }
    if (surcharge == null) {
      throw new IllegalArgumentException("Topping surcharge cannot be null");
    }
  }

  /** Factory for custom toppings not in the predefined list. */
  public static Topping custom(String name, Money surcharge) {
    return new Topping(name, surcharge);
  }

  @Override
  public String toString() {
    return name + " (+" + surcharge + ")";
  }
}
