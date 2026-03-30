package com.pizzaco.order.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pizzaco.order.infrastructure.adapter.in.rest.dto.PizzaItemRequest;
import com.pizzaco.order.infrastructure.adapter.in.rest.dto.PlaceOrderRequest;
import java.util.List;

/** Reusable REST request fixtures and a pre-configured ObjectMapper for integration tests. */
public final class RequestFixture {

  private RequestFixture() {}

  /** Returns an ObjectMapper with JavaTimeModule pre-registered. */
  public static ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  /** A valid PlaceOrderRequest with two pizzas (Margherita + Hawaiian). */
  public static PlaceOrderRequest validOrderRequest() {
    return new PlaceOrderRequest(
        "Alice Wonderland",
        AddressFixture.E2E_CUSTOMER.street(),
        AddressFixture.E2E_CUSTOMER.city(),
        AddressFixture.E2E_CUSTOMER.zipCode(),
        AddressFixture.E2E_CUSTOMER.latitude(),
        AddressFixture.E2E_CUSTOMER.longitude(),
        List.of(
            new PizzaItemRequest("MARGHERITA", List.of("Extra Cheese", "Mushroom"), 2),
            new PizzaItemRequest("HAWAIIAN", List.of("Pineapple", "Bacon"), 1)));
  }

  /**
   * A PlaceOrderRequest with a Hawaiian pizza that lacks Pineapple — triggers business rule
   * violation.
   */
  public static PlaceOrderRequest hawaiianWithoutPineappleRequest() {
    return new PlaceOrderRequest(
        "Bob Builder",
        AddressFixture.SHOP.street(),
        AddressFixture.SHOP.city(),
        AddressFixture.SHOP.zipCode(),
        AddressFixture.SHOP.latitude(),
        AddressFixture.SHOP.longitude(),
        List.of(new PizzaItemRequest("HAWAIIAN", List.of("Extra Cheese"), 1)));
  }

  /** A PlaceOrderRequest with a single Margherita. */
  public static PlaceOrderRequest simpleMargheritaRequest() {
    return new PlaceOrderRequest(
        "Test User",
        AddressFixture.NYC_DEFAULT.street(),
        AddressFixture.NYC_DEFAULT.city(),
        AddressFixture.NYC_DEFAULT.zipCode(),
        AddressFixture.NYC_DEFAULT.latitude(),
        AddressFixture.NYC_DEFAULT.longitude(),
        List.of(new PizzaItemRequest("MARGHERITA", List.of(), 1)));
  }
}
