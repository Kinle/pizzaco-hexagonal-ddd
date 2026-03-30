package com.pizzaco.order.domain.service;

import com.pizzaco.order.domain.model.Address;
import com.pizzaco.order.domain.model.Money;

/**
 * Domain Service — "The Head Chef"
 *
 * <p>Decision Matrix: "Does it compare multiple Orders or Drivers? → Domain Service" A
 * DeliveryFeeCalculator that checks distance is too complex for a single Order object. This logic
 * belongs in a Domain Service — a specialist that understands "Pizza Rules" but doesn't belong to a
 * single entity.
 *
 * <p>Uses the Haversine formula to compute distance between the shop and the delivery address. Fee:
 * $1.00 per km, minimum $3.00, maximum $15.00.
 *
 * <p>Pure domain logic — no database calls, no framework annotations.
 */
public class DeliveryFeeCalculator {

  private static final double RATE_PER_KM = 1.00;
  private static final double MIN_FEE = 3.00;
  private static final double MAX_FEE = 15.00;
  private static final double EARTH_RADIUS_KM = 6371.0;

  private final Address shopAddress;

  public DeliveryFeeCalculator(Address shopAddress) {
    this.shopAddress = shopAddress;
  }

  /**
   * Calculates the delivery fee based on the Haversine distance between the shop and the delivery
   * address.
   */
  public Money calculate(Address deliveryAddress) {
    if (deliveryAddress == null) {
      throw new IllegalArgumentException("Delivery address cannot be null");
    }
    double distanceKm =
        haversine(
            shopAddress.latitude(), shopAddress.longitude(),
            deliveryAddress.latitude(), deliveryAddress.longitude());

    double fee = distanceKm * RATE_PER_KM;
    fee = Math.max(fee, MIN_FEE);
    fee = Math.min(fee, MAX_FEE);

    return Money.of(fee);
  }

  /** Haversine formula: calculates the great-circle distance between two points on Earth. */
  private double haversine(double lat1, double lon1, double lat2, double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS_KM * c;
  }
}
