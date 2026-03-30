package com.pizzaco.order.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object representing a monetary amount. Immutable — two Money instances with the same amount
 * are interchangeable. Enforces the invariant: amount cannot be negative.
 */
public record Money(BigDecimal amount) {

  public static final Money ZERO = new Money(BigDecimal.ZERO);

  public Money(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("Money amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Money amount cannot be negative: " + amount);
    }
    this.amount = amount.setScale(2, RoundingMode.HALF_UP);
  }

  public static Money of(double amount) {
    return new Money(BigDecimal.valueOf(amount));
  }

  public static Money of(BigDecimal amount) {
    return new Money(amount);
  }

  public Money add(Money other) {
    return new Money(this.amount.add(other.amount));
  }

  public Money subtract(Money other) {
    return new Money(this.amount.subtract(other.amount));
  }

  public Money multiply(int quantity) {
    return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)));
  }

  /**
   * Returns the given percentage of this amount. e.g., Money.of(100).percentage(10) →
   * Money.of(10.00)
   */
  public Money percentage(int percent) {
    BigDecimal factor =
        BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    return new Money(this.amount.multiply(factor));
  }

  public boolean isGreaterThan(Money other) {
    return this.amount.compareTo(other.amount) > 0;
  }

  @Override
  public String toString() {
    return "$" + amount.toPlainString();
  }
}
