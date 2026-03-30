package com.pizzaco.order.infrastructure.adapter.in.rest;

import com.pizzaco.order.application.port.in.GetOrderUseCase;
import com.pizzaco.order.application.port.in.PlaceOrderUseCase;
import com.pizzaco.order.application.port.in.UpdateOrderStatusUseCase;
import com.pizzaco.order.domain.model.OrderId;
import com.pizzaco.order.domain.model.PizzaOrder;
import com.pizzaco.order.infrastructure.adapter.in.rest.dto.OrderResponse;
import com.pizzaco.order.infrastructure.adapter.in.rest.dto.PlaceOrderRequest;
import com.pizzaco.order.infrastructure.adapter.in.rest.mapper.OrderRestMapper;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Inbound Adapter — REST Controller
 *
 * <p>"Inbound Adapter: Converts a web request into a command the Core understands, like a
 * PizzaWebController."
 *
 * <p>This adapter depends ONLY on Inbound Ports (use case interfaces), never on the Application
 * Service directly. This is the "Plug" that fits into the "Socket".
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final PlaceOrderUseCase placeOrderUseCase;
  private final GetOrderUseCase getOrderUseCase;
  private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
  private final OrderRestMapper mapper;

  /** POST /api/orders — Place a new pizza order. */
  @PostMapping
  public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
    var command = mapper.toCommand(request);
    PizzaOrder order = placeOrderUseCase.placeOrder(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(order));
  }

  /** GET /api/orders/{id} — Retrieve an order by its ID. */
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
    PizzaOrder order = getOrderUseCase.getOrder(OrderId.of(id));
    return ResponseEntity.ok(mapper.toResponse(order));
  }

  /** PATCH /api/orders/{id}/status — Advance an order to the next lifecycle status. */
  @PatchMapping("/{id}/status")
  public ResponseEntity<OrderResponse> advanceStatus(@PathVariable UUID id) {
    PizzaOrder order = updateOrderStatusUseCase.advanceOrderStatus(OrderId.of(id));
    return ResponseEntity.ok(mapper.toResponse(order));
  }
}
