package com.pizzaco.order.core.application.service;

import com.pizzaco.order.core.application.exception.OrderNotFoundException;
import com.pizzaco.order.core.application.usecase.GetOrderUseCase;
import com.pizzaco.order.core.domain.model.OrderId;
import com.pizzaco.order.core.domain.store.OrderStore;

public class GetOrderService implements GetOrderUseCase {
    private final OrderStore orderStore;

    public GetOrderService(OrderStore orderStore) {
        this.orderStore = orderStore;
    }

    @Override
    public OrderView getOrder(OrderId orderId) {
        var order =
                orderStore
                        .findById(orderId)
                        .orElseThrow(
                                () ->
                                        new OrderNotFoundException(
                                                "Order not found: " + orderId.value()));

        return new OrderView(
                order.getId(),
                order.getStatus(),
                order.getDeliveryAddress().value(),
                order.getEtaMinutes().value(),
                order.getTotal().value().toPlainString(),
                order.getPaymentReference(),
                order.getPlacedAt());
    }
}
