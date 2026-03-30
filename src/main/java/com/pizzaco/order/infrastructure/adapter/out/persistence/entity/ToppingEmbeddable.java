package com.pizzaco.order.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Embeddable for storing topping data. Maps the immutable Topping Value Object to a persistable
 * form.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToppingEmbeddable {

  @Column(name = "topping_name", nullable = false)
  private String name;

  @Column(name = "surcharge", nullable = false)
  private BigDecimal surcharge;
}
