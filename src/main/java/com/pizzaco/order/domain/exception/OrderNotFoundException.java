package com.pizzaco.order.domain.exception;

/** Thrown when a requested order cannot be found. */
public class OrderNotFoundException extends DomainException {

  private static final long serialVersionUID = 1L;

  public OrderNotFoundException(String message) {
    super(message);
  }
}
