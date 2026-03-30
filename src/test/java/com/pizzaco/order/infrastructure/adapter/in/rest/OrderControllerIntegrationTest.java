package com.pizzaco.order.infrastructure.adapter.in.rest;

import static com.pizzaco.order.fixture.RequestFixture.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration test — full end-to-end through REST → Application → Domain → H2 database.
 *
 * <p>Demonstrates: "You can test the entire checkout flow without actually charging a credit card."
 * The LoggingPaymentAdapter simulates payment via logging — no real charge occurs.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = objectMapper();
  }

  @Test
  @DisplayName("Full E2E: place order → get order → advance status through lifecycle")
  void fullEndToEndFlow() throws Exception {
    var request = validOrderRequest();

    String responseJson =
        mockMvc
            .perform(
                post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").isNotEmpty())
            .andExpect(jsonPath("$.customerName").value("Alice Wonderland"))
            .andExpect(jsonPath("$.status").value("PLACED"))
            .andExpect(jsonPath("$.pizzas", hasSize(2)))
            .andExpect(jsonPath("$.totalPrice").isNumber())
            .andExpect(jsonPath("$.deliveryFee").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String orderId = mapper.readTree(responseJson).get("orderId").asText();

    // 2. Get order by ID
    mockMvc
        .perform(get("/api/orders/{id}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(orderId))
        .andExpect(jsonPath("$.status").value("PLACED"));

    // 3. Advance status: PLACED → PREPARING
    mockMvc
        .perform(patch("/api/orders/{id}/status", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PREPARING"));

    // 4. Advance status: PREPARING → BAKED
    mockMvc
        .perform(patch("/api/orders/{id}/status", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("BAKED"));

    // 5. Advance status: BAKED → OUT_FOR_DELIVERY
    mockMvc
        .perform(patch("/api/orders/{id}/status", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OUT_FOR_DELIVERY"));

    // 6. Advance status: OUT_FOR_DELIVERY → DELIVERED
    mockMvc
        .perform(patch("/api/orders/{id}/status", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DELIVERED"));

    // 7. Advancing past DELIVERED should fail
    mockMvc.perform(patch("/api/orders/{id}/status", orderId)).andExpect(status().isConflict());
  }

  @Test
  @DisplayName("Hawaiian pizza without pineapple should return 400")
  void hawaiianWithoutPineappleShouldReturn400() throws Exception {
    var request = hawaiianWithoutPineappleRequest();

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", containsString("Pineapple")));
  }

  @Test
  @DisplayName("Getting a non-existent order should return 404")
  void nonExistentOrderShouldReturn404() throws Exception {
    mockMvc
        .perform(get("/api/orders/{id}", "00000000-0000-0000-0000-000000000000"))
        .andExpect(status().isNotFound());
  }
}
