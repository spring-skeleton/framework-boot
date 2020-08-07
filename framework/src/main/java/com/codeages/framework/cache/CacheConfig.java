package com.codeages.framework.cache;

import com.codeages.framework.biz.BaseEntity;
import com.codeages.framework.biz.BaseRepository;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@Slf4j
@Aspect
public class CacheConfig implements ApplicationRunner {
    @Autowired
    private CacheInterceptor interceptor;

    @Autowired
    private ApplicationContext context;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.cache.redis.time-to-live}")
    private Duration ttl;

    @Autowired(required = false)
    private List<BaseRepository> repositories;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    private Map<String, Map<String, List<String>>> classMethodsFieldsMap = new HashMap<>();
    private Map<String, Map<String, List<String>>> classFieldsMethodsMap = new HashMap<>();
    private static Map<String, String> classMap = new ConcurrentHashMap<String, String>();

    @After("execution(public * com.codeages..*.*Repository.update*(..))")
    public void afterUpdate(JoinPoint joinPoint) {

    }

    @Around("execution(public * com.codeages..*.*Repository.deleteById(..))  || execution(public * test.codeages..*.*Repository.deleteById(..))")
    public Object aroudDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = getEntityClassByRepository((BaseRepository)joinPoint.getTarget());
        if(!isCachedEntity(className)) {
            return joinPoint.proceed();
        }

        Long id = (Long)joinPoint.getArgs()[0];
        BaseRepository repository = (BaseRepository)joinPoint.getTarget();
        BaseEntity entity = repository.getById(id);

        Object result = joinPoint.proceed();
        this.clearCacheByEntity(className, entity);
        return result;
    }

    @Around("execution(public * com.codeages..*.*Repository.save(..)) || execution(public * test.codeages..*.*Repository.save(..))")
    public Object aroudSave(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = getEntityClassByRepository((BaseRepository)joinPoint.getTarget());
        if(!isCachedEntity(className)) {
            return joinPoint.proceed();
        }

        BaseEntity entity = (BaseEntity)joinPoint.getArgs()[0];
        if(null == entity.getId()) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        this.clearCacheByEntity(className, entity);
        return result;
    }

    public void syncCache(Long startDate, Long endDate) throws Exception {
        if(null == repositories || repositories.size() == 0) {
            log.debug("no repository should be sync.");
            return;
        }
        for (BaseRepository repository:repositories) {
            String className = getEntityClassByRepository(repository);
            if (isCachedEntity(className)) {
                log.debug("sync cache, get repository class: {} {} {}", className, startDate, endDate);
                List<BaseEntity> entities = repository.findByUpdatedTimeBetween(startDate, endDate);
                for (BaseEntity entity: entities) {
                    log.debug("clear cache: entity {}, id {}", entity.getClass().getName(), entity.getId());
                    clearCacheByEntity(className, entity);
                }
            }
        }
    }

    private boolean isCachedEntity(String className) {
        return !"".equals(className) && classMethodsFieldsMap.containsKey(className);
    }

    private void clearCacheByEntity(String className, BaseEntity entity) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<byte[]> keyList = new ArrayList<byte[]>();
        for (Map.Entry<String, List<String>> entry : classMethodsFieldsMap.get(className).entrySet()) {
            StringBuffer key = new StringBuffer(applicationName).append(":").append(className).append(":").append(entry.getKey()).append(":");
            key.append(this.generateKeyPartByFields(entity, entry.getValue()));
            log.info("delete key {}", key.toString());
            keyList.add(key.toString().getBytes());
        }

        if(keyList.size()>0){
            connectionFactory.getConnection().del(convertBytes(keyList));
        }
    }

    private byte[][] convertBytes(List<byte[]> keyList) {
        byte[][] keys = new byte[keyList.size()][];
        for (int i=0; i<keys.length; i++) {
            keys[i] = keyList.get(i);
        }
        ;
        return keys;
    }

    private String generateKeyPartByFields(Object arg, List<String> fields) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        StringBuffer key = new StringBuffer("");
        for (int i = 0; i < fields.size(); i++) {
            String fieldName = fields.get(i);
            Method method = arg.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
            key.append(fieldName).append(":").append(method.invoke(arg).toString());
            if (i < fields.size() - 1) {
                key.append(":");
            }
        }
        return key.toString();
    }

    @Bean
    public KeyGenerator keyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuffer key=new StringBuffer(":").append(getEntityClassByRepository((BaseRepository) target)).append(":").append(method.getName()).append(params.length).append(":");
                Parameter[] parameters = method.getParameters();
                Map<String, Object> paramsMap = new HashMap<>();
                for (int i = 0; i<parameters.length; i++) {
                    paramsMap.put(parameters[i].getName(), params[i]);
                }

                List<String> fields = classMethodsFieldsMap.get(getEntityClassByRepository((BaseRepository) target)).get(method.getName() + params.length);

                for (int i = 0; i<fields.size(); i++) {
                    key.append(fields.get(i)).append(":").append(paramsMap.get(fields.get(i)));
                    if(i < fields.size() - 1) {
                        key.append(":");
                    }
                }
                log.debug("redis key: {}", key.toString());
                return key.toString();
            }
        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .prefixKeysWith(applicationName)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        interceptor.setKeyGenerator(keyGenerator());
        this.parseClassMethodsFields();
        this.parseClassFieldsMethods();
        log.info("classMethodsFieldsMap: {}", classMethodsFieldsMap.toString());
        log.info("classFieldsMethodsMap: {}", classFieldsMethodsMap.toString());
    }

    private void parseClassFieldsMethods() {
        classMethodsFieldsMap.forEach((clazz,methodsFields)-> {
            Map<String, List<String>> fieldsMethods = new HashMap<>();
            methodsFields.forEach((method, fields)->{
                fields.forEach(field->{
                    if(!fieldsMethods.containsKey(field)){
                        List<String> methods = new ArrayList<>();
                        fieldsMethods.put(field, methods);
                    }
                    if(!fieldsMethods.get(field).contains(method)) {
                        fieldsMethods.get(field).add(method);
                    }
                });
            });
            classFieldsMethodsMap.put(clazz, fieldsMethods);
        });
    }

    private void parseClassMethodsFields() {
        context.getBeansOfType(BaseRepository.class).forEach((key, repository) -> {
            Arrays.stream(repository.getClass().getInterfaces()).forEach(clazz->{
                if(BaseRepository.class.isAssignableFrom(clazz)) {
                    String className = clazz.getName();
                    log.info("clazz: {}", className);

                    Map<String, List<String>> methodFields = new HashMap<>();
                    Arrays.stream(clazz.getDeclaredMethods()).forEach(method->{
                        Cache cacheable = method.getAnnotation(Cache.class);
                        if(cacheable != null) {
                            List<String> fields = new ArrayList<>();
                            log.info("method: {}", method.getName());
                            Parameter[] parameters = method.getParameters();
                            Arrays.stream(parameters).forEach(parameter -> {
                                if(!fields.contains(parameter.getName())) {
                                    fields.add(parameter.getName());
                                }
                            });
                            Collections.sort(fields);
                            methodFields.put(method.getName() + fields.size(), fields);
                        }
                    });

                    classMethodsFieldsMap.put(getEntityClassByRepository(repository), methodFields);
                }
            });
        });
    }

    private static String getEntityClassByRepository(BaseRepository repository){
        if(classMap.containsKey(repository.getClass().getName())){
            return classMap.get(repository.getClass().getName());
        }

        AtomicReference<String> result = new AtomicReference<>("");
        Arrays.stream(repository.getClass().getInterfaces()).forEach(clazz-> {
            if(BaseRepository.class.isAssignableFrom(clazz)) {
                result.set(clazz.getSimpleName().replace("Repository",""));
            }
        });
        classMap.put(repository.getClass().getName(), result.get());
        return result.get();
    }
}
