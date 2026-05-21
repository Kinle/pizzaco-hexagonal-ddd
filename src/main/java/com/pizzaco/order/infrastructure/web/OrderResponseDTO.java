package com.pizzaco.order.infrastructure.web;

import java.time.Instant;
import java.util.UUID;

public record OrderResponseDTO(
        UUID orderId,
        String status,
        String total,
        int etaMinutes,
        String paymentReference,
        String deliveryAddress,
        Instant placedAt) {}
