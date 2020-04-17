package com.codeages.framework.jsonrpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ServiceCollector extends BaseServiceCollector  {
    @Override
    public void exec(ClassMetadata classMetadata, AnnotationMetadata annotationMetadata) throws Exception {
        Map<String, RpcService> map = defaultListableBeanFactory.getBeansOfType(RpcService.class);
        map.entrySet().forEach(entry -> {
            Object rpcService = entry.getValue();
            String beanName = "rpc."+rpcService.getClass().getSimpleName();
            if (!defaultListableBeanFactory.containsBean(beanName)) {
                log.info("Found JSON-RPC-Servicer to proxy [{}].", rpcService.getClass().getName());
                defaultListableBeanFactory.registerSingleton(beanName, rpcService);
            }
        });
    }
}
