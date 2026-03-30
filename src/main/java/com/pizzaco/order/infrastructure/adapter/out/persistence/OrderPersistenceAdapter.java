package com.pizzaco.order.infrastructure.adapter.out.persistence;

import com.pizzaco.order.application.port.out.LoadOrderPort;
import com.pizzaco.order.application.port.out.SaveOrderPort;
import com.pizzaco.order.domain.model.OrderId;
import com.pizzaco.order.domain.model.PizzaOrder;
import com.pizzaco.order.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import com.pizzaco.order.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Outbound Adapter — "PostgresOrderRepository" (using H2 in this demo)
 *
 * <p>"Outbound Adapter: Implements the contract using a specific tool, like a StripePaymentAdapter
 * or PostgresOrderRepository."
 *
 * <p>Implements both LoadOrderPort and SaveOrderPort outbound ports. The domain and application
 * layers only see the port interfaces — never this class.
 */
@Component
@Transactional
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements LoadOrderPort, SaveOrderPort {

  private final SpringDataOrderRepository repository;
  private final OrderPersistenceMapper mapper;

  @Override
  public Optional<PizzaOrder> loadById(OrderId orderId) {
    return repository.findById(orderId.value()).map(mapper::toDomainEntity);
  }

  @Override
  public PizzaOrder save(PizzaOrder order) {
    OrderJpaEntity jpaEntity = mapper.toJpaEntity(order);
    OrderJpaEntity saved = repository.save(jpaEntity);
    return mapper.toDomainEntity(saved);
  }
}
