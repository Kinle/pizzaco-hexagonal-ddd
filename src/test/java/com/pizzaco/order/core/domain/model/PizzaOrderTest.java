package com.pizzaco.order.core.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class PizzaOrderTest {

    @Test
    void shouldProgressThroughValidLifecycle() {
        PizzaOrder order = sampleSubmittedOrder();

        PizzaOrder confirmed = order.confirm(new PaymentReference("pay-ref"));
        PizzaOrder baking = confirmed.startBaking();
        PizzaOrder ready = baking.markReadyForDelivery();
        PizzaOrder outForDelivery = ready.markOutForDelivery();
        PizzaOrder delivered = outForDelivery.markDelivered();

        assertEquals(OrderStatus.Delivered, delivered.getStatus());
    }

    @Test
    void shouldRejectInvalidTransition() {
        PizzaOrder order = sampleSubmittedOrder();

        assertThrows(IllegalStateException.class, order::startBaking);
    }

    private static PizzaOrder sampleSubmittedOrder() {
        PizzaLineItem item =
                new PizzaLineItem(
                        new PizzaSize("Large"),
                        new CrustType("Thin"),
                        List.of(new Topping("Olive", new Money(new BigDecimal("1.00")))),
                        new Money(new BigDecimal("10.00")),
                        2);

        return PizzaOrder.submitted(
                List.of(item),
                new DeliveryAddress("123 Main Street"),
                new CustomerContact("customer@example.com"),
                new Money(new BigDecimal("20.00")),
                new Money(new BigDecimal("5.00")),
                new Money(new BigDecimal("25.00")),
                new EtaMinutes(30),
                Instant.now());
    }
}
