package com.pizzaco.order.application.port.in;

import com.pizzaco.order.domain.model.OrderId;
import com.pizzaco.order.domain.model.PizzaOrder;

/** Inbound Port — Query use case for retrieving an order by its ID. */
public interface GetOrderUseCase {

  PizzaOrder getOrder(OrderId orderId);
}
