# 🍕 PizzaCo — Hexagonal Architecture & DDD Sample

A fully working **Pizza Order** system built with **Spring Boot 4**, demonstrating **Hexagonal Architecture** (Ports &
Adapters) and **Domain-Driven Design (DDD)** principles.

> Companion code for the blog post: [The Pizza Fortress and the Bridge – Mastering Ports & Adapters](docs/blog.md)

---

## Architecture Overview

```
┌──────────────────────────────────────────────────────────────────┐
│                     INFRASTRUCTURE LAYER                         │
│                                                                  │
│  ┌───────────────────┐                  ┌──────────────────────┐ │
│  │  Inbound Adapter  │                  │  Outbound Adapters   │ │
│  │                   │                  │                      │ │
│  │  REST Controller  │                  │  JPA Persistence     │ │
│  │  (OrderController)│                  │  (H2 Database)       │ │
│  │                   │                  │                      │ │
│  │  Converts HTTP    │                  │  Logging Payment     │ │
│  │  → Commands       │                  │  (Simulated Stripe)  │ │
│  └────────┬──────────┘                  └──────────▲───────────┘ │
│           │                                        │             │
├───────────┼────────────────────────────────────────┼─────────────┤
│           │        APPLICATION LAYER               │             │
│           ▼                                        │             │
│  ┌──────────────────┐    Inbound        ┌─────────────────────┐  │
│  │  Inbound Ports   │    Ports          │  Outbound Ports     │  │
│  │                  │◄───────────────── │                     │  │
│  │ PlaceOrderUseCase│                   │ LoadOrderPort       │  │
│  │ GetOrderUseCase  │    Application    │ SaveOrderPort       │  │
│  │ UpdateOrderStatus│──    Service  ──  │ PaymentPort         │  │
│  │     UseCase      │  (Orchestrator)   │                     │  │
│  └──────────────────┘                   └─────────────────────┘  │
│                                                                  │
├──────────────────────────────────────────────────────────────────┤
│                       DOMAIN LAYER                               │
│                    (Pure Java + Lombok)                          │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐     │
│  │  Entities & Aggregate Root                              │     │
│  │  • PizzaOrder (Aggregate Root — gateway to all children)│     │
│  │  • Pizza (validates Hawaiian invariant)                 │     │
│  ├─────────────────────────────────────────────────────────┤     │
│  │  Value Objects (Immutable)                              │     │
│  │  • Money • Topping • Address • OrderId • PizzaType      │     │
│  ├─────────────────────────────────────────────────────────┤     │
│  │  Domain Services                                        │     │
│  │  • DeliveryFeeCalculator (Haversine distance → fee)     │     │
│  │  • PizzaPriceCalculator                                 │     │
│  └─────────────────────────────────────────────────────────┘     │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Blog Concepts → Code Mapping

| Blog Concept                     | Code Location                                        | Description                                                                     |
|----------------------------------|------------------------------------------------------|---------------------------------------------------------------------------------|
| **Hexagonal Architecture**       | Package structure                                    | `domain/` → `application/` → `infrastructure/`                                  |
| **The Inside (Core)**            | `domain/` + `application/`                           | Pure Java, zero framework imports                                               |
| **The Outside (Infrastructure)** | `infrastructure/`                                    | Spring, JPA, REST, H2                                                           |
| **Golden Rule**                  | `HexagonalArchitectureTest`                          | ArchUnit enforces: domain → no deps on application/infra                        |
| **Inbound Port (Driving)**       | `application/port/in/`                               | `PlaceOrderUseCase`, `GetOrderUseCase`, `UpdateOrderStatusUseCase`              |
| **Outbound Port (Driven)**       | `application/port/out/`                              | `LoadOrderPort`, `SaveOrderPort`, `PaymentPort`                                 |
| **Inbound Adapter**              | `infrastructure/adapter/in/rest/`                    | `OrderController` — converts HTTP → commands                                    |
| **Outbound Adapter (DB)**        | `infrastructure/adapter/out/persistence/`            | `OrderPersistenceAdapter` — implements repository ports                         |
| **Outbound Adapter (Payment)**   | `infrastructure/adapter/out/payment/`                | `LoggingPaymentAdapter` — simulated Stripe                                      |
| **Entity**                       | `domain/model/PizzaOrder` (class)                    | Identity by ID — `equals`/`hashCode` on `OrderId` only                          |
| **Value Object**                 | `domain/model/Money`, `Topping`, `Address` (records) | Immutable, equality by all attributes (record semantics)                        |
| **Entity vs Value Object**       | `PizzaOrderTest`                                     | Tests prove: same ID = same entity; same attributes = same VO                   |
| **Aggregate Root**               | `domain/model/PizzaOrder`                            | Gateway to all children; enforces invariants                                    |
| **Business Invariant**           | `Pizza` constructor, `PizzaOrder.advanceStatus()`    | Hawaiian must have pineapple; status lifecycle guards                           |
| **Domain Service**               | `domain/service/DeliveryFeeCalculator`               | Distance-based fee — logic spanning beyond a single entity                      |
| **Domain Service**               | `domain/service/PizzaPriceCalculator`                | Discounts (buy 3 cheapest free, holiday 10% off)                                |
| **Apply Discounts**              | `PizzaPriceCalculator`                               | "telling the Domain to apply discounts"                                         |
| **Holiday Season**               | `BeanConfiguration` → `PizzaPriceCalculator(true)`   | "changing your discount logic for the holiday season"                           |
| **Application Service**          | `application/service/OrderApplicationService`        | "The Kitchen Manager" — orchestrates, doesn't cook                              |
| **Decision Matrix**              | See code comments                                    | DB/Stripe → App Service; multi-entity → Domain Service; self-contained → Entity |
| **Plug-and-Play**                | `LoggingPaymentAdapter`                              | Swap to real Stripe by creating a new adapter — zero core changes               |
| **Testable**                     | `OrderApplicationServiceTest`                        | Full checkout tested with mocked ports, no real DB or payment                   |

---

## Prerequisites

- **Java 25** (or compatible JDK)
- **Gradle** (wrapper included — no install needed)

---

## Build & Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application starts on **http://localhost:8080**.

### H2 Database Console

Access the in-memory database at: **http://localhost:8080/h2-console**

| Setting  | Value                                                   |
|----------|---------------------------------------------------------|
| JDBC URL | `jdbc:h2:mem:pizzaco;MODE=PostgreSQL;DB_CLOSE_DELAY=-1` |
| Username | `sa`                                                    |
| Password | *(empty)*                                               |

---

## API Endpoints & Sample Requests

### 1. Place an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Wonderland",
    "street": "789 Elm St",
    "city": "NYC",
    "zipCode": "10003",
    "latitude": 40.73,
    "longitude": -74.00,
    "pizzas": [
      {
        "type": "MARGHERITA",
        "toppings": ["Extra Cheese", "Mushroom"],
        "quantity": 2
      },
      {
        "type": "HAWAIIAN",
        "toppings": ["Pineapple", "Bacon"],
        "quantity": 1
      }
    ]
  }'
```

