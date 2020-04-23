package com.codeages.framework.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@Component
@Slf4j
public class MeterConfig implements MeterRegistryCustomizer {

    @Value("${server.port:8080}")
    private String port;

    @Override
    public void customize(MeterRegistry registry) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String instanceId = hostAddress + "_" + port;
            log.info("设置metrics实例id:" + instanceId);
            registry.config().commonTags("instance-id", instanceId);
        } catch (UnknownHostException e) {
            String uuid = UUID.randomUUID().toString();
            registry.config().commonTags("instance-id", uuid);
            log.error("获取实例ip失败，设置实例id为uuid:" + uuid, e);
        }
    }
}