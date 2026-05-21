package com.pizzaco.order.core.application.port;

import com.pizzaco.order.core.domain.model.CustomerContact;
import java.time.Instant;

public interface NotificationPort {
    NotificationResult notifyCustomer(CustomerContact customerContact, String message);

    record NotificationResult(boolean sent, String messageId, Instant sentAt) {}
}
