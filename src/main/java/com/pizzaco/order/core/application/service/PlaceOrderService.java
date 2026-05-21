package com.pizzaco.order.core.application.service;

import com.pizzaco.order.core.application.exception.PaymentFailedException;
import com.pizzaco.order.core.application.port.ClockPort;
import com.pizzaco.order.core.application.port.EtaProcessor;
import com.pizzaco.order.core.application.port.NotificationPort;
import com.pizzaco.order.core.application.port.PaymentProcessor;
import com.pizzaco.order.core.application.usecase.PlaceOrderUseCase;
import com.pizzaco.order.core.domain.model.Money;
import com.pizzaco.order.core.domain.model.PaymentReference;
import com.pizzaco.order.core.domain.model.PizzaOrder;
import com.pizzaco.order.core.domain.service.OrderPricingService;
import com.pizzaco.order.core.domain.service.OrderValidationService;
import com.pizzaco.order.core.domain.store.OrderStore;

public class PlaceOrderService implements PlaceOrderUseCase {
    private final EtaProcessor etaProcessor;
    private final PaymentProcessor paymentProcessor;
    private final NotificationPort notificationPort;
    private final OrderStore orderStore;
    private final OrderValidationService validationService;
    private final OrderPricingService pricingService;
    private final ClockPort clockPort;

    public PlaceOrderService(
            EtaProcessor etaProcessor,
            PaymentProcessor paymentProcessor,
            NotificationPort notificationPort,
            OrderStore orderStore,
            OrderValidationService validationService,
            OrderPricingService pricingService,
            ClockPort clockPort) {
        this.etaProcessor = etaProcessor;
        this.paymentProcessor = paymentProcessor;
        this.notificationPort = notificationPort;
        this.orderStore = orderStore;
        this.validationService = validationService;
        this.pricingService = pricingService;
        this.clockPort = clockPort;
    }

    @Override
    public PlaceOrderResult placeOrder(PlaceOrderCommand command) {
        validationService.validate(
                command.items(), command.deliveryAddress(), command.customerContact());

        var eta = etaProcessor.estimateMinutes(command.storeAddress(), command.deliveryAddress());
        Money subtotal = pricingService.calculateSubtotal(command.items());

        PizzaOrder pricingDraft =
                PizzaOrder.submitted(
                        command.items(),
                        command.deliveryAddress(),
                        command.customerContact(),
                        subtotal,
                        Money.ZERO,
                        subtotal,
                        eta,
                        clockPort.now());

        Money deliveryFee =
                pricingService.calculateDeliveryFee(pricingDraft, command.deliveryDistanceMiles());
        Money total = subtotal.add(deliveryFee);

        PizzaOrder submittedOrder =
                PizzaOrder.rehydrate(
                        pricingDraft.getId(),
                        pricingDraft.getStatus(),
                        pricingDraft.getItems(),
                        pricingDraft.getDeliveryAddress(),
                        pricingDraft.getCustomerContact(),
                        subtotal,
                        deliveryFee,
                        total,
                        eta,
                        null,
                        pricingDraft.getPlacedAt());

        var paymentResult = paymentProcessor.processPayment(command.customerId(), total);
        if (!paymentResult.success()) {
            throw new PaymentFailedException("Payment failed: " + paymentResult.failureReason());
        }

        PizzaOrder confirmedOrder =
                submittedOrder.confirm(new PaymentReference(paymentResult.paymentReference()));
        orderStore.save(confirmedOrder);

        notificationPort.notifyCustomer(
                confirmedOrder.getCustomerContact(),
                "Order confirmed. ETA: " + confirmedOrder.getEtaMinutes().value() + " minutes.");

        return new PlaceOrderResult(
                confirmedOrder.getId(),
                confirmedOrder.getStatus(),
                confirmedOrder.getTotal(),
                confirmedOrder.getEtaMinutes().value(),
                confirmedOrder.getPaymentReference().value());
    }
}
