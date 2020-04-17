package com.codeages.framework.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

@Slf4j
public class BaseListener implements ApplicationListener<BaseEvent> {

    @Override
    public void onApplicationEvent(BaseEvent event) {
        Method[] methods = this.getClass().getMethods();
        AtomicBoolean hasMethod = new AtomicBoolean(false);
        Arrays.stream(methods).filter(new Predicate<Method>() {
            @Override
            public boolean test(Method method) {
                return method.getName().equals("onApplicationEvent")
                        && method.getParameterTypes()[0].getName().equals(event.getClass().getName());
            }
        }).forEach(method -> {
            try {
                hasMethod.set(true);
                method.invoke(this, event);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });

        if(!hasMethod.get()){
            log.warn("找不到事件监听：{}", event.getClass().getName());
        }
    }
}
