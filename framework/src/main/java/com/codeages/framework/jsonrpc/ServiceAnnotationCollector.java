package com.codeages.framework.jsonrpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ServiceAnnotationCollector extends BaseServiceCollector {

    private String rpcPrefix = "rpc.";

    @Override
    public void exec(ClassMetadata classMetadata, AnnotationMetadata annotationMetadata) throws Exception {
        String annotation = JsonRpcService.class.getName();
        if (annotationMetadata.isAnnotated(annotation)) {
            String className = classMetadata.getClassName();
            Class clazz = Class.forName(className);
            Map<String, Object> map = applicationContext.getBeansOfType(clazz);
            map.entrySet().forEach(entry -> {
                Object rpcService = entry.getValue();
                Map<String, Object> annotationAttrs = annotationMetadata.getAnnotationAttributes(annotation);

                String rpcName = annotationAttrs.get("value").toString().trim();
                if ("".equals(rpcName)) {
                    rpcName = clazz.getSimpleName();
                }

                String beanName = rpcPrefix + rpcName;
                if (!defaultListableBeanFactory.containsBean(beanName)) {
                    log.info("Found JSON-RPC-Servicer to proxy [{}], bean name: {}.", className, beanName);
                    defaultListableBeanFactory.registerSingleton(beanName, rpcService);
                }
            });
        }
    }
}
