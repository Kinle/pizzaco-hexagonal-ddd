package com.pizzaco.order.core.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PizzaOrder {
    private final OrderId id;
    private final OrderStatus status;
    private final List<PizzaLineItem> items;
    private final DeliveryAddress deliveryAddress;
    private final CustomerContact customerContact;
    private final Money subtotal;
    private final Money deliveryFee;
    private final Money total;
    private final EtaMinutes etaMinutes;
    private final PaymentReference paymentReference;
    private final Instant placedAt;

    private PizzaOrder(
            OrderId id,
            OrderStatus status,
            List<PizzaLineItem> items,
            DeliveryAddress deliveryAddress,
            CustomerContact customerContact,
            Money subtotal,
            Money deliveryFee,
            Money total,
            EtaMinutes etaMinutes,
            PaymentReference paymentReference,
            Instant placedAt) {
        this.id = Objects.requireNonNull(id, "Order id is required");
        this.status = Objects.requireNonNull(status, "Order status is required");
        this.items = List.copyOf(Objects.requireNonNull(items, "Order items are required"));
        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        this.deliveryAddress =
                Objects.requireNonNull(deliveryAddress, "Delivery address is required");
        this.customerContact =
                Objects.requireNonNull(customerContact, "Customer contact is required");
        this.subtotal = Objects.requireNonNull(subtotal, "Subtotal is required");
        this.deliveryFee = Objects.requireNonNull(deliveryFee, "Delivery fee is required");
        this.total = Objects.requireNonNull(total, "Total is required");
        this.etaMinutes = Objects.requireNonNull(etaMinutes, "ETA is required");
        this.paymentReference = paymentReference;
        this.placedAt = Objects.requireNonNull(placedAt, "Placed at timestamp is required");
    }

    public static PizzaOrder submitted(
            List<PizzaLineItem> items,
            DeliveryAddress deliveryAddress,
            CustomerContact customerContact,
            Money subtotal,
            Money deliveryFee,
            Money total,
            EtaMinutes etaMinutes,
            Instant placedAt) {
        return new PizzaOrder(
                new OrderId(UUID.randomUUID()),
                OrderStatus.Submitted,
                items,
                deliveryAddress,
                customerContact,
                subtotal,
                deliveryFee,
                total,
                etaMinutes,
                null,
                placedAt);
    }

    public static PizzaOrder rehydrate(
            OrderId id,
            OrderStatus status,
            List<PizzaLineItem> items,
            DeliveryAddress deliveryAddress,
            CustomerContact customerContact,
            Money subtotal,
            Money deliveryFee,
            Money total,
            EtaMinutes etaMinutes,
            PaymentReference paymentReference,
            Instant placedAt) {
        return new PizzaOrder(
                id,
                status,
                items,
                deliveryAddress,
                customerContact,
                subtotal,
                deliveryFee,
                total,
                etaMinutes,
                paymentReference,
                placedAt);
    }

    public PizzaOrder confirm(PaymentReference confirmedPaymentReference) {
        requireCurrentStatus(OrderStatus.Submitted);
        return new PizzaOrder(
                id,
                OrderStatus.Confirmed,
                items,
                deliveryAddress,
                customerContact,
                subtotal,
                deliveryFee,
                total,
                etaMinutes,
                Objects.requireNonNull(confirmedPaymentReference, "Payment reference is required"),
                placedAt);
    }

    public PizzaOrder startBaking() {
        requireCurrentStatus(OrderStatus.Confirmed);
        return withStatus(OrderStatus.Baking);
    }

    public PizzaOrder markReadyForDelivery() {
        requireCurrentStatus(OrderStatus.Baking);
        return withStatus(OrderStatus.ReadyForDelivery);
    }

    public PizzaOrder markOutForDelivery() {
        requireCurrentStatus(OrderStatus.ReadyForDelivery);
        return withStatus(OrderStatus.OutForDelivery);
    }

    public PizzaOrder markDelivered() {
        requireCurrentStatus(OrderStatus.OutForDelivery);
        return withStatus(OrderStatus.Delivered);
    }

    public PizzaOrder cancel() {
        if (status == OrderStatus.Delivered) {
            throw new IllegalStateException("Delivered orders cannot be cancelled");
        }
        if (status == OrderStatus.Cancelled) {
            return this;
        }
        return withStatus(OrderStatus.Cancelled);
    }

    private void requireCurrentStatus(OrderStatus expectedStatus) {
        if (status != expectedStatus) {
            throw new IllegalStateException(
                    "Invalid state transition. Expected "
                            + expectedStatus
                            + " but found "
                            + status);
        }
    }

    private PizzaOrder withStatus(OrderStatus newStatus) {
        return new PizzaOrder(
                id,
                newStatus,
                items,
                deliveryAddress,
                customerContact,
                subtotal,
                deliveryFee,
                total,
                etaMinutes,
                paymentReference,
                placedAt);
    }

    public OrderId getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<PizzaLineItem> getItems() {
        return items;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public CustomerContact getCustomerContact() {
        return customerContact;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    public Money getDeliveryFee() {
        return deliveryFee;
    }

    public Money getTotal() {
        return total;
    }

    public EtaMinutes getEtaMinutes() {
        return etaMinutes;
    }

    public PaymentReference getPaymentReference() {
        return paymentReference;
    }

    public Instant getPlacedAt() {
        return placedAt;
    }
}
