package com.pizzaco.order.application.port.out;

import com.pizzaco.order.domain.model.PizzaOrder;

/**
 * Outbound Port (Driven Port) — "OrderRepository" write side.
 *
 * <p>Defines WHAT the app needs: the ability to persist an order.
 */
public interface SaveOrderPort {

  PizzaOrder save(PizzaOrder order);
}
