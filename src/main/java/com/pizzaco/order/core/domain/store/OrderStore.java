package com.pizzaco.order.core.domain.store;

import com.pizzaco.order.core.domain.model.OrderId;
import com.pizzaco.order.core.domain.model.PizzaOrder;
import java.util.Optional;

public interface OrderStore {
    void save(PizzaOrder order);

    Optional<PizzaOrder> findById(OrderId id);
}
