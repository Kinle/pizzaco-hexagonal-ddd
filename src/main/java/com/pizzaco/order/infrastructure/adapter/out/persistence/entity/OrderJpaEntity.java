package com.pizzaco.order.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for persisting orders — completely separate from the domain model. This separation
 * ensures the domain never leaks JPA annotations.
 */
@Entity
@Table(name = "pizza_orders")
@Getter
@Setter
@NoArgsConstructor
public class OrderJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "customer_name", nullable = false)
  private String customerName;

  @Column(name = "status", nullable = false)
  private String status;

  // Address fields (flattened — Value Object stored as columns)
  @Column(name = "delivery_street")
  private String deliveryStreet;

  @Column(name = "delivery_city")
  private String deliveryCity;

  @Column(name = "delivery_zip_code")
  private String deliveryZipCode;

  @Column(name = "delivery_latitude")
  private double deliveryLatitude;

  @Column(name = "delivery_longitude")
  private double deliveryLongitude;

  @Column(name = "delivery_fee", nullable = false)
  private BigDecimal deliveryFee;

  @Column(name = "total_price", nullable = false)
  private BigDecimal totalPrice;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @OneToMany(
      mappedBy = "order",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private List<PizzaJpaEntity> pizzas = new ArrayList<>();

  public void addPizza(PizzaJpaEntity pizza) {
    pizzas.add(pizza);
    pizza.setOrder(this);
  }
}
