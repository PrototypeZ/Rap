package io.github.jason1114.library;

import android.content.Context;

import java.lang.reflect.Method;

/**
 * Created by Jason on 2017/1/31/0031.
 */
public interface StorageProxy {

    boolean canHandleService(Class service);

    <T> ProxyContext<T> createContext(Class<T> service, Rap rap);

    ProxyMethod createProxyMethod(ProxyContext<?> ctx, Method method);

    void clearStorageByScope(Context context, String scope);
}
