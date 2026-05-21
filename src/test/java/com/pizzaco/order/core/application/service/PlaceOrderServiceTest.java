package com.pizzaco.order.core.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pizzaco.order.core.application.exception.PaymentFailedException;
import com.pizzaco.order.core.application.port.ClockPort;
import com.pizzaco.order.core.application.port.NotificationPort;
import com.pizzaco.order.core.application.port.PaymentProcessor;
import com.pizzaco.order.core.application.usecase.PlaceOrderUseCase;
import com.pizzaco.order.core.domain.model.CrustType;
import com.pizzaco.order.core.domain.model.CustomerContact;
import com.pizzaco.order.core.domain.model.DeliveryAddress;
import com.pizzaco.order.core.domain.model.EtaMinutes;
import com.pizzaco.order.core.domain.model.Money;
import com.pizzaco.order.core.domain.model.OrderId;
import com.pizzaco.order.core.domain.model.OrderStatus;
import com.pizzaco.order.core.domain.model.PizzaLineItem;
import com.pizzaco.order.core.domain.model.PizzaOrder;
import com.pizzaco.order.core.domain.model.PizzaSize;
import com.pizzaco.order.core.domain.model.Topping;
import com.pizzaco.order.core.domain.service.DeliveryFeeCalculator;
import com.pizzaco.order.core.domain.service.OrderPricingService;
import com.pizzaco.order.core.domain.service.OrderValidationService;
import com.pizzaco.order.core.domain.store.OrderStore;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PlaceOrderServiceTest {

    @Test
    void shouldPlaceOrderSuccessfully() {
        InMemoryOrderStore orderStore = new InMemoryOrderStore();
        PlaceOrderService service =
                new PlaceOrderService(
                        (from, to) -> new EtaMinutes(25),
                        (customerId, amount) ->
                                new PaymentProcessor.PaymentResult(true, "pay-1", null),
                        (contact, message) ->
                                new NotificationPort.NotificationResult(
                                        true, "msg-1", Instant.now()),
                        orderStore,
                        new OrderValidationService(),
                        new OrderPricingService(new DeliveryFeeCalculator()),
                        fixedClock());

        var result = service.placeOrder(sampleCommand());

        assertEquals(OrderStatus.Confirmed, result.status());
        assertEquals("pay-1", result.paymentReference());
        assertEquals(1, orderStore.backingStore.size());
    }

    @Test
    void shouldFailWhenPaymentFails() {
        PlaceOrderService service =
                new PlaceOrderService(
                        (from, to) -> new EtaMinutes(25),
                        (customerId, amount) ->
                                new PaymentProcessor.PaymentResult(false, null, "card rejected"),
                        (contact, message) ->
                                new NotificationPort.NotificationResult(
                                        true, "msg-1", Instant.now()),
                        new InMemoryOrderStore(),
                        new OrderValidationService(),
                        new OrderPricingService(new DeliveryFeeCalculator()),
                        fixedClock());

        assertThrows(PaymentFailedException.class, () -> service.placeOrder(sampleCommand()));
    }

    private static PlaceOrderUseCase.PlaceOrderCommand sampleCommand() {
        PizzaLineItem item =
                new PizzaLineItem(
                        new PizzaSize("Medium"),
                        new CrustType("Pan"),
                        List.of(new Topping("Cheese", new Money(new BigDecimal("0.50")))),
                        new Money(new BigDecimal("12.00")),
                        1);

        return new PlaceOrderUseCase.PlaceOrderCommand(
                "customer-1",
                new CustomerContact("customer@example.com"),
                new DeliveryAddress("Pizzaco Central Kitchen"),
                new DeliveryAddress("221B Baker Street"),
                4.0,
                List.of(item));
    }

    private static ClockPort fixedClock() {
        return () -> Instant.parse("2026-03-30T10:15:30Z");
    }

    private static class InMemoryOrderStore implements OrderStore {
        private final Map<OrderId, PizzaOrder> backingStore = new HashMap<>();

        @Override
        public void save(PizzaOrder order) {
            backingStore.put(order.getId(), order);
        }

        @Override
        public Optional<PizzaOrder> findById(OrderId id) {
            return Optional.ofNullable(backingStore.get(id));
        }
    }
}
