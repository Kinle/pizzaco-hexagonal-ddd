package com.pizzaco.order.fixture;

import com.pizzaco.order.domain.model.Address;
import com.pizzaco.order.domain.model.OrderId;
import com.pizzaco.order.domain.model.PizzaOrder;

/**
 * Reusable PizzaOrder (Aggregate Root) factory methods for tests. Each method returns a fresh
 * instance so tests remain independent.
 */
public final class OrderFixture {

  private OrderFixture() {}

  /** Creates an empty order with "John Doe" and {@link AddressFixture#NYC_DEFAULT}. */
  public static PizzaOrder createOrder() {
    return PizzaOrder.create(OrderId.generate(), "John Doe", AddressFixture.NYC_DEFAULT);
  }

  /** Creates an empty order with the given customer name and address. */
  public static PizzaOrder createOrder(String customerName, Address address) {
    return PizzaOrder.create(OrderId.generate(), customerName, address);
  }

  /** Creates an empty order with the given OrderId, customer name, and address. */
  public static PizzaOrder createOrder(OrderId orderId, String customerName, Address address) {
    return PizzaOrder.create(orderId, customerName, address);
  }

  /** Creates an order with one plain Margherita already added. */
  public static PizzaOrder createOrderWithMargherita() {
    PizzaOrder order = createOrder();
    order.addPizza(PizzaFixture.margherita());
    return order;
  }
}
