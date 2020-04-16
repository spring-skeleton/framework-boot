package com.codeages.framework.handler;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
public class ExceptionResponse {

    @Setter(AccessLevel.NONE)
    private Boolean ok = false;

    private String code;

    private String message;

    private String traceId;

    private List<String> details;
}
