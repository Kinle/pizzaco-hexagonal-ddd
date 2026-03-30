package com.pizzaco.order.domain.model;

import static com.pizzaco.order.fixture.PizzaFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import com.pizzaco.order.domain.exception.InvalidPizzaException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Pizza Entity. Demonstrates the Hawaiian invariant: "A Hawaiian pizza must
 * always include pineapple."
 */
class PizzaTest {

  @Test
  @DisplayName("Hawaiian pizza with pineapple should be valid")
  void hawaiianWithPineappleShouldBeValid() {
    Pizza pizza = hawaiianWithPineappleAndBacon();
    assertNotNull(pizza);
    assertEquals(PizzaType.HAWAIIAN, pizza.type());
  }

  @Test
  @DisplayName("Hawaiian pizza WITHOUT pineapple should throw InvalidPizzaException")
  void hawaiianWithoutPineappleShouldThrow() {
    assertThrows(
        InvalidPizzaException.class,
        () -> new Pizza(PizzaType.HAWAIIAN, List.of(Topping.EXTRA_CHEESE), 1));
  }

  @Test
  @DisplayName("Non-Hawaiian pizza without pineapple should be fine")
  void nonHawaiianWithoutPineappleShouldBeValid() {
    Pizza pizza = new Pizza(PizzaType.PEPPERONI, List.of(Topping.EXTRA_CHEESE), 2);
    assertNotNull(pizza);
    assertEquals(2, pizza.quantity());
  }

  @Test
  @DisplayName("Should calculate price correctly: base + toppings × quantity")
  void shouldCalculatePrice() {
    // MARGHERITA base: $8.00, Extra Cheese: +$1.50, Mushroom: +$1.25
    // Unit price = 8.00 + 1.50 + 1.25 = 10.75
    // Quantity 2 → total = 21.50
    Pizza pizza = margheritaWithExtraCheeseAndMushroom();
    assertEquals(Money.of(21.50), pizza.calculatePrice());
  }

  @Test
  @DisplayName("Should reject null type")
  void shouldRejectNullType() {
    assertThrows(InvalidPizzaException.class, () -> new Pizza(null, List.of(), 1));
  }

  @Test
  @DisplayName("Should reject quantity less than 1")
  void shouldRejectZeroQuantity() {
    assertThrows(InvalidPizzaException.class, () -> new Pizza(PizzaType.MARGHERITA, List.of(), 0));
  }

  @Test
  @DisplayName("Should reject null toppings list")
  void shouldRejectNullToppings() {
    assertThrows(InvalidPizzaException.class, () -> new Pizza(PizzaType.MARGHERITA, null, 1));
  }

  @Test
  @DisplayName("toString should contain type and quantity")
  void toStringShouldContainTypeAndQuantity() {
    Pizza pizza = new Pizza(PizzaType.PEPPERONI, List.of(Topping.PEPPERONI), 2);
    String str = pizza.toString();
    assertTrue(str.contains("2x"));
    assertTrue(str.contains("PEPPERONI"));
  }
}
