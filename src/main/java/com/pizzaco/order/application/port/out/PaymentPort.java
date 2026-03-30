package com.pizzaco.order.application.port.out;

import com.pizzaco.order.domain.model.Money;
import com.pizzaco.order.domain.model.OrderId;

/**
 * Outbound Port (Driven Port) — "PaymentProcessor"
 *
 * <p>"Defines what the app needs from the world, like a PaymentProcessor." The actual
 * implementation (Stripe, PayPal, or a logging fake) is an Adapter. Swapping payment providers
 * should never break core logic — Plug-and-Play.
 */
public interface PaymentPort {

  /**
   * Charges the given amount for the specified order.
   *
   * @param orderId the order being charged
   * @param amount the total amount to charge
   * @return true if the payment was successful
   */
  boolean charge(OrderId orderId, Money amount);
}
