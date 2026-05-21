package com.pizzaco.order.infrastructure.integration;

import com.pizzaco.order.core.application.port.NotificationPort;
import com.pizzaco.order.core.domain.model.CustomerContact;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirebaseNotificationAdapter implements NotificationPort {
    @Override
    public NotificationResult notifyCustomer(CustomerContact customerContact, String message) {
        log.info(
                "Simulating notification to '{}' with message: {}",
                customerContact.value(),
                message);
        return new NotificationResult(true, "msg-" + UUID.randomUUID(), Instant.now());
    }
}
