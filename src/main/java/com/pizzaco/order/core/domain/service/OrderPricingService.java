package com.pizzaco.order.core.domain.service;

import com.pizzaco.order.core.domain.model.Money;
import com.pizzaco.order.core.domain.model.PizzaLineItem;
import com.pizzaco.order.core.domain.model.PizzaOrder;
import java.time.Instant;
import java.util.List;

public class OrderPricingService {
    private final DeliveryFeeCalculator deliveryFeeCalculator;

    public OrderPricingService(DeliveryFeeCalculator deliveryFeeCalculator) {
        this.deliveryFeeCalculator = deliveryFeeCalculator;
    }

    public Money calculateSubtotal(List<PizzaLineItem> items) {
        Money subtotal = Money.ZERO;
        for (PizzaLineItem item : items) {
            subtotal = subtotal.add(item.lineTotal());
        }
        return subtotal;
    }

    public Money calculateDeliveryFee(PizzaOrder orderDraft, double distanceInMiles) {
        return deliveryFeeCalculator.calculateFee(orderDraft, distanceInMiles);
    }

    public Instant now() {
        return Instant.now();
    }
}
