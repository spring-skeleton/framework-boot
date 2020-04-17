package com.codeages.framework.monitor;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
@Data
public class HealthMetrics implements MeterBinder {

    @Autowired
    private HealthEndpoint healthEndpoint;

    /**
     * 100  up
     * 0  down
     * 0 unknown
     */
    private Integer health = 100;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("health", () -> {
            setHealthStatus();
            return health;
        })
                .register(registry);
    }

    private void setHealthStatus() {
        HealthComponent health = healthEndpoint.health();
        if (health != null) {
            Status status = health.getStatus();
            switch (status.getCode()) {
                case "UP": {
                    this.health = 100;
                    break;
                }
                case "DOWN":
                    ;
                case "UNKNOWN":
                    ;
                default: {
                    this.health = 0;
                    break;
                }

            }
        }

    }
}
