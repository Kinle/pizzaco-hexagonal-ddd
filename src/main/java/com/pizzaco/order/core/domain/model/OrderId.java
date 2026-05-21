package com.pizzaco.order.core.domain.model;

import java.util.UUID;

public record OrderId(UUID value) {
    public OrderId {
        if (value == null) throw new IllegalArgumentException("OrderId cannot be null");
    }
}
