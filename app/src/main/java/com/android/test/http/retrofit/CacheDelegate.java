package com.android.test.http.retrofit;

import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.mock.Calls;

/**
 * des:
 * author: libingyan
 * Date: 18-6-1 15:15
 */
public final class CacheDelegate<T> {
    final Retrofit retrofit;
    private final Class<T> service;


    public CacheDelegate(Retrofit retrofit, Class<T> service) {
        this.retrofit =retrofit;
        this.service =service;
    }

    public T returningResponse(@Nullable Object response) {
        return returning(Calls.response(response));
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <R> T returning(final Call<R> call) {
        //final Call<R> behaviorCall = new CacheCall<>(call);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[] { service },
            new InvocationHandler() {
                @Override
                public T invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Type returnType = method.getGenericReturnType();
                    Annotation[] methodAnnotations = method.getAnnotations();
                    CallAdapter<R, T> callAdapter =
                        (CallAdapter<R, T>) retrofit.callAdapter(returnType, methodAnnotations);
                    //return callAdapter.adapt(behaviorCall);
                    return callAdapter.adapt(call);
                }
            });
    }
}
