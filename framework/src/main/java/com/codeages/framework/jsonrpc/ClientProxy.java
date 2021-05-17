package com.codeages.framework.jsonrpc;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public class ClientProxy
        implements MethodInterceptor, InitializingBean, FactoryBean<Object>, ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(ClientProxy.class);
    private ApplicationContext applicationContext;
    private Environment environment;
    private Object proxyObject = null;
    private Class<?> clazz;
    private String path;
    private String server;
    private HttpClient client = new HttpClient();
    private Gson gson;

    @Autowired
    private DiscoveryClient discoveryClient;

    public ClientProxy(String className, Map<String, Object> annotationAttrs, DiscoveryClient discoveryClient) throws Throwable {
        this.clazz = Class.forName(className);
        this.path = annotationAttrs.get("name").toString();
        this.server = annotationAttrs.get("server").toString();
        this.discoveryClient = discoveryClient;
        this.gson = initGson();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        environment = this.applicationContext.getBean(Environment.class);
    }

    public Object getObject() throws Exception {
        return proxyObject;
    }

    public Class<?> getObjectType() {
        return this.clazz;
    }

    public boolean isSingleton() {
        return true;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        PostMethod httpMethod = new PostMethod(getUri());
        httpMethod.addRequestHeader("Authorization", "Basic " + getBasicAuthorization());
        httpMethod.addRequestHeader("Content-type", "application/json");

        Request request = new Request();
        request.setParams(invocation.getArguments());
        request.setMethod(path + "." + invocation.getMethod().getName());
        String requestJson = this.gson.toJson(request).toString();

        logger.info(request.getMethod() + " json-rpc request: " + requestJson);

        RequestEntity requestEntity = new StringRequestEntity(requestJson, "application/json", "UTF-8");

        httpMethod.setRequestEntity(requestEntity);
        client.executeMethod(httpMethod);
        String responseStr = httpMethod.getResponseBodyAsString();

        logger.info(request.getMethod() + " json-rpc response: " + responseStr);

        Type returnType = invocation.getMethod().getGenericReturnType();

        Type responseType = new TypeToken<Response>() {
            private static final long serialVersionUID = 1L;
        }.getType();
        Response response = this.gson.fromJson(responseStr, responseType);

        if (response == null) {
            throw new JsonRpcException("rpc调用异常 无返回值");
        }

        if (response.getError() != null) {
            throw new JsonRpcException(String.format("rpc 调用错误, code: %s, error: %s", response.getError().getCode(), response.getError().getMessage()));
        }
        return gson.fromJson(this.gson.toJson(response.getResult()), returnType);
    }

    private Gson initGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateSerializer()).setDateFormat(DateFormat.LONG);
        builder.registerTypeAdapter(Date.class, new DateDeserializer()).setDateFormat(DateFormat.LONG);
        Gson gson = builder.create();
        return gson;
    }

    private String getBasicAuthorization() {
        String userAndPassword = environment.getProperty(server + ".username") + ":"
                + environment.getProperty(server + ".password");
        byte[] auth = Base64.getEncoder().encode(userAndPassword.getBytes());
        return new String(auth);
    }

    private String getUri() {
        List<ServiceInstance> instances = discoveryClient.getInstances(environment.getProperty(server));
        List<String> uris = instances.stream().map(instance -> instance.getUri().toString()).collect(Collectors.toList());
        int index = ThreadLocalRandom.current().nextInt(uris.size());
        String uri = uris.get(index);
        return uri + "/jsonrpc.php";
    }

    public void afterPropertiesSet() {
        proxyObject = ProxyFactory.getProxy(this.clazz, this);
    }

}

class DateSerializer implements JsonSerializer<Date> {
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getTime() / 1000);
    }
}

class DateDeserializer implements JsonDeserializer<Date> {
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return new Date(json.getAsJsonPrimitive().getAsLong() * 1000);
    }
}

