package com.codeages.framework.biz;

import com.codeages.framework.event.BaseEvent;
import com.codeages.framework.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.Set;

public class BaseService {

    @Autowired
    private ApplicationContext applicationContext;

    protected void publish(BaseEvent event) {
        applicationContext.publishEvent(event);
    }

    protected void validate(@Valid BaseDto dto) {
        Set<ConstraintViolation<@Valid BaseDto>> validateSet = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(dto, new Class[0]);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2)
                    .orElse("参数输入有误！");
            throw new InvalidArgumentException(messages);
        }
    }

    protected void validate(@Valid BaseVo vo) {
        Set<ConstraintViolation<@Valid BaseVo>> validateSet = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(vo, new Class[0]);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2)
                    .orElse("参数输入有误！");
            throw new InvalidArgumentException(messages);
        }
    }

    protected void validate(@Valid BaseEntity entity) {
        Set<ConstraintViolation<@Valid BaseEntity>> validateSet = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(entity, new Class[0]);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2)
                    .orElse("参数输入有误！");
            throw new InvalidArgumentException(messages);
        }
    }
}
