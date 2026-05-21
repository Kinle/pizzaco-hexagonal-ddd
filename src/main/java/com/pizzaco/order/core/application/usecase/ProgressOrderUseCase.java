package com.pizzaco.order.core.application.usecase;

import com.pizzaco.order.core.domain.model.OrderId;
import com.pizzaco.order.core.domain.model.OrderStatus;

public interface ProgressOrderUseCase {
    ProgressOrderResult progressOrder(ProgressOrderCommand command);

    record ProgressOrderCommand(OrderId orderId, OrderStatus targetStatus) {}

    record ProgressOrderResult(OrderId orderId, OrderStatus status) {}
}
