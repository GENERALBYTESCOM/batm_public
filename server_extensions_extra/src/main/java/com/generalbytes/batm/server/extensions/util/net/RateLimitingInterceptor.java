package com.generalbytes.batm.server.extensions.util.net;

import si.mazi.rescu.Interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RateLimitingInterceptor implements Interceptor {

    private final Class<?> clazz;
    private final int permitsPerSecond;
    private final int timeoutMillis;

    public RateLimitingInterceptor(Class<?> clazz, int permitsPerSecond, int timeoutMillis) {
        this.clazz = clazz;
        this.permitsPerSecond = permitsPerSecond;
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public Object aroundInvoke(InvocationHandler invocationHandler, Object proxy, Method method, Object[] args) throws Throwable {
        RateLimiter.waitForPossibleCall(clazz, permitsPerSecond, timeoutMillis);
        return invocationHandler.invoke(proxy, method, args);
    }
}
