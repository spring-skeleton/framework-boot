package com.codeages.framework.auth;

import java.util.Map;

public interface AuthService {
    String generateAuthToken(Map<String, String> map);

    Long verify(String authToken);
}
