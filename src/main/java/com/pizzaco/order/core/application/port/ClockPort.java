package com.pizzaco.order.core.application.port;

import java.time.Instant;

public interface ClockPort {
    Instant now();
}
