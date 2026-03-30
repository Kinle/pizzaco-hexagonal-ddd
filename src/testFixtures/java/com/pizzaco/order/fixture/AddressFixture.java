package com.pizzaco.order.fixture;

import com.pizzaco.order.domain.model.Address;

/**
 * Reusable Address constants for tests. Eliminates duplicate address creation scattered across test
 * classes.
 */
public final class AddressFixture {

  /**
   * Pizza shop address — NYC (40.7128, -74.0060). Used as the shop origin for delivery fee
   * calculations.
   */
  public static final Address SHOP = Address.of("123 Pizza St", "NYC", "10001", 40.7128, -74.0060);

  /** Default NYC address for general-purpose tests. */
  public static final Address NYC_DEFAULT = Address.of("1 Main St", "NYC", "10001", 40.71, -74.00);

  /** Very close to the shop — triggers minimum delivery fee. */
  public static final Address NEARBY =
      Address.of("124 Pizza St", "NYC", "10001", 40.7130, -74.0062);

  /** Boston — about 306 km from NYC, triggers maximum delivery fee. */
  public static final Address BOSTON =
      Address.of("1 Main St", "Boston", "02101", 42.3601, -71.0589);

  /** Newark, NJ — about 8 km from NYC, mid-range delivery fee. */
  public static final Address NEWARK =
      Address.of("1 Broad St", "Newark", "07102", 40.7357, -74.1724);

  /** Typical customer delivery address in NYC. */
  public static final Address CUSTOMER =
      Address.of("456 Oak Ave", "NYC", "10002", 40.7200, -74.0000);

  /** Another customer address for E2E / integration tests. */
  public static final Address E2E_CUSTOMER =
      Address.of("789 Elm St", "NYC", "10003", 40.7300, -74.0000);

  private AddressFixture() {}
}
