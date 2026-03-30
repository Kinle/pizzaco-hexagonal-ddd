package com.pizzaco.order.domain.model;

import java.util.UUID;

/**
 * Value Object representing the unique identity of a PizzaOrder. Wraps a UUID to provide a typed
 * identity for the Aggregate Root.
 */
public record OrderId(UUID value) {

  public OrderId {
    if (value == null) {
      throw new IllegalArgumentException("OrderId value cannot be null");
    }
  }

  public static OrderId generate() {
    return new OrderId(UUID.randomUUID());
  }

  public static OrderId of(UUID value) {
    return new OrderId(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
