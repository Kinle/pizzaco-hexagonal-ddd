package com.pizzaco.order.core.domain.service;

import com.pizzaco.order.core.domain.model.CustomerContact;
import com.pizzaco.order.core.domain.model.DeliveryAddress;
import com.pizzaco.order.core.domain.model.PizzaLineItem;
import java.util.List;

public class OrderValidationService {
    public void validate(
            List<PizzaLineItem> items,
            DeliveryAddress deliveryAddress,
            CustomerContact customerContact) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one pizza line item");
        }
        if (items.stream().anyMatch(item -> item.quantity() <= 0)) {
            throw new IllegalArgumentException(
                    "All pizza line item quantities must be greater than zero");
        }
        if (deliveryAddress == null) {
            throw new IllegalArgumentException("Delivery address is required");
        }
        if (customerContact == null) {
            throw new IllegalArgumentException("Customer contact is required");
        }
    }
}
