package com.codeages.framework.event;

import org.springframework.context.ApplicationEvent;

abstract public class BaseEvent<T> extends ApplicationEvent {
    public BaseEvent(T source) {
        super(source);
    }

    @Override
    public T getSource() {
        return (T)super.getSource();
    }
}
