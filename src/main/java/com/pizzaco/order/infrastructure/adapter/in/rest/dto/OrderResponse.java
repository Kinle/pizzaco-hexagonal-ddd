package com.pizzaco.order.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST response DTO for an order. Part of the Inbound Adapter — the JSON representation exposed to
 * the outside world.
 */
public record OrderResponse(
    String orderId,
    String customerName,
    String status,
    String deliveryAddress,
    List<PizzaResponse> pizzas,
    BigDecimal deliveryFee,
    BigDecimal totalPrice,
    LocalDateTime createdAt) {}
