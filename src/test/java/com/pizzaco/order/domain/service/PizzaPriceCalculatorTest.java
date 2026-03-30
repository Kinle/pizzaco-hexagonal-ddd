package com.pizzaco.order.domain.service;

import static com.pizzaco.order.fixture.PizzaFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import com.pizzaco.order.domain.model.*;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PizzaPriceCalculator Domain Service.
 *
 * <p>Demonstrates: - "Buy 3, cheapest free" discount rule - Holiday 10% off toggle - "Changing your
 * discount logic for the holiday season" — just flip a boolean
 */
class PizzaPriceCalculatorTest {

  @Test
  @DisplayName("No discount for fewer than 3 pizzas")
  void noDiscountForFewerThan3Pizzas() {
    var calculator = new PizzaPriceCalculator(false);

    List<Pizza> pizzas = List.of(margherita(), pepperoni());

    Money total = calculator.calculateTotal(pizzas);
    assertEquals(Money.of(18.00), total);
  }

  @Test
  @DisplayName("Buy 3, cheapest free: cheapest pizza is discounted")
  void buy3CheapestFree() {
    var calculator = new PizzaPriceCalculator(false);

    List<Pizza> pizzas =
        List.of(
            margherita(), // $8.00 ← cheapest (free!)
            pepperoni(), // $10.00
            hawaiianWithPineapple() // $12.00
            );

    // Raw total = 8 + 10 + 12 = 30.00
    // Discount = 8.00 (cheapest)
    // After discount = 22.00
    Money total = calculator.calculateTotal(pizzas);
    assertEquals(Money.of(22.00), total);
  }

  @Test
  @DisplayName("Holiday discount: 10% off after buy-3 discount")
  void holidayDiscountApplied() {
    var calculator = new PizzaPriceCalculator(true); // Holiday season!

    List<Pizza> pizzas =
        List.of(
            margherita(), // $8.00
            pepperoni(), // $10.00
            hawaiianWithPineapple() // $12.00
            );

    // Raw total = 30.00
    // Buy 3 discount = 8.00 → after = 22.00
    // Holiday 10% off 22.00 = 2.20 → after = 19.80
    Money total = calculator.calculateTotal(pizzas);
    assertEquals(Money.of(19.80), total);
  }

  @Test
  @DisplayName("Holiday discount without buy-3 (fewer than 3 pizzas)")
  void holidayDiscountWithoutBuy3() {
    var calculator = new PizzaPriceCalculator(true);

    List<Pizza> pizzas = List.of(margherita());

    // Raw total = 8.00
    // No buy-3 discount
    // Holiday 10% off 8.00 = 0.80 → after = 7.20
    Money total = calculator.calculateTotal(pizzas);
    assertEquals(Money.of(7.20), total);
  }

  @Test
  @DisplayName("calculateDiscount returns zero for fewer than 3 pizzas")
  void discountZeroForFewerThan3() {
    var calculator = new PizzaPriceCalculator(false);
    List<Pizza> pizzas = List.of(margherita());
    assertEquals(Money.ZERO, calculator.calculateDiscount(pizzas));
  }
}
