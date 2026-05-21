package com.pizzaco.order.core.application.usecase;

import com.pizzaco.order.core.domain.model.CustomerContact;
import com.pizzaco.order.core.domain.model.DeliveryAddress;
import com.pizzaco.order.core.domain.model.Money;
import com.pizzaco.order.core.domain.model.OrderId;
import com.pizzaco.order.core.domain.model.OrderStatus;
import com.pizzaco.order.core.domain.model.PizzaLineItem;
import java.util.List;

public interface PlaceOrderUseCase {
    PlaceOrderResult placeOrder(PlaceOrderCommand command);

    record PlaceOrderCommand(
            String customerId,
            CustomerContact customerContact,
            DeliveryAddress storeAddress,
            DeliveryAddress deliveryAddress,
            double deliveryDistanceMiles,
            List<PizzaLineItem> items) {}

    record PlaceOrderResult(
            OrderId orderId,
            OrderStatus status,
            Money total,
            int etaMinutes,
            String paymentReference) {}
}
