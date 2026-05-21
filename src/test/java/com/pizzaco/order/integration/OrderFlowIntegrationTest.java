package com.pizzaco.order.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void shouldPlaceAndProgressOrderToDelivered() throws Exception {
        String placeOrderRequest =
                """
        {
          "customerId": "customer-9",
          "customerContact": "customer9@example.com",
          "deliveryAddress": "742 Evergreen Terrace",
          "deliveryDistanceMiles": 3.2,
          "items": [
            {
              "size": "Large",
              "crustType": "Thin",
              "priceAtPurchase": 18.50,
              "quantity": 1,
              "toppings": [
                {"name": "Pepperoni", "extraCost": 1.00}
              ]
            }
          ]
        }
        """;

        MvcResult created =
                mockMvc.perform(
                                post("/orders")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(placeOrderRequest))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.status").value("Confirmed"))
                        .andReturn();

        JsonNode body = objectMapper.readTree(created.getResponse().getContentAsString());
        String orderId = body.get("orderId").asText();

        progress(orderId, "Baking");
        progress(orderId, "ReadyForDelivery");
        progress(orderId, "OutForDelivery");
        progress(orderId, "Delivered");

        mockMvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Delivered"));
    }

    private void progress(String orderId, String targetStatus) throws Exception {
        String payload = "{\"targetStatus\":\"" + targetStatus + "\"}";
        mockMvc.perform(
                        post("/orders/{orderId}/progress", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isOk());
    }
}
