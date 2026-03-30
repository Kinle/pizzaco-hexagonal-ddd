package com.pizzaco.order.application.port.in.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PizzaItemCommandTest {

  @Test
  @DisplayName("Should create valid pizza item command")
  void shouldCreateValidCommand() {
    PizzaItemCommand cmd = new PizzaItemCommand("MARGHERITA", List.of("Extra Cheese"), 2);
    assertEquals("MARGHERITA", cmd.type());
    assertEquals(2, cmd.quantity());
    assertEquals(List.of("Extra Cheese"), cmd.toppings());
  }

  @Test
  @DisplayName("Should reject null type")
  void shouldRejectNullType() {
    assertThrows(IllegalArgumentException.class, () -> new PizzaItemCommand(null, List.of(), 1));
  }

  @Test
  @DisplayName("Should reject blank type")
  void shouldRejectBlankType() {
    assertThrows(IllegalArgumentException.class, () -> new PizzaItemCommand("  ", List.of(), 1));
  }

  @Test
  @DisplayName("Should reject quantity less than 1")
  void shouldRejectZeroQuantity() {
    assertThrows(
        IllegalArgumentException.class, () -> new PizzaItemCommand("MARGHERITA", List.of(), 0));
  }

  @Test
  @DisplayName("Null toppings should default to empty list")
  void nullToppingsShouldDefaultToEmptyList() {
    PizzaItemCommand cmd = new PizzaItemCommand("MARGHERITA", null, 1);
    assertNotNull(cmd.toppings());
    assertTrue(cmd.toppings().isEmpty());
  }
}
