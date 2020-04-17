package com.codeages.framework.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@Component
@Slf4j
public class MeterConfig implements MeterRegistryCustomizer {

    @Override
    public void customize(MeterRegistry registry) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            log.info("设置metrics实例id为ip:" + hostAddress);
            registry.config().commonTags("instance-id", hostAddress);
        } catch (UnknownHostException e) {
            String uuid = UUID.randomUUID().toString();
            registry.config().commonTags("instance-id", uuid);
            log.error("获取实例ip失败，设置实例id为uuid:" + uuid, e);
        }
    }
}