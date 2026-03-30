package com.pizzaco.order.domain.exception;

/**
 * Thrown when an illegal order status transition is attempted. e.g., trying to go to
 * OUT_FOR_DELIVERY before the order is BAKED.
 */
public class InvalidOrderStateException extends DomainException {

  private static final long serialVersionUID = 1L;

  public InvalidOrderStateException(String message) {
    super(message);
  }
}
