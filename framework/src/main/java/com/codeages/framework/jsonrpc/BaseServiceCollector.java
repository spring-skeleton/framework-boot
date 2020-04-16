package com.codeages.framework.jsonrpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import static java.lang.String.format;
import static org.springframework.util.ClassUtils.convertClassNameToResourcePath;
import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

@Slf4j
abstract public class BaseServiceCollector implements BeanFactoryPostProcessor, ApplicationListener<ContextRefreshedEvent> {

    protected ApplicationContext applicationContext;
    protected DefaultListableBeanFactory defaultListableBeanFactory;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    protected String[] resolvePackageToScan() {
        String[] rpcPackages = applicationContext.getBean(Environment.class).getProperty("rpc.package").split("\\,");
        log.info(rpcPackages.toString());
        String[] rpcPackagePaths= new String[rpcPackages.length];
        for (int i=0;i<rpcPackagePaths.length;i++) {
            rpcPackagePaths[i] = CLASSPATH_URL_PREFIX + convertClassNameToResourcePath(rpcPackages[i]) + "/**/*.class";
        }
        return rpcPackagePaths;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        applicationContext = event.getApplicationContext();

        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(applicationContext);
        String[] resolvedPaths = resolvePackageToScan();
        for (String resolvedPath: resolvedPaths) {
            log.info("Scanning '{}' for JSON-RPC client interfaces.", resolvedPath);
            try {
                for (Resource resource : applicationContext.getResources(resolvedPath)) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        ClassMetadata classMetadata = metadataReader.getClassMetadata();
                        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                        exec(classMetadata, annotationMetadata);
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException(format("Cannot scan package '%s' for classes.", resolvedPath), e);
            }
        }
    }

    abstract protected void exec(ClassMetadata classMetadata, AnnotationMetadata annotationMetadata) throws Exception;
}