package com.pizzaco.order.domain.model;

import static com.pizzaco.order.fixture.AddressFixture.NYC_DEFAULT;
import static com.pizzaco.order.fixture.PizzaFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import com.pizzaco.order.domain.exception.InvalidOrderStateException;
import com.pizzaco.order.domain.exception.InvalidPizzaException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PizzaOrder Aggregate Root. Demonstrates: status lifecycle transitions,
 * aggregate root as gateway, and the invariant "an order cannot be Out for Delivery if it hasn't
 * been Baked."
 */
class PizzaOrderTest {

  private PizzaOrder order;

  @BeforeEach
  void setUp() {
    order = PizzaOrder.create(OrderId.generate(), "John Doe", NYC_DEFAULT);
  }

  @Test
  @DisplayName("New order should start with PLACED status")
  void newOrderShouldBePlaced() {
    assertEquals(OrderStatus.PLACED, order.getStatus());
  }

  @Test
  @DisplayName("Should add pizza via Aggregate Root")
  void shouldAddPizzaViaAggregateRoot() {
    order.addPizza(margheritaWithExtraCheese());
    assertEquals(1, order.getPizzas().size());
  }

  @Test
  @DisplayName("Should remove pizza via Aggregate Root")
  void shouldRemovePizzaViaAggregateRoot() {
    order.addPizza(margherita());
    order.addPizza(pepperoni());
    assertEquals(2, order.getPizzas().size());

    order.removePizza(0);
    assertEquals(1, order.getPizzas().size());
    assertEquals(PizzaType.PEPPERONI, order.getPizzas().getFirst().type());
  }

  @Test
  @DisplayName("Cannot remove pizza after order moves past PLACED")
  void cannotRemovePizzaAfterPlaced() {
    order.addPizza(margherita());
    order.advanceStatus(); // PREPARING

    assertThrows(InvalidOrderStateException.class, () -> order.removePizza(0));
  }

  @Test
  @DisplayName(
      "Should advance status through full lifecycle: PLACED → PREPARING → BAKED → OUT_FOR_DELIVERY → DELIVERED")
  void shouldAdvanceThroughFullLifecycle() {
    order.addPizza(margherita());

    assertEquals(OrderStatus.PLACED, order.getStatus());

    order.advanceStatus();
    assertEquals(OrderStatus.PREPARING, order.getStatus());

    order.advanceStatus();
    assertEquals(OrderStatus.BAKED, order.getStatus());

    order.advanceStatus();
    assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());

