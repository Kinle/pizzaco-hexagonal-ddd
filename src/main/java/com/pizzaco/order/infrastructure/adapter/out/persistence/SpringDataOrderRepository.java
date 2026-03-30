package com.pizzaco.order.infrastructure.adapter.out.persistence;

import com.pizzaco.order.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository interface. This is an infrastructure concern — the domain and
 * application layers never see this.
 */
public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {}
