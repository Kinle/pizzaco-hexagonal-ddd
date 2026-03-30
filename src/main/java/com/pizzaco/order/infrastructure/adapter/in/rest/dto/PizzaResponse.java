package com.pizzaco.order.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.util.List;

/** REST response DTO for a single pizza. */
public record PizzaResponse(String type, List<String> toppings, int quantity, BigDecimal price) {}
