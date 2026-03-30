package com.pizzaco.order.infrastructure.adapter.out.payment;

import com.pizzaco.order.application.port.out.PaymentPort;
import com.pizzaco.order.domain.model.Money;
import com.pizzaco.order.domain.model.OrderId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Outbound Adapter — Simulated Payment Processor (logging only)
 *
 * <p>"Outbound Adapter: Implements the contract using a specific tool, like a
 * StripePaymentAdapter."
 *
 * <p>This demonstrates the Plug-and-Play advantage: "You can swap databases or SMS providers in an
 * afternoon without touching your core logic."
 *
 * <p>To switch to a real payment provider (Stripe, PayPal), create a new adapter class that
 * implements PaymentPort and swap it via Spring configuration — zero changes to the domain or
 * application layers.
 */
@Slf4j
@Component
public class LoggingPaymentAdapter implements PaymentPort {

  @Override
  public boolean charge(OrderId orderId, Money amount) {
    log.info("══════════════════════════════════════════════════════════");
    log.info("  [PAYMENT] Processing payment...");
    log.info("  [PAYMENT] Order ID : {}", orderId);
    log.info("  [PAYMENT] Amount   : {}", amount);
    log.info("  [PAYMENT] Provider : Simulated (Logging Only)");
    log.info("  [PAYMENT] Status   : ✅ CHARGED SUCCESSFULLY");
    log.info("══════════════════════════════════════════════════════════");

    // In a real adapter, this would call Stripe SDK:
    //   Stripe.apiKey = "sk_test_...";
    //   PaymentIntent intent = PaymentIntent.create(params);
    //   return intent.getStatus().equals("succeeded");

    return true;
  }
}