    order.advanceStatus();
    assertEquals(OrderStatus.DELIVERED, order.getStatus());
  }

  @Test
  @DisplayName("Cannot advance past DELIVERED — state machine enforced")
  void cannotAdvancePastDelivered() {
    order.addPizza(margherita());
    order.advanceStatus(); // PREPARING
    order.advanceStatus(); // BAKED
    order.advanceStatus(); // OUT_FOR_DELIVERY
    order.advanceStatus(); // DELIVERED

    assertThrows(InvalidOrderStateException.class, order::advanceStatus);
  }

  @Test
  @DisplayName("Cannot advance an empty order")
  void cannotAdvanceEmptyOrder() {
    assertThrows(InvalidOrderStateException.class, order::advanceStatus);
  }

  @Test
  @DisplayName("Cannot add pizza after order moves past PLACED")
  void cannotAddPizzaAfterPlaced() {
    order.addPizza(margherita());
    order.advanceStatus(); // PREPARING

    assertThrows(InvalidOrderStateException.class, () -> order.addPizza(pepperoni()));
  }

  @Test
  @DisplayName("Should calculate total correctly: pizza prices + delivery fee")
  void shouldCalculateTotalCorrectly() {
    // MARGHERITA ($8.00) + PEPPERONI ($10.00 + $2.00 pepperoni topping) = $20.00
    order.addPizza(margherita());
    order.addPizza(pepperoniWithPepperoni());
    order.applyDeliveryFee(Money.of(5.00));

    Money total = order.recalculateTotal();
    assertEquals(Money.of(25.00), total);
  }

  @Test
  @DisplayName("Pizzas list returned by Aggregate Root should be unmodifiable")
  void pizzasListShouldBeUnmodifiable() {
    order.addPizza(margherita());
    assertThrows(
        UnsupportedOperationException.class,
        () -> order.getPizzas().add(new Pizza(PizzaType.VEGGIE, List.of(), 1)));
  }

  // ── Entity Identity Tests ──────────────────────────────────────────

  @Test
  @DisplayName("Entity equality: same ID = same entity, regardless of state")
  void sameIdMeansSameEntity() {
    OrderId sharedId = OrderId.generate();
    PizzaOrder order1 = PizzaOrder.create(sharedId, "Alice", NYC_DEFAULT);
    PizzaOrder order2 = PizzaOrder.create(sharedId, "Bob", NYC_DEFAULT);

    assertEquals(order1, order2, "Entities with the same ID must be equal");
    assertEquals(order1.hashCode(), order2.hashCode());
  }

  @Test
  @DisplayName("Entity inequality: different ID = different entity, even if attributes match")
  void differentIdMeansDifferentEntity() {
    PizzaOrder order1 = PizzaOrder.create(OrderId.generate(), "Alice", NYC_DEFAULT);
    PizzaOrder order2 = PizzaOrder.create(OrderId.generate(), "Alice", NYC_DEFAULT);

    assertNotEquals(order1, order2, "Entities with different IDs must not be equal");
  }

  @Test
  @DisplayName("Value Object equality contrast: Toppings compare by attributes, not identity")
  void valueObjectEqualityByAttributes() {
    Topping topping1 = new Topping("Pineapple", Money.of(1.00));
    Topping topping2 = new Topping("Pineapple", Money.of(1.00));
    assertEquals(topping1, topping2, "Value Objects with same attributes must be equal");
  }

  // ── Additional coverage for Aggregate Root validation ────────────────

  @Test
  @DisplayName("Should reject null customer name")
  void shouldRejectNullCustomerName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PizzaOrder.create(OrderId.generate(), null, NYC_DEFAULT));
  }

  @Test
  @DisplayName("Should reject blank customer name")
  void shouldRejectBlankCustomerName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PizzaOrder.create(OrderId.generate(), "   ", NYC_DEFAULT));
  }

  @Test
  @DisplayName("Should reject null delivery address")
  void shouldRejectNullDeliveryAddress() {
    assertThrows(
        IllegalArgumentException.class, () -> PizzaOrder.create(OrderId.generate(), "John", null));
  }

  @Test
  @DisplayName("Should reject adding null pizza")
  void shouldRejectNullPizza() {
    assertThrows(InvalidPizzaException.class, () -> order.addPizza(null));
  }

  @Test
  @DisplayName("Should reject invalid remove pizza index — negative")
  void shouldRejectNegativeRemoveIndex() {
    order.addPizza(margherita());
    assertThrows(IllegalArgumentException.class, () -> order.removePizza(-1));
  }

  @Test
  @DisplayName("Should reject invalid remove pizza index — out of bounds")
  void shouldRejectOutOfBoundsRemoveIndex() {
    order.addPizza(margherita());
    assertThrows(IllegalArgumentException.class, () -> order.removePizza(5));
  }

  @Test
  @DisplayName("Should reject null delivery fee")
  void shouldRejectNullDeliveryFee() {
    assertThrows(IllegalArgumentException.class, () -> order.applyDeliveryFee(null));
  }

  @Test
  @DisplayName("Should reject null pizza total")
  void shouldRejectNullPizzaTotal() {
    assertThrows(IllegalArgumentException.class, () -> order.applyPizzaTotal(null));
  }

  @Test
  @DisplayName("applyPizzaTotal should set totalPrice to pizzaTotal + deliveryFee")
  void applyPizzaTotalShouldAddDeliveryFee() {
    order.applyDeliveryFee(Money.of(5.00));
    order.applyPizzaTotal(Money.of(20.00));
    assertEquals(Money.of(25.00), order.getTotalPrice());
  }

  @Test
  @DisplayName("reconstitute should restore all fields")
  void reconstituteShouldRestoreAllFields() {
    OrderId id = OrderId.generate();
    List<Pizza> pizzas = List.of(margherita());
    LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 12, 0);

    PizzaOrder reconstituted =
        PizzaOrder.reconstitute(
            id,
            "Alice",
            NYC_DEFAULT,
            pizzas,
            OrderStatus.BAKED,
            Money.of(4.00),
            Money.of(12.00),
            createdAt);

    assertEquals(id, reconstituted.getId());
    assertEquals("Alice", reconstituted.getCustomerName());
    assertEquals(OrderStatus.BAKED, reconstituted.getStatus());
    assertEquals(Money.of(4.00), reconstituted.getDeliveryFee());
    assertEquals(Money.of(12.00), reconstituted.getTotalPrice());
    assertEquals(createdAt, reconstituted.getCreatedAt());
    assertEquals(1, reconstituted.getPizzas().size());
  }

  @Test
  @DisplayName("toString should contain id, status, and totalPrice")
  void toStringShouldContainIdStatusTotalPrice() {
    String str = order.toString();
    assertNotNull(str);
    assertTrue(str.contains("PLACED"));
  }
}
