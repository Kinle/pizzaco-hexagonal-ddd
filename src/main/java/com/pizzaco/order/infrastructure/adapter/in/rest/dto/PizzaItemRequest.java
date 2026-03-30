package com.pizzaco.order.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** REST request DTO for a single pizza item. */
public record PizzaItemRequest(
    @NotBlank(message = "Pizza type is required") String type,
    List<String> toppings,
    @Min(value = 1, message = "Quantity must be at least 1") int quantity) {}
