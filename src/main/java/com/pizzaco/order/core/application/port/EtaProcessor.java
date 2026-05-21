package com.pizzaco.order.core.application.port;

import com.pizzaco.order.core.domain.model.DeliveryAddress;
import com.pizzaco.order.core.domain.model.EtaMinutes;

public interface EtaProcessor {
    EtaMinutes estimateMinutes(DeliveryAddress addressFrom, DeliveryAddress addressTo);
}
