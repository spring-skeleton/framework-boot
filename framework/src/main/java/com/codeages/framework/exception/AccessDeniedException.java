package com.codeages.framework.exception;

public class AccessDeniedException extends ServiceException {

    protected String code = "403";

    public AccessDeniedException() {
        super("无权限访问");
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }

    public AccessDeniedException(String errorCode, String message) {
        super(message);
        this.code = errorCode;
    }

    public AccessDeniedException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
