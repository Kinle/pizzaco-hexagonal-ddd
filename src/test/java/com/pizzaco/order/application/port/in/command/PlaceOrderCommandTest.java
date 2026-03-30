package com.pizzaco.order.application.port.in.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceOrderCommandTest {

  private final List<PizzaItemCommand> validPizzas =
      List.of(new PizzaItemCommand("MARGHERITA", List.of(), 1));

  @Test
  @DisplayName("Should create valid command")
  void shouldCreateValidCommand() {
    PlaceOrderCommand cmd =
        new PlaceOrderCommand("John", "1 Main St", "NYC", "10001", 40.71, -74.00, validPizzas);
    assertEquals("John", cmd.customerName());
  }

  @Test
  @DisplayName("Should reject null customer name")
  void shouldRejectNullCustomerName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlaceOrderCommand(null, "1 Main St", "NYC", "10001", 40.71, -74.00, validPizzas));
  }

  @Test
  @DisplayName("Should reject blank customer name")
  void shouldRejectBlankCustomerName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlaceOrderCommand("  ", "1 Main St", "NYC", "10001", 40.71, -74.00, validPizzas));
  }

  @Test
  @DisplayName("Should reject null street")
  void shouldRejectNullStreet() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlaceOrderCommand("John", null, "NYC", "10001", 40.71, -74.00, validPizzas));
  }

  @Test
  @DisplayName("Should reject blank street")
  void shouldRejectBlankStreet() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlaceOrderCommand("John", "  ", "NYC", "10001", 40.71, -74.00, validPizzas));
  }

  @Test
  @DisplayName("Should reject null city")
  void shouldRejectNullCity() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new PlaceOrderCommand("John", "1 Main St", null, "10001", 40.71, -74.00, validPizzas));
  }

  @Test
  @DisplayName("Should reject blank city")
  void shouldRejectBlankCity() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new PlaceOrderCommand("John", "1 Main St", "  ", "10001", 40.71, -74.00, validPizzas));
  }

  @Test
  @DisplayName("Should reject null zip code")
  void shouldRejectNullZipCode() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlaceOrderCommand("John", "1 Main St", "NYC", null, 40.71, -74.00, validPizzas));
  }

  @Test
  @DisplayName("Should reject blank zip code")
  void shouldRejectBlankZipCode() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlaceOrderCommand("John", "1 Main St", "NYC", "  ", 40.71, -74.00, validPizzas));
  }

  @Test
  @DisplayName("Should reject null pizzas list")
  void shouldRejectNullPizzas() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlaceOrderCommand("John", "1 Main St", "NYC", "10001", 40.71, -74.00, null));
  }

  @Test
  @DisplayName("Should reject empty pizzas list")
  void shouldRejectEmptyPizzas() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PlaceOrderCommand("John", "1 Main St", "NYC", "10001", 40.71, -74.00, List.of()));
  }
}
