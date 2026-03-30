package com.pizzaco.order.application.service;

import static com.pizzaco.order.fixture.AddressFixture.SHOP;
import static com.pizzaco.order.fixture.CommandFixture.*;
import static com.pizzaco.order.fixture.OrderFixture.createOrder;
import static com.pizzaco.order.fixture.PizzaFixture.margherita;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pizzaco.order.application.port.in.command.PizzaItemCommand;
import com.pizzaco.order.application.port.out.LoadOrderPort;
import com.pizzaco.order.application.port.out.PaymentPort;
import com.pizzaco.order.application.port.out.SaveOrderPort;
import com.pizzaco.order.domain.exception.DomainException;
import com.pizzaco.order.domain.exception.InvalidPizzaException;
import com.pizzaco.order.domain.exception.OrderNotFoundException;
import com.pizzaco.order.domain.model.*;
import com.pizzaco.order.domain.service.DeliveryFeeCalculator;
import com.pizzaco.order.domain.service.PizzaPriceCalculator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the Application Service with mocked ports.
 *
 * <p>Demonstrates the blog's "Testable" advantage: "You can test the entire checkout flow without
 * actually charging a credit card."
 *
 * <p>The PaymentPort and persistence ports are mocked — no real DB or payment provider needed.
 */
class OrderApplicationServiceTest {

  private OrderApplicationService service;
  private LoadOrderPort loadOrderPort;
  private SaveOrderPort saveOrderPort;
  private PaymentPort paymentPort;

  @BeforeEach
  void setUp() {
    loadOrderPort = mock(LoadOrderPort.class);
    saveOrderPort = mock(SaveOrderPort.class);
    paymentPort = mock(PaymentPort.class);
    DeliveryFeeCalculator deliveryFeeCalculator = new DeliveryFeeCalculator(SHOP);
    PizzaPriceCalculator pizzaPriceCalculator = new PizzaPriceCalculator(false);

    service =
        new OrderApplicationService(
            loadOrderPort, saveOrderPort, paymentPort, deliveryFeeCalculator, pizzaPriceCalculator);

    // By default, payment succeeds and save returns the order as-is
    when(paymentPort.charge(any(), any())).thenReturn(true);
    when(saveOrderPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  @DisplayName("Should place order successfully — full checkout without charging a real card")
  void shouldPlaceOrderSuccessfully() {
    var command = placeOrderCommand(List.of(margheritaWithExtraCheeseItem(), hawaiianItem()));

    PizzaOrder result = service.placeOrder(command);

    assertNotNull(result);
    assertEquals("Jane Doe", result.getCustomerName());
    assertEquals(2, result.getPizzas().size());
    assertEquals(OrderStatus.PLACED, result.getStatus());
    assertTrue(result.getTotalPrice().isGreaterThan(Money.ZERO));

    // Verify the outbound ports were called
    verify(paymentPort).charge(any(OrderId.class), any(Money.class));
    verify(saveOrderPort).save(any(PizzaOrder.class));
  }

  @Test
  @DisplayName("Should fail if payment is declined")
  void shouldFailIfPaymentDeclined() {
    when(paymentPort.charge(any(), any())).thenReturn(false);

    var command = simpleMargheritaCommand();

    assertThrows(DomainException.class, () -> service.placeOrder(command));
    verify(saveOrderPort, never()).save(any());
  }

  @Test
  @DisplayName("Should enforce Hawaiian invariant through application flow")
  void shouldEnforceHawaiianInvariant() {
    var command = placeOrderCommand(List.of(hawaiianWithoutPineappleItem()));

    assertThrows(InvalidPizzaException.class, () -> service.placeOrder(command));
  }

  @Test
  @DisplayName("Should advance order status — Aggregate Root enforces lifecycle")
  void shouldAdvanceOrderStatus() {
    OrderId orderId = OrderId.generate();
    PizzaOrder existingOrder = createOrder(orderId, "John", SHOP);
    existingOrder.addPizza(margherita());

    when(loadOrderPort.loadById(orderId)).thenReturn(Optional.of(existingOrder));
    when(saveOrderPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

    PizzaOrder result = service.advanceOrderStatus(orderId);

    assertEquals(OrderStatus.PREPARING, result.getStatus());
    verify(saveOrderPort).save(any());
  }

  @Test
  @DisplayName("Should throw OrderNotFoundException for unknown order ID")
  void shouldThrowForUnknownOrder() {
    OrderId unknownId = OrderId.generate();
    when(loadOrderPort.loadById(unknownId)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class, () -> service.getOrder(unknownId));
  }

  @Test
  @DisplayName("Should resolve unknown toppings with default surcharge")
  void shouldResolveUnknownToppingsWithDefaultSurcharge() {
    var command =
        placeOrderCommand(List.of(new PizzaItemCommand("MARGHERITA", List.of("Truffle Oil"), 1)));

    PizzaOrder result = service.placeOrder(command);

    assertNotNull(result);
    assertEquals(1, result.getPizzas().size());
    // Custom topping should have default surcharge of $1.50
    assertEquals("Truffle Oil", result.getPizzas().getFirst().toppings().getFirst().name());
    assertEquals(Money.of(1.50), result.getPizzas().getFirst().toppings().getFirst().surcharge());
  }

  @Test
  @DisplayName("Should throw OrderNotFoundException when advancing unknown order")
  void shouldThrowWhenAdvancingUnknownOrder() {
    OrderId unknownId = OrderId.generate();
    when(loadOrderPort.loadById(unknownId)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class, () -> service.advanceOrderStatus(unknownId));
  }
}
