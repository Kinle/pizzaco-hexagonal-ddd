package com.pizzaco.order.domain.service;

import static com.pizzaco.order.fixture.AddressFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import com.pizzaco.order.domain.model.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the DeliveryFeeCalculator Domain Service. Demonstrates: distance-based fee
 * calculation using Haversine formula. Pure domain logic — no Spring context needed.
 */
class DeliveryFeeCalculatorTest {

  private DeliveryFeeCalculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new DeliveryFeeCalculator(SHOP);
  }

  @Test
  @DisplayName("Very close delivery should incur minimum fee of $3.00")
  void veryCloseDeliveryShouldUseMinimumFee() {
    Money fee = calculator.calculate(NEARBY);
    assertEquals(new BigDecimal("3.00"), fee.amount());
  }

  @Test
  @DisplayName("Far delivery should incur maximum fee of $15.00")
  void farDeliveryShouldUseMaximumFee() {
    Money fee = calculator.calculate(BOSTON);
    assertEquals(new BigDecimal("15.00"), fee.amount());
  }

  @Test
  @DisplayName("Mid-range delivery should have fee between min and max")
  void midRangeDeliveryShouldBeBetweenMinAndMax() {
    Money fee = calculator.calculate(NEWARK);
    assertTrue(fee.amount().compareTo(new BigDecimal("3.00")) >= 0);
    assertTrue(fee.amount().compareTo(new BigDecimal("15.00")) <= 0);
  }

  @Test
  @DisplayName("Should reject null delivery address")
  void shouldRejectNullDeliveryAddress() {
    assertThrows(IllegalArgumentException.class, () -> calculator.calculate(null));
  }
}
