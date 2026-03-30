package com.pizzaco.order.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA entity for persisting individual pizzas within an order. */
@Entity
@Table(name = "pizzas")
@Getter
@Setter
@NoArgsConstructor
public class PizzaJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "pizza_type", nullable = false)
  private String pizzaType;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "pizza_toppings", joinColumns = @JoinColumn(name = "pizza_id"))
  private List<ToppingEmbeddable> toppings = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderJpaEntity order;
}
