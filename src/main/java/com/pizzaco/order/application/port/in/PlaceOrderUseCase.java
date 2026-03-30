package com.pizzaco.order.application.port.in;

import com.pizzaco.order.application.port.in.command.PlaceOrderCommand;
import com.pizzaco.order.domain.model.PizzaOrder;

/**
 * Inbound Port (Driving Port) — "PlaceOrderUseCase"
 *
 * <p>Defines HOW the world interacts with our app to place an order. The REST controller (Inbound
 * Adapter) calls this; the Application Service implements it.
 */
public interface PlaceOrderUseCase {

  PizzaOrder placeOrder(PlaceOrderCommand command);
}
