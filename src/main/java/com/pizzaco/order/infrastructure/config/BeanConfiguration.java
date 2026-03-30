package com.pizzaco.order.infrastructure.config;

import com.pizzaco.order.application.port.out.LoadOrderPort;
import com.pizzaco.order.application.port.out.PaymentPort;
import com.pizzaco.order.application.port.out.SaveOrderPort;
import com.pizzaco.order.application.service.OrderApplicationService;
import com.pizzaco.order.domain.model.Address;
import com.pizzaco.order.domain.service.DeliveryFeeCalculator;
import com.pizzaco.order.domain.service.PizzaPriceCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for wiring the core application and domain beans. This keeps framework
 * annotations out of the Application and Domain layers.
 */
@Configuration
public class BeanConfiguration {

  /**
   * The shop's physical address — used by the DeliveryFeeCalculator. In production this would come
   * from a config property or database.
   */
  private static final Address SHOP_ADDRESS =
      Address.of(
          "123 Pizza Street", "Pizza City", "10001", 40.7128, -74.0060 // New York City coordinates
          );

  @Bean
  public DeliveryFeeCalculator deliveryFeeCalculator() {
    return new DeliveryFeeCalculator(SHOP_ADDRESS);
  }

  @Bean
  public PizzaPriceCalculator pizzaPriceCalculator() {
    // Toggle to true during holiday season — domain logic change only, zero infrastructure changes.
    // "Whether you are changing your discount logic for the holiday season,
    //  your codebase remains a clean, testable representation of your business."
    return new PizzaPriceCalculator(false);
  }

  @Bean
  public OrderApplicationService orderApplicationService(
      LoadOrderPort loadOrderPort,
      SaveOrderPort saveOrderPort,
      PaymentPort paymentPort,
      DeliveryFeeCalculator deliveryFeeCalculator,
      PizzaPriceCalculator pizzaPriceCalculator) {
    return new OrderApplicationService(
        loadOrderPort, saveOrderPort, paymentPort, deliveryFeeCalculator, pizzaPriceCalculator);
  }
}
