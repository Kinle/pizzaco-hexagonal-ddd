package com.pizzaco.order.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ToppingTest {

  @Test
  @DisplayName("Should create topping with valid name and surcharge")
  void shouldCreateValidTopping() {
    Topping topping = new Topping("Jalapeño", Money.of(1.75));
    assertEquals("Jalapeño", topping.name());
    assertEquals(Money.of(1.75), topping.surcharge());
  }

  @Test
  @DisplayName("Should reject null topping name")
  void shouldRejectNullName() {
    assertThrows(IllegalArgumentException.class, () -> new Topping(null, Money.of(1.00)));
  }

  @Test
  @DisplayName("Should reject blank topping name")
  void shouldRejectBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new Topping("  ", Money.of(1.00)));
  }

  @Test
  @DisplayName("Should reject null surcharge")
  void shouldRejectNullSurcharge() {
    assertThrows(IllegalArgumentException.class, () -> new Topping("Cheese", null));
  }

  @Test
  @DisplayName("Custom factory should create topping")
  void customFactoryShouldWork() {
    Topping custom = Topping.custom("Truffle Oil", Money.of(5.00));
    assertEquals("Truffle Oil", custom.name());
    assertEquals(Money.of(5.00), custom.surcharge());
  }

  @Test
  @DisplayName("toString should format name and surcharge")
  void toStringShouldFormat() {
    Topping topping = Topping.EXTRA_CHEESE;
    assertEquals("Extra Cheese (+$1.50)", topping.toString());
  }

  @Test
  @DisplayName("Predefined toppings should have correct values")
  void predefinedToppingsShouldBeCorrect() {
    assertEquals(Money.of(1.50), Topping.EXTRA_CHEESE.surcharge());
    assertEquals(Money.of(1.00), Topping.PINEAPPLE.surcharge());
    assertEquals(Money.of(2.00), Topping.PEPPERONI.surcharge());
    assertEquals(Money.of(1.25), Topping.MUSHROOM.surcharge());
    assertEquals(Money.of(1.00), Topping.OLIVES.surcharge());
    assertEquals(Money.of(2.50), Topping.BACON.surcharge());
    assertEquals(Money.of(0.75), Topping.ONION.surcharge());
  }
}
