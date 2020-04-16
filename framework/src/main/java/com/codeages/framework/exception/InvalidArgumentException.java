package com.codeages.framework.exception;

public class InvalidArgumentException extends ServiceException {
    protected String code = "400";

    public InvalidArgumentException() {
        super();
    }

    public InvalidArgumentException(String message) {
        super(message);
    }

    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidArgumentException(Throwable cause) {
        super(cause);
    }

    public InvalidArgumentException(String errorCode, String message) {
        super(message);
        this.code = errorCode;
    }

    public InvalidArgumentException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
