package com.pizzaco.order.application.port.in.command;

import java.util.List;

/**
 * Command representing a single pizza within a PlaceOrderCommand. Compact constructor validates
 * required fields.
 */
public record PizzaItemCommand(String type, List<String> toppings, int quantity) {
  public PizzaItemCommand {
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException("Pizza type is required");
    }
    if (toppings == null) {
      toppings = List.of();
    }
    if (quantity < 1) {
      throw new IllegalArgumentException("Pizza quantity must be at least 1");
    }
  }
}
