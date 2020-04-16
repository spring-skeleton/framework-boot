package com.codeages.framework.handler;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class Response<T> {
    @Setter(AccessLevel.NONE)
    private Boolean ok = true;

    private T result;

    public Response(T resultBody) {
        this.result = resultBody;
    }
}
