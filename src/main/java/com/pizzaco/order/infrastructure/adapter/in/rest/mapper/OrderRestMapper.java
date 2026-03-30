package com.pizzaco.order.infrastructure.adapter.in.rest.mapper;

import com.pizzaco.order.application.port.in.command.PizzaItemCommand;
import com.pizzaco.order.application.port.in.command.PlaceOrderCommand;
import com.pizzaco.order.domain.model.Pizza;
import com.pizzaco.order.domain.model.PizzaOrder;
import com.pizzaco.order.domain.model.Topping;
import com.pizzaco.order.infrastructure.adapter.in.rest.dto.*;
import org.springframework.stereotype.Component;

/**
 * Maps between REST DTOs and Application-layer commands / domain objects.
 *
 * <p>"Inbound Adapter: Converts a web request into a command the Core understands"
 */
@Component
public class OrderRestMapper {

  /** REST request → Application command */
  public PlaceOrderCommand toCommand(PlaceOrderRequest request) {
    var pizzaCommands =
        request.pizzas().stream()
            .map(p -> new PizzaItemCommand(p.type(), p.toppings(), p.quantity()))
            .toList();

    return new PlaceOrderCommand(
        request.customerName(),
        request.street(),
        request.city(),
        request.zipCode(),
        request.latitude(),
        request.longitude(),
        pizzaCommands);
  }

  /** Domain PizzaOrder → REST response */
  public OrderResponse toResponse(PizzaOrder order) {
    var pizzaResponses = order.getPizzas().stream().map(this::toPizzaResponse).toList();

    return new OrderResponse(
        order.getId().value().toString(),
        order.getCustomerName(),
        order.getStatus().name(),
        order.getDeliveryAddress().toString(),
        pizzaResponses,
        order.getDeliveryFee().amount(),
        order.getTotalPrice().amount(),
        order.getCreatedAt());
  }

  private PizzaResponse toPizzaResponse(Pizza pizza) {
    var toppingNames = pizza.toppings().stream().map(Topping::name).toList();

    return new PizzaResponse(
        pizza.type().name(), toppingNames, pizza.quantity(), pizza.calculatePrice().amount());
  }
}
