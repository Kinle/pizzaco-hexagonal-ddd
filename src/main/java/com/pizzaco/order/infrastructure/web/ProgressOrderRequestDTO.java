package com.pizzaco.order.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record ProgressOrderRequestDTO(@NotBlank String targetStatus) {}
