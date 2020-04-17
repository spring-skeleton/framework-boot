package com.codeages.framework.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private static final long serialVersionUID = 1L;
    private String id;
    private String jsonrpc = "2.0";
    private ResponseError error;
    private T result;

    public Response(T responseType) {
        this.result = responseType;
    }

    public Response(ResponseError responseError) {
        this.error = responseError;
    }

}
