package com.pizzaco.order.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizzaco.order.core.domain.model.CustomerContact;
import com.pizzaco.order.core.domain.model.DeliveryAddress;
import com.pizzaco.order.core.domain.model.EtaMinutes;
import com.pizzaco.order.core.domain.model.Money;
import com.pizzaco.order.core.domain.model.OrderId;
import com.pizzaco.order.core.domain.model.PaymentReference;
import com.pizzaco.order.core.domain.model.PizzaOrder;
import com.pizzaco.order.core.domain.store.OrderStore;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRepository implements OrderStore {

    private final OrderJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void save(PizzaOrder order) {
        OrderEntity orderEntity = toEntity(order);
        jpaRepository.save(orderEntity);
    }

    @Override
    public Optional<PizzaOrder> findById(OrderId id) {
        return jpaRepository.findById(id.value()).map(this::toModel);
    }

    private OrderEntity toEntity(PizzaOrder order) {
        try {
            String itemsJson = objectMapper.writeValueAsString(order.getItems());
            return new OrderEntity(
                    order.getId().value(),
                    order.getStatus().name(),
                    order.getDeliveryAddress().value(),
                    order.getCustomerContact().value(),
                    order.getSubtotal().value(),
                    order.getDeliveryFee().value(),
                    order.getTotal().value(),
                    order.getEtaMinutes().value(),
                    order.getPaymentReference() == null
                            ? null
                            : order.getPaymentReference().value(),
                    order.getPlacedAt(),
                    itemsJson);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    private PizzaOrder toModel(OrderEntity entity) {
        try {
            List<com.pizzaco.order.core.domain.model.PizzaLineItem> items =
                    objectMapper.readValue(
                            entity.getItemsJson(),
                            new TypeReference<
                                    List<com.pizzaco.order.core.domain.model.PizzaLineItem>>() {});

            return PizzaOrder.rehydrate(
                    new OrderId(entity.getId()),
                    com.pizzaco.order.core.domain.model.OrderStatus.valueOf(entity.getStatus()),
                    items,
                    new DeliveryAddress(entity.getDeliveryAddress()),
                    new CustomerContact(entity.getCustomerContact()),
                    new Money(entity.getSubtotal()),
                    new Money(entity.getDeliveryFee()),
                    new Money(entity.getTotal()),
                    new EtaMinutes(entity.getEtaMinutes()),
                    entity.getPaymentReference() == null
                            ? null
                            : new PaymentReference(entity.getPaymentReference()),
                    entity.getPlacedAt());
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
