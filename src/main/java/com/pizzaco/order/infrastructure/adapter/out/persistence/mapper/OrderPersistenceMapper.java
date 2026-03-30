package com.pizzaco.order.infrastructure.adapter.out.persistence.mapper;

import com.pizzaco.order.domain.model.*;
import com.pizzaco.order.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import com.pizzaco.order.infrastructure.adapter.out.persistence.entity.PizzaJpaEntity;
import com.pizzaco.order.infrastructure.adapter.out.persistence.entity.ToppingEmbeddable;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Mapper between domain model and JPA entities.
 *
 * <p>This mapper is crucial to keeping the domain model free of JPA annotations. The domain "Pizza"
 * and "PizzaOrder" are completely different classes from the JPA "PizzaJpaEntity" and
 * "OrderJpaEntity".
 */
@Component
public class OrderPersistenceMapper {

  // ── Domain → JPA ────────────────────────────────────────────────────

  public OrderJpaEntity toJpaEntity(PizzaOrder order) {
    OrderJpaEntity entity = new OrderJpaEntity();
    entity.setId(order.getId().value());
    entity.setCustomerName(order.getCustomerName());
    entity.setStatus(order.getStatus().name());
    entity.setDeliveryStreet(order.getDeliveryAddress().street());
    entity.setDeliveryCity(order.getDeliveryAddress().city());
    entity.setDeliveryZipCode(order.getDeliveryAddress().zipCode());
    entity.setDeliveryLatitude(order.getDeliveryAddress().latitude());
    entity.setDeliveryLongitude(order.getDeliveryAddress().longitude());
    entity.setDeliveryFee(order.getDeliveryFee().amount());
    entity.setTotalPrice(order.getTotalPrice().amount());
    entity.setCreatedAt(order.getCreatedAt());

    for (Pizza pizza : order.getPizzas()) {
      PizzaJpaEntity pizzaEntity = toPizzaJpaEntity(pizza);
      entity.addPizza(pizzaEntity);
    }

    return entity;
  }

  private PizzaJpaEntity toPizzaJpaEntity(Pizza pizza) {
    PizzaJpaEntity entity = new PizzaJpaEntity();
    entity.setPizzaType(pizza.type().name());
    entity.setQuantity(pizza.quantity());
    entity.setPrice(pizza.calculatePrice().amount());

    List<ToppingEmbeddable> toppingEntities =
        pizza.toppings().stream()
            .map(t -> new ToppingEmbeddable(t.name(), t.surcharge().amount()))
            .toList();
    entity.setToppings(toppingEntities);

    return entity;
  }

  // ── JPA → Domain ────────────────────────────────────────────────────

  public PizzaOrder toDomainEntity(OrderJpaEntity entity) {
    OrderId orderId = OrderId.of(entity.getId());

    Address address =
        Address.of(
            entity.getDeliveryStreet(),
            entity.getDeliveryCity(),
            entity.getDeliveryZipCode(),
            entity.getDeliveryLatitude(),
            entity.getDeliveryLongitude());

    List<Pizza> pizzas = entity.getPizzas().stream().map(this::toDomainPizza).toList();

    return PizzaOrder.reconstitute(
        orderId,
        entity.getCustomerName(),
        address,
        pizzas,
        OrderStatus.valueOf(entity.getStatus()),
        Money.of(entity.getDeliveryFee()),
        Money.of(entity.getTotalPrice()),
        entity.getCreatedAt());
  }

  private Pizza toDomainPizza(PizzaJpaEntity entity) {
    PizzaType type = PizzaType.valueOf(entity.getPizzaType());

    List<Topping> toppings =
        entity.getToppings().stream()
            .map(t -> Topping.custom(t.getName(), Money.of(t.getSurcharge())))
            .toList();

    return new Pizza(type, toppings, entity.getQuantity());
  }
}
