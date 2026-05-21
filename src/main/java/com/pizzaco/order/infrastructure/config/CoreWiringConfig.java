package com.pizzaco.order.infrastructure.config;

import com.pizzaco.order.core.application.port.ClockPort;
import com.pizzaco.order.core.application.port.EtaProcessor;
import com.pizzaco.order.core.application.port.NotificationPort;
import com.pizzaco.order.core.application.port.PaymentProcessor;
import com.pizzaco.order.core.application.service.GetOrderService;
import com.pizzaco.order.core.application.service.PlaceOrderService;
import com.pizzaco.order.core.application.service.ProgressOrderService;
import com.pizzaco.order.core.application.usecase.GetOrderUseCase;
import com.pizzaco.order.core.application.usecase.PlaceOrderUseCase;
import com.pizzaco.order.core.application.usecase.ProgressOrderUseCase;
import com.pizzaco.order.core.domain.service.DeliveryFeeCalculator;
import com.pizzaco.order.core.domain.service.OrderPricingService;
import com.pizzaco.order.core.domain.service.OrderValidationService;
import com.pizzaco.order.core.domain.store.OrderStore;
import java.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreWiringConfig {

    @Bean
    public DeliveryFeeCalculator deliveryFeeCalculator() {
        return new DeliveryFeeCalculator();
    }

    @Bean
    public OrderValidationService orderValidationService() {
        return new OrderValidationService();
    }

    @Bean
    public OrderPricingService orderPricingService(DeliveryFeeCalculator deliveryFeeCalculator) {
        return new OrderPricingService(deliveryFeeCalculator);
    }

    @Bean
    public ClockPort clockPort() {
        return Instant::now;
    }

    @Bean
    public PlaceOrderUseCase placeOrderUseCase(
            EtaProcessor etaProcessor,
            PaymentProcessor paymentProcessor,
            NotificationPort notificationPort,
            OrderStore orderStore,
            OrderValidationService orderValidationService,
            OrderPricingService orderPricingService,
            ClockPort clockPort) {
        return new PlaceOrderService(
                etaProcessor,
                paymentProcessor,
                notificationPort,
                orderStore,
                orderValidationService,
                orderPricingService,
                clockPort);
    }

    @Bean
    public ProgressOrderUseCase progressOrderUseCase(
            OrderStore orderStore, NotificationPort notificationPort) {
        return new ProgressOrderService(orderStore, notificationPort);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderStore orderStore) {
        return new GetOrderService(orderStore);
    }
}
