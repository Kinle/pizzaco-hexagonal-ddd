package com.pizzaco.order.infrastructure.adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * REST request DTO for placing an order. Part of the Inbound Adapter — specific to the REST/JSON
 * transport. Jakarta validation annotations provide automatic field-level error responses.
 */
public record PlaceOrderRequest(
    @NotBlank(message = "Customer name is required") String customerName,
    @NotBlank(message = "Street is required") String street,
    @NotBlank(message = "City is required") String city,
    @NotBlank(message = "Zip code is required") String zipCode,
    double latitude,
    double longitude,
    @NotEmpty(message = "At least one pizza is required") List<@Valid PizzaItemRequest> pizzas) {}
