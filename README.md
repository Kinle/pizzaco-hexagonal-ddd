# Pizzaco Order Service - DDD + Hexagonal Architecture Showcase

This project demonstrates a strict Domain-Driven Design and Hexagonal Architecture implementation for pizza ordering from placement to delivery.

## Architectural rules applied

- Core is pure Java (no Spring annotations in core domain/application).
- Use cases and ports live in core.
- Web, persistence, and external integrations are infrastructure adapters.
- Aggregate invariants and state transitions are enforced inside domain model.
- Infrastructure wiring is centralized in config.

## Package structure

- core/domain/model: aggregate root and value objects
- core/domain/service: domain services for pricing and validation
- core/domain/store: repository port
- core/application/usecase: input ports (use case contracts)
- core/application/port: output ports (driven interfaces)
- core/application/service: use case implementations
- infrastructure/web: REST controller and DTOs
- infrastructure/persistence: JPA entities and repository adapter
- infrastructure/integration: payment, eta, notification adapters
- infrastructure/config: Spring bean wiring

## End-to-end flow

1. Place order via POST /orders.
2. PlaceOrderUseCase validates, estimates ETA, calculates total, processes payment, confirms order, persists, notifies customer.
3. Progress lifecycle via POST /orders/{id}/progress.
4. ProgressOrderUseCase enforces valid transitions:
   - Confirmed -> Baking -> ReadyForDelivery -> OutForDelivery -> Delivered
5. Retrieve status via GET /orders/{id}.

## API examples

Place order:

curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{
  "customerId":"customer-1",
  "customerContact":"customer1@example.com",
  "deliveryAddress":"221B Baker Street",
  "deliveryDistanceMiles":3.5,
  "items":[
    {
      "size":"Large",
      "crustType":"Thin",
      "priceAtPurchase":16.50,
      "quantity":1,
      "toppings":[
        {"name":"Mushroom","extraCost":1.00}
      ]
    }
  ]
}'

Progress to baking:

curl -X POST http://localhost:8080/orders/{orderId}/progress -H "Content-Type: application/json" -d '{"targetStatus":"Baking"}'

Progress to ready for delivery:

curl -X POST http://localhost:8080/orders/{orderId}/progress -H "Content-Type: application/json" -d '{"targetStatus":"ReadyForDelivery"}'

Progress to out for delivery:

curl -X POST http://localhost:8080/orders/{orderId}/progress -H "Content-Type: application/json" -d '{"targetStatus":"OutForDelivery"}'

Progress to delivered:

curl -X POST http://localhost:8080/orders/{orderId}/progress -H "Content-Type: application/json" -d '{"targetStatus":"Delivered"}'

Fetch order:

curl http://localhost:8080/orders/{orderId}

## Tests included

- Domain tests: aggregate transitions and invariant checks
- Application tests: use case orchestration and payment failure path
- Integration tests: REST flow from order placement to delivered state
- Architecture tests: enforce that core does not depend on infrastructure or Spring

## Notes

- The progress endpoint is intended for demo progression. In production, status updates should come from a kitchen and delivery bounded context (event-driven integration).
- Outbound adapters are currently simulated for payment, ETA, and notifications.
