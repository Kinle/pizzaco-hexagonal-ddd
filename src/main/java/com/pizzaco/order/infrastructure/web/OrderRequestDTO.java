package com.pizzaco.order.infrastructure.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record OrderRequestDTO(
        @NotBlank String customerId,
        @NotBlank String customerContact,
        @NotBlank String deliveryAddress,
        @DecimalMin("0.0") double deliveryDistanceMiles,
        @NotEmpty List<@Valid ItemDTO> items) {

    public record ItemDTO(
            @NotBlank String size,
            @NotBlank String crustType,
            @NotNull @Positive BigDecimal priceAtPurchase,
            @Positive int quantity,
            @NotNull List<@Valid ToppingDTO> toppings) {}

    public record ToppingDTO(
            @NotBlank String name, @NotNull @DecimalMin("0.0") BigDecimal extraCost) {}
}
