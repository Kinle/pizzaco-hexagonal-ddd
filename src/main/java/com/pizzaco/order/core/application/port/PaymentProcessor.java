package com.pizzaco.order.core.application.port;

import com.pizzaco.order.core.domain.model.Money;

public interface PaymentProcessor {
    PaymentResult processPayment(String customerId, Money amount);

    record PaymentResult(boolean success, String paymentReference, String failureReason) {}
}
