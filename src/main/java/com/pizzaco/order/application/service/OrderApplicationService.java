package com.pizzaco.order.application.service;

import com.pizzaco.order.application.port.in.GetOrderUseCase;
import com.pizzaco.order.application.port.in.PlaceOrderUseCase;
import com.pizzaco.order.application.port.in.UpdateOrderStatusUseCase;
import com.pizzaco.order.application.port.in.command.PizzaItemCommand;
import com.pizzaco.order.application.port.in.command.PlaceOrderCommand;
import com.pizzaco.order.application.port.out.LoadOrderPort;
import com.pizzaco.order.application.port.out.PaymentPort;
import com.pizzaco.order.application.port.out.SaveOrderPort;
import com.pizzaco.order.domain.exception.DomainException;
import com.pizzaco.order.domain.exception.OrderNotFoundException;
import com.pizzaco.order.domain.model.*;
import com.pizzaco.order.domain.service.DeliveryFeeCalculator;
import com.pizzaco.order.domain.service.PizzaPriceCalculator;
import java.util.List;
import java.util.Map;

/**
 * Application Service — "The Kitchen Manager"
 *
 * <p>"It coordinates the flow—fetching data via Ports, telling the Domain to apply discounts, and
 * saving the result—but it doesn't 'cook' the pizza logic itself."
 *
 * <p>Decision Matrix: "Does it need to call a Database or Stripe? → Application Service" This
 * service orchestrates outbound ports (DB, Payment) and domain services, but delegates all business
 * rules to the Domain layer.
 *
 * <p>Implements all three inbound ports — the single entry point for the core application.
 */
public class OrderApplicationService
    implements PlaceOrderUseCase, GetOrderUseCase, UpdateOrderStatusUseCase {

  // Well-known toppings map for resolving topping names to Value Objects
  private static final Map<String, Topping> KNOWN_TOPPINGS =
      Map.of(
          "extra cheese", Topping.EXTRA_CHEESE,
          "pineapple", Topping.PINEAPPLE,
          "pepperoni", Topping.PEPPERONI,
          "mushroom", Topping.MUSHROOM,
          "olives", Topping.OLIVES,
          "bacon", Topping.BACON,
          "onion", Topping.ONION);
  private final LoadOrderPort loadOrderPort;
  private final SaveOrderPort saveOrderPort;
  private final PaymentPort paymentPort;
  private final DeliveryFeeCalculator deliveryFeeCalculator;
  private final PizzaPriceCalculator pizzaPriceCalculator;

  public OrderApplicationService(
      LoadOrderPort loadOrderPort,
      SaveOrderPort saveOrderPort,
      PaymentPort paymentPort,
      DeliveryFeeCalculator deliveryFeeCalculator,
      PizzaPriceCalculator pizzaPriceCalculator) {
    this.loadOrderPort = loadOrderPort;
    this.saveOrderPort = saveOrderPort;
    this.paymentPort = paymentPort;
    this.deliveryFeeCalculator = deliveryFeeCalculator;
    this.pizzaPriceCalculator = pizzaPriceCalculator;
  }

  /**
   * Places a new order: 1. Maps command → domain entities (triggers Hawaiian invariant inside
   * Pizza) 2. Builds PizzaOrder aggregate, adds all pizzas 3. Calls DeliveryFeeCalculator domain
   * service for delivery fee 4. Calls PizzaPriceCalculator domain service for total with discounts
   * 5. Calls PaymentPort to charge (the "needs Stripe" row of the Decision Matrix) 6. Saves via
   * SaveOrderPort
   */
  @Override
  public PizzaOrder placeOrder(PlaceOrderCommand command) {
    // 1. Build delivery address (Value Object)
    Address deliveryAddress =
        Address.of(
            command.street(),
            command.city(),
            command.zipCode(),
            command.latitude(),
            command.longitude());

    // 2. Create the Aggregate Root
    OrderId orderId = OrderId.generate();
    PizzaOrder order = PizzaOrder.create(orderId, command.customerName(), deliveryAddress);

    // 3. Map each pizza command → domain Pizza entity and add via Aggregate Root
    for (PizzaItemCommand pizzaCmd : command.pizzas()) {
      PizzaType type = PizzaType.valueOf(pizzaCmd.type().toUpperCase());
      List<Topping> toppings = resolveToppings(pizzaCmd.toppings());
      Pizza pizza = new Pizza(type, toppings, pizzaCmd.quantity());
      order.addPizza(pizza); // All mutations go through the Aggregate Root
    }

    // 4. Calculate delivery fee via Domain Service
    Money deliveryFee = deliveryFeeCalculator.calculate(deliveryAddress);
    order.applyDeliveryFee(deliveryFee);

    // 5. Calculate pizza total with discounts via Domain Service
    //    "telling the Domain to apply discounts" — the Application Service coordinates,
    //    but the PizzaPriceCalculator Domain Service owns the discount rules.
    Money pizzaTotal = pizzaPriceCalculator.calculateTotal(order.getPizzas());
    order.applyPizzaTotal(pizzaTotal);

    // 6. Charge payment via Outbound Port (Plug-and-Play: swap Stripe for PayPal here)
    boolean paymentSuccess = paymentPort.charge(orderId, order.getTotalPrice());
    if (!paymentSuccess) {
      throw new DomainException("Payment failed for order " + orderId);
    }

    // 7. Persist via Outbound Port
    return saveOrderPort.save(order);
  }

  @Override
  public PizzaOrder getOrder(OrderId orderId) {
    return loadOrderPort
        .loadById(orderId)
        .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
  }

  @Override
  public PizzaOrder advanceOrderStatus(OrderId orderId) {
    PizzaOrder order =
        loadOrderPort
            .loadById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

    // Business logic is in the domain — the Application Service just coordinates
    order.advanceStatus();

    return saveOrderPort.save(order);
  }

  /**
   * Resolves topping names to domain Topping Value Objects. Known toppings get their predefined
   * surcharges; unknown ones get a default surcharge.
   */
  private List<Topping> resolveToppings(List<String> toppingNames) {
    return toppingNames.stream()
        .map(
            name -> {
              Topping known = KNOWN_TOPPINGS.get(name.toLowerCase());
              return known != null ? known : Topping.custom(name, Money.of(1.50));
            })
        .toList();
  }
}
