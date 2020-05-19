package com.codeages.framework.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.stereotype.Component;

import java.net.InetAddress;


@Component
@Slf4j
public class MeterConfig implements MeterRegistryCustomizer {

    @Value("${server.port:8080}")
    private String port;


    @SneakyThrows
    public void customize(MeterRegistry registry) {
        String hostName = InetAddress.getLocalHost().getHostName();
        String instanceId = hostName+ "_" + port;
        log.info("设置metrics实例id:" + instanceId);
        registry.config().commonTags("instance-id", instanceId);
    }
}