package com.pizzaco.order.domain.model;

/**
 * Value Object representing a delivery address. Immutable — includes latitude/longitude for
 * distance-based delivery fee calculation.
 */
public record Address(
    String street, String city, String zipCode, double latitude, double longitude) {

  public Address {
    if (street == null || street.isBlank()) {
      throw new IllegalArgumentException("Street cannot be empty");
    }
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("City cannot be empty");
    }
    if (zipCode == null || zipCode.isBlank()) {
      throw new IllegalArgumentException("Zip code cannot be empty");
    }
  }

  public static Address of(
      String street, String city, String zipCode, double latitude, double longitude) {
    return new Address(street, city, zipCode, latitude, longitude);
  }

  @Override
  public String toString() {
    return street + ", " + city + " " + zipCode;
  }
}