**Response (201 Created):**

```json
{
  "orderId": "a1b2c3d4-...",
  "customerName": "Alice Wonderland",
  "status": "PLACED",
  "deliveryAddress": "789 Elm St, NYC 10003",
  "pizzas": [
    {
      "type": "MARGHERITA",
      "toppings": [
        "Extra Cheese",
        "Mushroom"
      ],
      "quantity": 2,
      "price": 21.50
    },
    {
      "type": "HAWAIIAN",
      "toppings": [
        "Pineapple",
        "Bacon"
      ],
      "quantity": 1,
      "price": 14.50
    }
  ],
  "deliveryFee": 3.00,
  "totalPrice": 39.00,
  "createdAt": "2026-04-01T12:00:00"
}
```

### 2. Get an Order

```bash
curl http://localhost:8080/api/orders/{orderId}
```

### 3. Advance Order Status

```bash
curl -X PATCH http://localhost:8080/api/orders/{orderId}/status
```

Call this repeatedly to walk through the lifecycle:
`PLACED` → `PREPARING` → `BAKED` → `OUT_FOR_DELIVERY` → `DELIVERED`

### 4. Test Business Invariants

**Hawaiian pizza without pineapple (should fail):**

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Bob",
    "street": "1 Main St",
    "city": "NYC",
    "zipCode": "10001",
    "latitude": 40.71,
    "longitude": -74.00,
    "pizzas": [
      {
        "type": "HAWAIIAN",
        "toppings": ["Extra Cheese"],
        "quantity": 1
      }
    ]
  }'
```

**Response (400 Bad Request):**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "A Hawaiian pizza must always include Pineapple topping"
}
```

---

## Run Tests

```bash
# Run all tests
./gradlew test

# Run only architecture tests
./gradlew test --tests "*HexagonalArchitectureTest"

# Run only domain unit tests
./gradlew test --tests "com.pizzaco.order.domain.*"

# Run integration tests
./gradlew test --tests "*IntegrationTest"
```

### Test Coverage

| Test Class                       | What It Proves                                                                      |
|----------------------------------|-------------------------------------------------------------------------------------|
| `HexagonalArchitectureTest`      | Golden Rule via ArchUnit — domain has no Spring or Jakarta deps                     |
| `MoneyTest`                      | Value Object immutability, arithmetic, negative rejection, record equality          |
| `PizzaTest`                      | Hawaiian invariant, entity price calculation                                        |
| `PizzaOrderTest`                 | Aggregate Root behavior, status lifecycle, Entity identity vs Value Object equality |
| `DeliveryFeeCalculatorTest`      | Domain Service distance-based logic                                                 |
| `PizzaPriceCalculatorTest`       | Domain Service discounts: buy 3 cheapest free, holiday 10% off                      |
| `OrderApplicationServiceTest`    | Full checkout with mocked ports — no real DB/payment                                |
| `OrderControllerIntegrationTest` | E2E through REST → Domain → H2, full lifecycle                                      |

