package com.pizzaco.order.domain.model;

import com.pizzaco.order.domain.exception.InvalidOrderStateException;
import com.pizzaco.order.domain.exception.InvalidPizzaException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Entity and Aggregate Root for a pizza order.
 *
 * <p>"In DDD, the Order acts as the Aggregate Root. It is the gateway to all internal components
 * (like individual pizzas or line items). Any change to the state of the order must go through the
 * Aggregate Root to ensure business invariants—such as 'Total price cannot be negative'—are always
 * enforced."
 *
 * <p>All mutations (adding/removing pizzas, advancing status) go through this root.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(of = {"id", "status", "totalPrice"})
public class PizzaOrder {

  @EqualsAndHashCode.Include private final OrderId id;
  private final List<Pizza> pizzas;
  private final Address deliveryAddress;
  private final String customerName;
  private final LocalDateTime createdAt;
  private OrderStatus status;
  private Money deliveryFee;
  private Money totalPrice;

  /** Private constructor for new orders — use the static factory method {@link #create}. */
  private PizzaOrder(OrderId id, String customerName, Address deliveryAddress) {
    this(id, customerName, deliveryAddress, LocalDateTime.now());
  }

  /** Private constructor with explicit createdAt — used by {@link #reconstitute}. */
  private PizzaOrder(
      OrderId id, String customerName, Address deliveryAddress, LocalDateTime createdAt) {
    this.id = id;
    this.customerName = customerName;
    this.deliveryAddress = deliveryAddress;
    this.pizzas = new ArrayList<>();
    this.status = OrderStatus.PLACED;
    this.createdAt = createdAt;
    this.deliveryFee = Money.ZERO;
    this.totalPrice = Money.ZERO;
  }

  /** Factory method for creating a new order. */
  public static PizzaOrder create(OrderId id, String customerName, Address deliveryAddress) {
    if (customerName == null || customerName.isBlank()) {
      throw new IllegalArgumentException("Customer name cannot be empty");
    }
    if (deliveryAddress == null) {
      throw new IllegalArgumentException("Delivery address cannot be null");
    }
    return new PizzaOrder(id, customerName, deliveryAddress);
  }

  /** Reconstitute an existing order from persistence (no validation, no side effects). */
  public static PizzaOrder reconstitute(
      OrderId id,
      String customerName,
      Address deliveryAddress,
      List<Pizza> pizzas,
      OrderStatus status,
      Money deliveryFee,
      Money totalPrice,
      LocalDateTime createdAt) {
    PizzaOrder order = new PizzaOrder(id, customerName, deliveryAddress, createdAt);
    order.pizzas.addAll(pizzas);
    order.status = status;
    order.deliveryFee = deliveryFee;
    order.totalPrice = totalPrice;
    return order;
  }

  // ── Aggregate Root operations ──────────────────────────────────────

  /** Adds a pizza to this order. All pizza mutations go through the Aggregate Root. */
  public void addPizza(Pizza pizza) {
    if (status != OrderStatus.PLACED) {
      throw new InvalidOrderStateException(
          "Cannot modify pizzas after order has moved past PLACED status");
    }
    if (pizza == null) {
      throw new InvalidPizzaException("Pizza cannot be null");
    }
    this.pizzas.add(pizza);
  }

  /** Removes a pizza by index from this order. */
  public void removePizza(int index) {
    if (status != OrderStatus.PLACED) {
      throw new InvalidOrderStateException(
          "Cannot modify pizzas after order has moved past PLACED status");
    }
    if (index < 0 || index >= pizzas.size()) {
      throw new IllegalArgumentException("Invalid pizza index: " + index);
    }
    this.pizzas.remove(index);
  }

  /** Sets the delivery fee (calculated externally by DeliveryFeeCalculator domain service). */
  public void applyDeliveryFee(Money deliveryFee) {
    if (deliveryFee == null) {
      throw new IllegalArgumentException("Delivery fee cannot be null");
    }
    this.deliveryFee = deliveryFee;
  }

  /**
   * Sets the pizza total (calculated externally by PizzaPriceCalculator domain service, which may
   * include discounts like "buy 3, cheapest free" or holiday discounts). The final order total =
   * pizza total + delivery fee.
   */
  public void applyPizzaTotal(Money pizzaTotal) {
    if (pizzaTotal == null) {
      throw new IllegalArgumentException("Pizza total cannot be null");
    }
    this.totalPrice = pizzaTotal.add(deliveryFee);
  }

  /**
   * Calculates and updates the total price: sum of all pizza prices + delivery fee. Business
   * Invariant: "Total price cannot be negative" — guaranteed by Money value object.
   */
  public Money recalculateTotal() {
    Money pizzaTotal = pizzas.stream().map(Pizza::calculatePrice).reduce(Money.ZERO, Money::add);

    this.totalPrice = pizzaTotal.add(deliveryFee);
    return this.totalPrice;
  }

  /**
   * Advances the order to the next status in the lifecycle.
   *
   * <p>Business Invariant: "An order cannot be 'Out for Delivery' if it hasn't been 'Baked'." This
   * is enforced by the sequential state machine in {@link OrderStatus#next()}.
   *
   * @throws InvalidOrderStateException if the transition is not valid
   */
  public void advanceStatus() {
    OrderStatus next = status.next();
    if (next == null) {
      throw new InvalidOrderStateException(
          "Order is already DELIVERED — no further transitions allowed");
    }
    if (status == OrderStatus.PLACED && pizzas.isEmpty()) {
      throw new InvalidOrderStateException("Cannot advance an order with no pizzas");
    }
    this.status = next;
  }

  /** Returns an unmodifiable view of the pizzas in this order. */
  public List<Pizza> getPizzas() {
    return Collections.unmodifiableList(pizzas);
  }
}
