package com.codeages.framework.jsonrpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ClientCollector extends BaseCollector {

    protected void exec(ClassMetadata classMetadata, AnnotationMetadata annotationMetadata) {
        String jsonRpcPathAnnotation = JsonRpcClient.class.getName();
        if (annotationMetadata.isAnnotated(jsonRpcPathAnnotation)) {
            String className = classMetadata.getClassName();
            log.info("Found JSON-RPC client to proxy [{}].", className);

            Map<String, Object> annotationAttrs = annotationMetadata.getAnnotationAttributes(jsonRpcPathAnnotation);
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ClientProxy.class)
                    .addConstructorArgValue(className).addConstructorArgValue(annotationAttrs);
            defaultListableBeanFactory.registerBeanDefinition(className, beanDefinitionBuilder.getBeanDefinition());
        }
    }
}

