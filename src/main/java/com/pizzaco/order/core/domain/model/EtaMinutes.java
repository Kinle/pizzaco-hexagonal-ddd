package com.pizzaco.order.core.domain.model;

public record EtaMinutes(int value) {
    public EtaMinutes {
        if (value <= 0) {
            throw new IllegalArgumentException("ETA must be greater than zero");
        }
    }
}
