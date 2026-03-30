package com.pizzaco.order.domain.exception;

/**
 * Thrown when a pizza violates a business rule. e.g., a Hawaiian pizza that does not include
 * pineapple.
 */
public class InvalidPizzaException extends DomainException {

  private static final long serialVersionUID = 1L;

  public InvalidPizzaException(String message) {
    super(message);
  }
}
