package com.pizzaco.order.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressTest {

  @Test
  @DisplayName("Should create valid address")
  void shouldCreateValidAddress() {
    Address address = Address.of("1 Main St", "NYC", "10001", 40.71, -74.00);
    assertNotNull(address);
    assertEquals("1 Main St", address.street());
    assertEquals("NYC", address.city());
    assertEquals("10001", address.zipCode());
  }

  @Test
  @DisplayName("Should reject null street")
  void shouldRejectNullStreet() {
    assertThrows(
        IllegalArgumentException.class, () -> Address.of(null, "NYC", "10001", 40.71, -74.00));
  }

  @Test
  @DisplayName("Should reject blank street")
  void shouldRejectBlankStreet() {
    assertThrows(
        IllegalArgumentException.class, () -> Address.of("  ", "NYC", "10001", 40.71, -74.00));
  }

  @Test
  @DisplayName("Should reject null city")
  void shouldRejectNullCity() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Address.of("1 Main St", null, "10001", 40.71, -74.00));
  }

  @Test
  @DisplayName("Should reject blank city")
  void shouldRejectBlankCity() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Address.of("1 Main St", "  ", "10001", 40.71, -74.00));
  }

  @Test
  @DisplayName("Should reject null zip code")
  void shouldRejectNullZipCode() {
    assertThrows(
        IllegalArgumentException.class, () -> Address.of("1 Main St", "NYC", null, 40.71, -74.00));
  }

  @Test
  @DisplayName("Should reject blank zip code")
  void shouldRejectBlankZipCode() {
    assertThrows(
        IllegalArgumentException.class, () -> Address.of("1 Main St", "NYC", "  ", 40.71, -74.00));
  }

  @Test
  @DisplayName("toString should format correctly")
  void toStringShouldFormatCorrectly() {
    Address address = Address.of("1 Main St", "NYC", "10001", 40.71, -74.00);
    assertEquals("1 Main St, NYC 10001", address.toString());
  }
}
