package com.codeages.framework.jsonrpc;

import java.util.Base64;

public class Authorization {
    public static String getBasicAuthorization(String username, String password) {
        String userAndPassword = username + ":"
                + password;
        byte[] auth = Base64.getEncoder().encode(userAndPassword.getBytes());
        return new String(auth);
    }
}
