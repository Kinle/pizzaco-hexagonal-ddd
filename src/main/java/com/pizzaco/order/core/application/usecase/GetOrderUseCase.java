package com.pizzaco.order.core.application.usecase;

import com.pizzaco.order.core.domain.model.OrderId;
import com.pizzaco.order.core.domain.model.OrderStatus;
import com.pizzaco.order.core.domain.model.PaymentReference;
import java.time.Instant;

public interface GetOrderUseCase {
    OrderView getOrder(OrderId orderId);

    record OrderView(
            OrderId orderId,
            OrderStatus status,
            String deliveryAddress,
            int etaMinutes,
            String total,
            PaymentReference paymentReference,
            Instant placedAt) {}
}
