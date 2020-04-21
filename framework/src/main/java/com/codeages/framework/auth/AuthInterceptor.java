package com.codeages.framework.auth;

import com.codeages.framework.exception.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Value("${auth.token.name}")
    private String tokenName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (!handlerMethod.getMethod().isAnnotationPresent(ExcludeAuth.class)) {
            return true;
        }

        String authToken = request.getHeader(tokenName);
        if (authToken == null) {
            throw new AccessDeniedException(String.format("No %s Header Token", tokenName));
        }

        Boolean result = checkAuthorization(authToken);
        if (!result) {
            throw new AccessDeniedException("无权限访问");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        return;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
    }

    private Boolean checkAuthorization(String authToken) {
        try {
            return authService.verify(authToken);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
