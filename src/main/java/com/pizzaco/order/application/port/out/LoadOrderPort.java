package com.pizzaco.order.application.port.out;

import com.pizzaco.order.domain.model.OrderId;
import com.pizzaco.order.domain.model.PizzaOrder;
import java.util.Optional;

/**
 * Outbound Port (Driven Port) — "OrderRepository" read side.
 *
 * <p>Defines WHAT the app needs from the world: the ability to load an order. The persistence
 * adapter (Outbound Adapter) implements this.
 */
public interface LoadOrderPort {

  Optional<PizzaOrder> loadById(OrderId orderId);
}
