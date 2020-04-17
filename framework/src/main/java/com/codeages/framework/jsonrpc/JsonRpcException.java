package com.codeages.framework.jsonrpc;

import com.codeages.framework.exception.ServiceException;

public class JsonRpcException extends ServiceException {
    protected String code = "500";

    public JsonRpcException(String message){
        super(message);
    }
}
