package com.codeages.framework.jsonrpc;

import com.codeages.framework.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class JsonRpcFilter implements Filter {
    @Autowired
    private ApplicationContext context;

    @Value("${rpc.username}")
    private String username;

    @Value("${rpc.password}")
    private String password;

    private Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL)
            .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (!httpServletRequest.getRequestURI().startsWith("/jsonrpc") || !httpServletRequest.getMethod().equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        String[] authorization = httpServletRequest.getHeader("Authorization").split("\\ ");
        if (!validAuthorization(authorization)) {
            log.info("rpc-request: authorization invalid {}", httpServletRequest.getHeader("Authorization"));
            throw new ServiceException("无效的Authorization");
        }

        String body = this.getBody(httpServletRequest);
        log.info("json-rpc server client-ip: {} request: {}", httpServletRequest.getRemoteHost(), body);

        Request<Object> rpcRequest = gson.fromJson(body, new TypeToken<Request<Object>>() {
        }.getType());
        String[] rpcMethods = rpcRequest.getMethod().split("\\.");
        log.debug("bean name: {}", "rpc." + rpcMethods[0]);
        Object bean = context.getBean("rpc." + rpcMethods[0]);

        Optional<Method> callMethod = Arrays.stream(bean.getClass().getMethods()).filter(method -> method.getName().equals(rpcMethods[1])).findAny();
        if (callMethod.isEmpty()) {
            Response jsonResponse = new Response(new ResponseError(-32601, "Method not found"));
            sendJsonResponse(response, httpServletRequest, rpcRequest, jsonResponse);
            return;
        }
        Method method = callMethod.get();
        if (rpcRequest.getParams().length != method.getParameterTypes().length) {
            Response jsonResponse = new Response(new ResponseError(-32602, "Invalid params"));
            sendJsonResponse(response, httpServletRequest, rpcRequest, jsonResponse);
            return;
        }

        Object[] params = new Object[rpcRequest.getParams().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Type methodType = method.getParameterTypes()[i];
            log.debug("param {} {}", methodType.getTypeName(), gson.toJson(rpcRequest.getParams()[i]));
            params[i] = gson.fromJson(gson.toJson(rpcRequest.getParams()[i]), methodType);
        }

        try {
            log.debug("id: {} rpc get method: {}", rpcRequest.getId(), method.getName());
            log.debug("id: {} rpc get bean: {}", rpcRequest.getId(), bean.getClass());

            Response jsonRpcResponse = new Response(method.invoke(bean, params));
            sendJsonResponse(response, httpServletRequest, rpcRequest, jsonRpcResponse);
            return;
        } catch (InvocationTargetException e) {
            ResponseError responseError = new ResponseError();
            responseError.setMessage(e.getTargetException().getMessage());
            if (e.getTargetException() instanceof ServiceException) {
                String errorCode = ((ServiceException) e.getTargetException()).getCode();
                responseError.setCode(Integer.valueOf(errorCode));
            } else {
                responseError.setCode(-32000);
            }
            Response jsonResponse = new Response(responseError);
            sendJsonResponse(response, httpServletRequest, rpcRequest, jsonResponse);
            log.error(e.getTargetException().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new JsonRpcException(String.format("rpc 调用错误, method: %s, params: %s, exception: %s", rpcRequest.getMethod(), rpcRequest.getParams().toString(), e.getMessage()));
        }

    }

    private void sendJsonResponse(ServletResponse response, HttpServletRequest httpServletRequest, Request<Object> rpcRequest, Response jsonRpcResponse) throws IOException {
        jsonRpcResponse.setId(rpcRequest.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        String result = objectMapper.writeValueAsString(jsonRpcResponse);
        log.info("json-rpc server client-ip: {} response: {}", httpServletRequest.getRemoteHost(), result);
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(result);
        writer.flush();
    }

    private boolean validAuthorization(String[] authorization) {
        return "Basic".toLowerCase().equals(authorization[0].toLowerCase()) && Authorization.getBasicAuthorization(username, password).equals(authorization[1]);
    }

    private String getBody(HttpServletRequest request) {
        String line, body = "";
        try {
            BufferedReader br = request.getReader();
            while ((line = br.readLine()) != null) {
                body += line;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return body;
    }
}
