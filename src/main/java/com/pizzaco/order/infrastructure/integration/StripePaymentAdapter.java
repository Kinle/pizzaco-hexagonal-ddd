package com.pizzaco.order.infrastructure.integration;

import com.pizzaco.order.core.application.port.PaymentProcessor;
import com.pizzaco.order.core.domain.model.Money;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StripePaymentAdapter implements PaymentProcessor {
    @Override
    public PaymentResult processPayment(String customerId, Money amount) {
        log.info(
                "Simulating payment processing for customer '{}' with amount {}",
                customerId,
                amount.value());
        return new PaymentResult(true, "stripe-" + UUID.randomUUID(), null);
    }
}
