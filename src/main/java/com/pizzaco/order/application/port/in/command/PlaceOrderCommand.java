package com.pizzaco.order.application.port.in.command;

import java.util.List;

/**
 * Command object for placing a new order. Part of the Inbound Port contract — the data the outside
 * world must provide. Compact constructor enforces required fields — fail fast before reaching the
 * domain.
 */
public record PlaceOrderCommand(
    String customerName,
    String street,
    String city,
    String zipCode,
    double latitude,
    double longitude,
    List<PizzaItemCommand> pizzas) {
  public PlaceOrderCommand {
    if (customerName == null || customerName.isBlank()) {
      throw new IllegalArgumentException("Customer name is required");
    }
    if (street == null || street.isBlank()) {
      throw new IllegalArgumentException("Street is required");
    }
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("City is required");
    }
    if (zipCode == null || zipCode.isBlank()) {
      throw new IllegalArgumentException("Zip code is required");
    }
    if (pizzas == null || pizzas.isEmpty()) {
      throw new IllegalArgumentException("At least one pizza is required");
    }
  }
}
