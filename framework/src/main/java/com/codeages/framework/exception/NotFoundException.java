package com.codeages.framework.exception;

public class NotFoundException extends ServiceException {
    protected String code = "404";

    public NotFoundException() {
        super("资源不存在");
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String errorCode, String message) {
        super(message);
        this.code = errorCode;
    }

    public NotFoundException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
