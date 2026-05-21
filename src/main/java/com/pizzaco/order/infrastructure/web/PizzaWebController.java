package com.pizzaco.order.infrastructure.web;

import com.pizzaco.order.core.application.usecase.GetOrderUseCase;
import com.pizzaco.order.core.application.usecase.PlaceOrderUseCase;
import com.pizzaco.order.core.application.usecase.ProgressOrderUseCase;
import com.pizzaco.order.core.domain.model.CrustType;
import com.pizzaco.order.core.domain.model.CustomerContact;
import com.pizzaco.order.core.domain.model.DeliveryAddress;
import com.pizzaco.order.core.domain.model.Money;
import com.pizzaco.order.core.domain.model.OrderId;
import com.pizzaco.order.core.domain.model.OrderStatus;
import com.pizzaco.order.core.domain.model.PizzaLineItem;
import com.pizzaco.order.core.domain.model.PizzaSize;
import com.pizzaco.order.core.domain.model.Topping;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class PizzaWebController {
    private static final DeliveryAddress STORE_ADDRESS =
            new DeliveryAddress("Pizzaco Central Kitchen");

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ProgressOrderUseCase progressOrderUseCase;

    public PizzaWebController(
            PlaceOrderUseCase placeOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            ProgressOrderUseCase progressOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.progressOrderUseCase = progressOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @Valid @RequestBody OrderRequestDTO request) {
        var result =
                placeOrderUseCase.placeOrder(
                        new PlaceOrderUseCase.PlaceOrderCommand(
                                request.customerId(),
                                new CustomerContact(request.customerContact()),
                                STORE_ADDRESS,
                                new DeliveryAddress(request.deliveryAddress()),
                                request.deliveryDistanceMiles(),
                                toDomainItems(request.items())));

        var orderView = getOrderUseCase.getOrder(result.orderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(orderView));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable UUID orderId) {
        var view = getOrderUseCase.getOrder(new OrderId(orderId));
        return ResponseEntity.ok(toResponse(view));
    }

    @PostMapping("/{orderId}/progress")
    public ResponseEntity<OrderResponseDTO> progressOrder(
            @PathVariable UUID orderId, @Valid @RequestBody ProgressOrderRequestDTO request) {
        progressOrderUseCase.progressOrder(
                new ProgressOrderUseCase.ProgressOrderCommand(
                        new OrderId(orderId), OrderStatus.valueOf(request.targetStatus())));

        var view = getOrderUseCase.getOrder(new OrderId(orderId));
        return ResponseEntity.ok(toResponse(view));
    }

    private static List<PizzaLineItem> toDomainItems(List<OrderRequestDTO.ItemDTO> requestItems) {
        return requestItems.stream()
                .map(
                        item ->
                                new PizzaLineItem(
                                        new PizzaSize(item.size()),
                                        new CrustType(item.crustType()),
                                        item.toppings().stream()
                                                .map(
                                                        t ->
                                                                new Topping(
                                                                        t.name(),
                                                                        new Money(t.extraCost())))
                                                .toList(),
                                        new Money(item.priceAtPurchase()),
                                        item.quantity()))
                .toList();
    }

    private static OrderResponseDTO toResponse(GetOrderUseCase.OrderView orderView) {
        return new OrderResponseDTO(
                orderView.orderId().value(),
                orderView.status().name(),
                orderView.total(),
                orderView.etaMinutes(),
                orderView.paymentReference() == null ? null : orderView.paymentReference().value(),
                orderView.deliveryAddress(),
                orderView.placedAt());
    }
}
