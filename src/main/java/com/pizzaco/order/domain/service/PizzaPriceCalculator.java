package com.pizzaco.order.domain.service;

import com.pizzaco.order.domain.model.Money;
import com.pizzaco.order.domain.model.Pizza;
import java.util.Comparator;
import java.util.List;

/**
 * Domain Service for calculating the total price of a list of pizzas, including cross-cutting
 * discount rules.
 *
 * <p>Decision Matrix: "Does it compare multiple Orders or Drivers? → Domain Service" Discount logic
 * spans across multiple pizzas — too complex for a single Pizza entity, but doesn't need a database
 * or external service. This is the "Head Chef" of pricing.
 *
 * <p>Blog: "telling the Domain to apply discounts" and "changing your discount logic for the
 * holiday season"
 *
 * <p>Pure domain logic — no framework annotations, no external dependencies. Swapping discount
 * strategies is a domain-only change — the Hexagonal shell is untouched.
 */
public class PizzaPriceCalculator {

  private final boolean holidayDiscountEnabled;

  public PizzaPriceCalculator(boolean holidayDiscountEnabled) {
    this.holidayDiscountEnabled = holidayDiscountEnabled;
  }

  /**
   * Calculates the combined price of all pizzas, applying applicable discounts.
   *
   * <p>Current rules:
   *
   * <ul>
   *   <li><b>Buy 3, cheapest free:</b> If 3+ pizza line items, the cheapest one is free.
   *   <li><b>Holiday 10% off:</b> When enabled, applies a 10% discount to the total.
   * </ul>
   */
  public Money calculateTotal(List<Pizza> pizzas) {
    Money rawTotal = pizzas.stream().map(Pizza::calculatePrice).reduce(Money.ZERO, Money::add);

    Money discount = calculateDiscount(pizzas);
    Money afterDiscount = rawTotal.subtract(discount);

    if (holidayDiscountEnabled) {
      Money holidayDiscount = afterDiscount.percentage(10);
      afterDiscount = afterDiscount.subtract(holidayDiscount);
    }

    return afterDiscount;
  }

  /** Calculates the "buy 3, cheapest free" discount. */
  public Money calculateDiscount(List<Pizza> pizzas) {
    if (pizzas.size() >= 3) {
      // Find the cheapest pizza line item and discount it
      return pizzas.stream()
          .map(Pizza::calculatePrice)
          .min(Comparator.comparing(Money::amount))
          .orElse(Money.ZERO);
    }
    return Money.ZERO;
  }
}
