package com.codeages.framework.jsonrpc;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseError {
    private Integer code = 0;
    private String message = "";

    public ResponseError(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
