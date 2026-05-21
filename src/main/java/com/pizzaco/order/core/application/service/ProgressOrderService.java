package com.pizzaco.order.core.application.service;

import com.pizzaco.order.core.application.exception.OrderNotFoundException;
import com.pizzaco.order.core.application.port.NotificationPort;
import com.pizzaco.order.core.application.usecase.ProgressOrderUseCase;
import com.pizzaco.order.core.domain.model.OrderStatus;
import com.pizzaco.order.core.domain.store.OrderStore;

public class ProgressOrderService implements ProgressOrderUseCase {
    private final OrderStore orderStore;
    private final NotificationPort notificationPort;

    public ProgressOrderService(OrderStore orderStore, NotificationPort notificationPort) {
        this.orderStore = orderStore;
        this.notificationPort = notificationPort;
    }

    @Override
    public ProgressOrderResult progressOrder(ProgressOrderCommand command) {
        var current =
                orderStore
                        .findById(command.orderId())
                        .orElseThrow(
                                () ->
                                        new OrderNotFoundException(
                                                "Order not found: " + command.orderId().value()));

        var progressed = transition(current, command.targetStatus());
        orderStore.save(progressed);

        if (progressed.getStatus() == OrderStatus.OutForDelivery) {
            notificationPort.notifyCustomer(
                    progressed.getCustomerContact(), "Order is out for delivery.");
        } else if (progressed.getStatus() == OrderStatus.Delivered) {
            notificationPort.notifyCustomer(
                    progressed.getCustomerContact(), "Order delivered. Enjoy your pizza!");
        }

        return new ProgressOrderResult(progressed.getId(), progressed.getStatus());
    }

    private static com.pizzaco.order.core.domain.model.PizzaOrder transition(
            com.pizzaco.order.core.domain.model.PizzaOrder order, OrderStatus target) {
        return switch (target) {
            case Baking -> order.startBaking();
            case ReadyForDelivery -> order.markReadyForDelivery();
            case OutForDelivery -> order.markOutForDelivery();
            case Delivered -> order.markDelivered();
            case Cancelled -> order.cancel();
            default ->
                    throw new IllegalArgumentException("Unsupported progression target: " + target);
        };
    }
}
