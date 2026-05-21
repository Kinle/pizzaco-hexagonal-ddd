package com.pizzaco.order.infrastructure.integration;

import com.pizzaco.order.core.application.port.EtaProcessor;
import com.pizzaco.order.core.domain.model.DeliveryAddress;
import com.pizzaco.order.core.domain.model.EtaMinutes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GoogleMapsEtaAdapter implements EtaProcessor {
    @Override
    public EtaMinutes estimateMinutes(DeliveryAddress addressFrom, DeliveryAddress addressTo) {
        log.info(
                "Simulating ETA calculation from '{}' to '{}'",
                addressFrom.value(),
                addressTo.value());
        return new EtaMinutes(30);
    }
}
