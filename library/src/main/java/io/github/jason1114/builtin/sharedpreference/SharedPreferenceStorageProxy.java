package io.github.jason1114.builtin.sharedpreference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import io.github.jason1114.library.ProxyContext;
import io.github.jason1114.library.ProxyMethod;
import io.github.jason1114.library.Rap;
import io.github.jason1114.library.StorageProxy;

/**
 * Created by Jason on 2017/1/31/0031.
 */
public class SharedPreferenceStorageProxy implements StorageProxy {
    @Override
    public boolean canHandleService(Class service) {
        Annotation sharedPreferencesAnnotation = service.getAnnotation(SharedPreferences.class);
        return (sharedPreferencesAnnotation != null);
    }

    @Override
    public <T> ProxyContext<T> createContext(Class<T> service, Rap rap) {
        return new SharedPreferenceProxyContext<>(service, rap);
    }

    @Override
    public ProxyMethod createProxyMethod(ProxyContext<?> ctx, Method method) {
        if (ctx instanceof SharedPreferenceProxyContext) {
            return new SharedPreferenceProxyMethod(method);
        } else {
            throw new IllegalStateException("Proxy method not implemented.");
        }
    }
}
