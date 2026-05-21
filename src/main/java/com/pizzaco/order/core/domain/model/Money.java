package com.pizzaco.order.core.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal value) {
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(value, "Money value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money value cannot be negative");
        }
        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public Money add(Money other) {
        return new Money(value.add(other.value));
    }

    public Money multiply(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        return new Money(value.multiply(BigDecimal.valueOf(count)));
    }
}
