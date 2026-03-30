package com.pizzaco.order.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderIdTest {

  @Test
  @DisplayName("Should create OrderId from UUID")
  void shouldCreateFromUuid() {
    UUID uuid = UUID.randomUUID();
    OrderId orderId = OrderId.of(uuid);
    assertEquals(uuid, orderId.value());
  }

  @Test
  @DisplayName("Should generate random OrderId")
  void shouldGenerateRandom() {
    OrderId orderId = OrderId.generate();
    assertNotNull(orderId);
    assertNotNull(orderId.value());
  }

  @Test
  @DisplayName("Should reject null UUID")
  void shouldRejectNullUuid() {
    assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
  }

  @Test
  @DisplayName("toString should return UUID string")
  void toStringShouldReturnUuidString() {
    UUID uuid = UUID.randomUUID();
    OrderId orderId = OrderId.of(uuid);
    assertEquals(uuid.toString(), orderId.toString());
  }
}
