package com.pizzaco.order.fixture;

import com.pizzaco.order.application.port.in.command.PizzaItemCommand;
import com.pizzaco.order.application.port.in.command.PlaceOrderCommand;
import java.util.List;

/**
 * Reusable command factory methods for Application Service tests. Each method returns a fresh
 * instance so tests remain independent.
 */
public final class CommandFixture {

  private static final String DEFAULT_CUSTOMER = "Jane Doe";

  private CommandFixture() {}

  /** A simple PlaceOrderCommand with one plain Margherita, delivered to the customer address. */
  public static PlaceOrderCommand simpleMargheritaCommand() {
    return placeOrderCommand(List.of(margheritaItem()));
  }

  /** PlaceOrderCommand with the given pizza items, delivered to the default customer address. */
  public static PlaceOrderCommand placeOrderCommand(List<PizzaItemCommand> pizzas) {
    return placeOrderCommand(DEFAULT_CUSTOMER, pizzas);
  }

  /** PlaceOrderCommand with a custom name and the given pizza items. */
  public static PlaceOrderCommand placeOrderCommand(
      String customerName, List<PizzaItemCommand> pizzas) {
    return new PlaceOrderCommand(
        customerName,
        AddressFixture.CUSTOMER.street(),
        AddressFixture.CUSTOMER.city(),
        AddressFixture.CUSTOMER.zipCode(),
        AddressFixture.CUSTOMER.latitude(),
        AddressFixture.CUSTOMER.longitude(),
        pizzas);
  }

  /** PizzaItemCommand for a Margherita with Extra Cheese, qty 2. */
  public static PizzaItemCommand margheritaWithExtraCheeseItem() {
    return new PizzaItemCommand("MARGHERITA", List.of("Extra Cheese"), 2);
  }

  /** PizzaItemCommand for a plain Margherita, qty 1. */
  public static PizzaItemCommand margheritaItem() {
    return new PizzaItemCommand("MARGHERITA", List.of(), 1);
  }

  /** PizzaItemCommand for a Hawaiian with Pineapple + Bacon, qty 1. */
  public static PizzaItemCommand hawaiianItem() {
    return new PizzaItemCommand("HAWAIIAN", List.of("Pineapple", "Bacon"), 1);
  }

  /** PizzaItemCommand for a Hawaiian with Extra Cheese (no Pineapple — invalid!). */
  public static PizzaItemCommand hawaiianWithoutPineappleItem() {
    return new PizzaItemCommand("HAWAIIAN", List.of("Extra Cheese"), 1);
  }
}
