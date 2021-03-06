package com.codeages.framework.biz;

import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Map;

abstract public class BaseSpecification<T> implements Specification<T> {

    protected Map<String, Object> conditions;

    public BaseSpecification(Map<String, Object> conditions) {
        this.conditions = filterConditions(conditions);
    }

    private Map filterConditions(Map<String, Object> conditions) {
        conditions.values().removeAll(Collections.singleton(""));

        return conditions;
    }
}
