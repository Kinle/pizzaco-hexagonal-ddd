package com.pizzaco.order.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Value Object / Enum representing the type of pizza. Each type has a base price used by the
 * pricing logic.
 */
@Getter
@RequiredArgsConstructor
public enum PizzaType {
  MARGHERITA(Money.of(8.00)),
  PEPPERONI(Money.of(10.00)),
  HAWAIIAN(Money.of(11.00)),
  VEGGIE(Money.of(9.50)),
  CUSTOM(Money.of(7.00));

  private final Money basePrice;
}
