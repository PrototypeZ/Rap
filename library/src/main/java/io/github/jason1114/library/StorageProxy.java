package io.github.jason1114.library;

import java.lang.reflect.Method;

/**
 * Created by Jason on 2017/1/31/0031.
 */
public interface StorageProxy {

    boolean canHandleService(Class service);

    <T> ProxyContext<T> createContext(Class<T> service);

    ProxyMethod createProxyMethod(ProxyContext<?> ctx, Method method);
}
