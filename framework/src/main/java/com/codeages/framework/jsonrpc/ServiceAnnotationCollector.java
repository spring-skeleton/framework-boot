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
            String beanName = clazz.getSimpleName();
            beanName = new StringBuilder().append(Character.toLowerCase(beanName.charAt(0))).append(beanName.substring(1)).toString();
            Object rpcService = applicationContext.getBean(beanName);
            Map<String, Object> annotationAttrs = annotationMetadata.getAnnotationAttributes(annotation);

            String rpcName = annotationAttrs.get("value").toString().trim();
            if ("".equals(rpcName)) {
                rpcName = clazz.getSimpleName();
            }

            String aliasBeanName = rpcPrefix + rpcName;
            if (!defaultListableBeanFactory.containsBean(aliasBeanName)) {
                log.info("Found JSON-RPC-Servicer to proxy [{}], bean name: {}.", className, aliasBeanName);
                defaultListableBeanFactory.registerSingleton(aliasBeanName, rpcService);
            }
        }
    }
}
