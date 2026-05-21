package com.pizzaco.order.core.domain.service;

import com.pizzaco.order.core.domain.model.Money;
import com.pizzaco.order.core.domain.model.PizzaOrder;
import java.math.BigDecimal;

public class DeliveryFeeCalculator {
    private static final Money STANDARD_FEE = new Money(new BigDecimal("5.00"));
    private static final int FAMILY_DISCOUNT_THRESHOLD = 3;
    private static final double FREE_DELIVERY_RADIUS_MILES = 5.0;

    public Money calculateFee(PizzaOrder order, double distanceInMiles) {
        // Rule: "Free delivery if you order 3 pizzas AND live within 5 miles"
        if (order.getItems().size() >= FAMILY_DISCOUNT_THRESHOLD
                && distanceInMiles <= FREE_DELIVERY_RADIUS_MILES) {
            return Money.ZERO;
        }

        return STANDARD_FEE;
    }
}
