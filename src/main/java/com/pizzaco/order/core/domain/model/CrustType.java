package com.pizzaco.order.core.domain.model;

public record CrustType(String value) {
    public CrustType {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("CrustType cannot be blank");
    }
}
