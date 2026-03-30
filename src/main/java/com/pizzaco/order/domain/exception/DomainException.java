package com.pizzaco.order.domain.exception;

/** Base exception for all domain-level rule violations. */
public class DomainException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DomainException(String message) {
    super(message);
  }
}
