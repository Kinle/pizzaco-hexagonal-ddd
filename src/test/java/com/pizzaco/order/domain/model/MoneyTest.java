package com.pizzaco.order.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Money Value Object. Demonstrates: immutability, arithmetic, and the "Total
 * price cannot be negative" invariant.
 */
class MoneyTest {

  @Test
  @DisplayName("Should create Money with valid amount")
  void shouldCreateMoneyWithValidAmount() {
    Money money = Money.of(10.50);
    assertEquals(new BigDecimal("10.50"), money.amount());
  }

  @Test
  @DisplayName("Should reject negative amount — 'Total price cannot be negative'")
  void shouldRejectNegativeAmount() {
    assertThrows(IllegalArgumentException.class, () -> Money.of(-5.00));
  }

  @Test
  @DisplayName("Should reject null amount")
  void shouldRejectNullAmount() {
    assertThrows(IllegalArgumentException.class, () -> new Money(null));
  }

  @Test
  @DisplayName("Should add two Money values correctly")
  void shouldAddMoney() {
    Money a = Money.of(5.25);
    Money b = Money.of(3.75);
    Money result = a.add(b);
    assertEquals(new BigDecimal("9.00"), result.amount());
  }

  @Test
  @DisplayName("Should multiply Money by quantity")
  void shouldMultiplyMoney() {
    Money price = Money.of(8.00);
    Money result = price.multiply(3);
    assertEquals(new BigDecimal("24.00"), result.amount());
  }

  @Test
  @DisplayName("ZERO should have amount of 0.00")
  void zeroShouldBeZero() {
    assertEquals(new BigDecimal("0.00"), Money.ZERO.amount());
  }

  @Test
  @DisplayName("Should compare Money values correctly")
  void shouldCompareMoney() {
    Money ten = Money.of(10.00);
    Money five = Money.of(5.00);
    assertTrue(ten.isGreaterThan(five));
    assertFalse(five.isGreaterThan(ten));
  }

  @Test
  @DisplayName("Should subtract Money values correctly")
  void shouldSubtractMoney() {
    Money ten = Money.of(10.00);
    Money three = Money.of(3.00);
    assertEquals(Money.of(7.00), ten.subtract(three));
  }

  @Test
  @DisplayName("Subtract resulting in negative should throw — enforces non-negative invariant")
  void subtractNegativeShouldThrow() {
    Money three = Money.of(3.00);
    Money ten = Money.of(10.00);
    assertThrows(IllegalArgumentException.class, () -> three.subtract(ten));
  }

  @Test
  @DisplayName("Should calculate percentage correctly")
  void shouldCalculatePercentage() {
    Money hundred = Money.of(100.00);
    assertEquals(Money.of(10.00), hundred.percentage(10));
  }

  @Test
  @DisplayName("Value Object equality: same amount means equal (record semantics)")
  void valueObjectEquality() {
    Money a = Money.of(9.99);
    Money b = Money.of(9.99);
    assertEquals(a, b, "Value Objects with same attributes must be equal");
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  @DisplayName("toString should format with dollar sign")
  void toStringShouldFormatWithDollarSign() {
    Money money = Money.of(10.50);
    assertEquals("$10.50", money.toString());
  }

  @Test
  @DisplayName("of(BigDecimal) factory should work")
  void ofBigDecimalShouldWork() {
    Money money = Money.of(new java.math.BigDecimal("7.25"));
    assertEquals(new BigDecimal("7.25"), money.amount());
  }
}
