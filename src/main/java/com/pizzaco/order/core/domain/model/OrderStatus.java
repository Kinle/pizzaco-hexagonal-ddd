package com.pizzaco.order.core.domain.model;

public enum OrderStatus {
    Submitted,
    Confirmed,
    Baking,
    ReadyForDelivery,
    OutForDelivery,
    Delivered,
    Cancelled
}
