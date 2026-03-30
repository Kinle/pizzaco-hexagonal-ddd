package com.pizzaco.order.fixture;

import com.pizzaco.order.domain.model.Pizza;
import com.pizzaco.order.domain.model.PizzaType;
import com.pizzaco.order.domain.model.Topping;
import java.util.List;

/**
 * Reusable Pizza factory methods for tests. Each method returns a fresh instance so tests remain
 * independent.
 */
public final class PizzaFixture {

  private PizzaFixture() {}

  /** Plain Margherita, qty 1 — $8.00. */
  public static Pizza margherita() {
    return new Pizza(PizzaType.MARGHERITA, List.of(), 1);
  }

  /** Margherita with Extra Cheese, qty 1 — $9.50. */
  public static Pizza margheritaWithExtraCheese() {
    return new Pizza(PizzaType.MARGHERITA, List.of(Topping.EXTRA_CHEESE), 1);
  }

  /** Margherita with Extra Cheese + Mushroom, qty 2 — $21.50. */
  public static Pizza margheritaWithExtraCheeseAndMushroom() {
    return new Pizza(PizzaType.MARGHERITA, List.of(Topping.EXTRA_CHEESE, Topping.MUSHROOM), 2);
  }

  /** Plain Pepperoni, qty 1 — $10.00. */
  public static Pizza pepperoni() {
    return new Pizza(PizzaType.PEPPERONI, List.of(), 1);
  }

  /** Pepperoni with Pepperoni topping, qty 1 — $12.00. */
  public static Pizza pepperoniWithPepperoni() {
    return new Pizza(PizzaType.PEPPERONI, List.of(Topping.PEPPERONI), 1);
  }

  /** Hawaiian with Pineapple, qty 1 — $12.00. */
  public static Pizza hawaiianWithPineapple() {
    return new Pizza(PizzaType.HAWAIIAN, List.of(Topping.PINEAPPLE), 1);
  }

  /** Hawaiian with Pineapple + Bacon, qty 1 — $14.50. */
  public static Pizza hawaiianWithPineappleAndBacon() {
    return new Pizza(PizzaType.HAWAIIAN, List.of(Topping.PINEAPPLE, Topping.BACON), 1);
  }
}