---

## DDD Design Decisions

### `record` vs `class` — Entity vs Value Object Pattern

| DDD Concept      | Java Type | Why                                                                                                                            |
|------------------|-----------|--------------------------------------------------------------------------------------------------------------------------------|
| **Value Object** | `record`  | Immutable by default; `equals`/`hashCode` compare all attributes — *"Extra Cheese doesn't need a unique ID"*                   |
| **Entity**       | `class`   | Mutable state (status, total); `equals`/`hashCode` compare by ID only — *"Even if toppings change, it's still the same order"* |

- `Money`, `Topping`, `Address`, `OrderId`, `Pizza` → **records** (Value Objects)
- `PizzaOrder` → **class** with `equals`/`hashCode` on `OrderId` (Entity / Aggregate Root)

### Discount Rules (Domain Service)

The `PizzaPriceCalculator` domain service demonstrates cross-cutting pricing logic:

- **Buy 3, cheapest free:** If 3+ pizza line items, the cheapest one is free
- **Holiday 10% off:** Toggled via `BeanConfiguration` — flip `false` → `true` during holiday season

To enable holiday discounts, change one line in `BeanConfiguration.java`:

```java
return new PizzaPriceCalculator(true);  // Holiday season!
```

Zero changes to infrastructure, adapters, or REST layer — pure domain change.

---

## Available Pizza Types & Toppings

**Pizza Types:** `MARGHERITA` ($8), `PEPPERONI` ($10), `HAWAIIAN` ($11), `VEGGIE` ($9.50), `CUSTOM` ($7)

**Known Toppings:** `Extra Cheese` (+$1.50), `Pineapple` (+$1), `Pepperoni` (+$2), `Mushroom` (+$1.25), `Olives` (+$1),
`Bacon` (+$2.50), `Onion` (+$0.75)

Any other topping name is treated as a custom topping with a +$1.50 surcharge.

---

## Project Structure

```
src/main/java/com/pizzaco/order/
├── PizzacoOrderApplication.java
├── domain/                          ← Pure Java + Lombok (The Fortress)
│   ├── exception/
│   │   ├── DomainException.java
│   │   ├── InvalidOrderStateException.java
│   │   ├── InvalidPizzaException.java
│   │   └── OrderNotFoundException.java
│   ├── model/
│   │   ├── Address.java             ← Value Object
│   │   ├── Money.java               ← Value Object (rejects negatives)
│   │   ├── OrderId.java             ← Value Object (typed identity)
│   │   ├── OrderStatus.java         ← Lifecycle enum
│   │   ├── Pizza.java               ← Entity (Hawaiian invariant)
│   │   ├── PizzaOrder.java          ← Entity & Aggregate Root
│   │   ├── PizzaType.java           ← Enum with base prices
│   │   └── Topping.java             ← Value Object (immutable)
│   └── service/
│       ├── DeliveryFeeCalculator.java  ← Domain Service (Haversine)
│       └── PizzaPriceCalculator.java   ← Domain Service
├── application/                     ← Orchestration (The Kitchen Manager)
│   ├── port/
│   │   ├── in/
│   │   │   ├── GetOrderUseCase.java         ← Inbound Port
│   │   │   ├── PlaceOrderUseCase.java       ← Inbound Port
│   │   │   ├── UpdateOrderStatusUseCase.java ← Inbound Port
│   │   │   └── command/
│   │   │       ├── PizzaItemCommand.java
│   │   │       └── PlaceOrderCommand.java
│   │   └── out/
│   │       ├── LoadOrderPort.java           ← Outbound Port
│   │       ├── PaymentPort.java             ← Outbound Port
│   │       └── SaveOrderPort.java           ← Outbound Port
│   └── service/
│       └── OrderApplicationService.java     ← Application Service
└── infrastructure/                  ← Adapters (The Plugs)
    ├── config/
    │   └── BeanConfiguration.java
    └── adapter/
        ├── in/rest/
        │   ├── GlobalExceptionHandler.java
        │   ├── OrderController.java         ← Inbound Adapter
        │   ├── dto/
        │   │   ├── OrderResponse.java
        │   │   ├── PizzaItemRequest.java
        │   │   ├── PizzaResponse.java
        │   │   └── PlaceOrderRequest.java
        │   └── mapper/
        │       └── OrderRestMapper.java
        └── out/
            ├── payment/
            │   └── LoggingPaymentAdapter.java  ← Outbound Adapter (simulated)
            └── persistence/
                ├── OrderPersistenceAdapter.java ← Outbound Adapter
                ├── SpringDataOrderRepository.java
                ├── entity/
                │   ├── OrderJpaEntity.java
                │   ├── PizzaJpaEntity.java
                │   └── ToppingEmbeddable.java
                └── mapper/
                    └── OrderPersistenceMapper.java
```

---

## License

This project is a learning resource — use it freely.

