package com.pizzaco.order.domain.model;

/**
 * Enum representing the lifecycle of a PizzaOrder. Valid transitions: PLACED → PREPARING → BAKED →
 * OUT_FOR_DELIVERY → DELIVERED
 *
 * <p>Business Invariant: "An order cannot be Out for Delivery if it hasn't been Baked."
 */
public enum OrderStatus {
  PLACED,
  PREPARING,
  BAKED,
  OUT_FOR_DELIVERY,
  DELIVERED;

  /** Returns the next status in the lifecycle, or null if the order is already DELIVERED. */
  public OrderStatus next() {
    return switch (this) {
      case PLACED -> PREPARING;
      case PREPARING -> BAKED;
      case BAKED -> OUT_FOR_DELIVERY;
      case OUT_FOR_DELIVERY -> DELIVERED;
      case DELIVERED -> null;
    };
  }
}
