package com.pizzaco.order.infrastructure.adapter.in.rest;

import static com.pizzaco.order.fixture.RequestFixture.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Tests that exercise all GlobalExceptionHandler branches. */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;
  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = objectMapper();
  }

  @Test
  @DisplayName("OrderNotFoundException should return 404")
  void orderNotFoundShouldReturn404() throws Exception {
    mockMvc
        .perform(get("/api/orders/{id}", UUID.randomUUID().toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").isNotEmpty());
  }

  @Test
  @DisplayName("InvalidPizzaException should return 400")
  void invalidPizzaShouldReturn400() throws Exception {
    var request = hawaiianWithoutPineappleRequest();

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  @DisplayName("InvalidOrderStateException should return 409 Conflict")
  void invalidOrderStateShouldReturn409() throws Exception {
    var request = simpleMargheritaRequest();

    String responseJson =
        mockMvc
            .perform(
                post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String orderId = mapper.readTree(responseJson).get("orderId").asText();

    // Advance to DELIVERED
    for (int i = 0; i < 4; i++) {
      mockMvc.perform(patch("/api/orders/{id}/status", orderId)).andExpect(status().isOk());
    }

    // Try to advance past DELIVERED → should return 409
    mockMvc
        .perform(patch("/api/orders/{id}/status", orderId))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"));
  }

  @Test
  @DisplayName("Validation errors should return 400 with field errors")
  void validationErrorsShouldReturn400() throws Exception {
    String invalidJson =
        """
        {
          "customerName": "",
          "street": "",
          "city": "",
          "zipCode": "",
          "latitude": 0,
          "longitude": 0,
          "pizzas": []
        }
        """;

    mockMvc
        .perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors").isArray());
  }

  @Test
  @DisplayName("IllegalArgumentException should return 400")
  void illegalArgumentShouldReturn400() throws Exception {
    mockMvc
        .perform(get("/api/orders/{id}", "not-a-uuid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }
}
