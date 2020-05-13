package com.codeages.framework.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Component
@Slf4j
public class MeterConfig implements MeterRegistryCustomizer {

    @Value("${server.port:8080}")
    private String port;

    @SneakyThrows
    @Override
    public void customize(MeterRegistry registry) {
        String[] hostAddress = getLocalIPs();
        String instanceId = hostAddress[0] + "_" + port;
        log.info("设置metrics实例id:" + instanceId);
        registry.config().commonTags("instance-id", instanceId);
    }

    public String[] getLocalIPs() throws SocketException {
        List<String> list = new ArrayList<>();
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface intf = enumeration.nextElement();
            if (intf.isLoopback() || intf.isVirtual()) {
                continue;
            }
            Enumeration<InetAddress> inets = intf.getInetAddresses();
            while (inets.hasMoreElements()) {
                InetAddress addr = inets.nextElement();
                if (addr.isLoopbackAddress() || !addr.isSiteLocalAddress() || addr.isAnyLocalAddress()) {
                    continue;
                }
                list.add(addr.getHostAddress());
            }
        }
        return list.toArray(new String[0]);
    }
}