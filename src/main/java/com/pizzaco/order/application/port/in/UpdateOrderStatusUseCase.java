package com.pizzaco.order.application.port.in;

import com.pizzaco.order.domain.model.OrderId;
import com.pizzaco.order.domain.model.PizzaOrder;

/**
 * Inbound Port — Use case for advancing an order's status through the lifecycle. PLACED → PREPARING
 * → BAKED → OUT_FOR_DELIVERY → DELIVERED
 */
public interface UpdateOrderStatusUseCase {

  PizzaOrder advanceOrderStatus(OrderId orderId);
}
