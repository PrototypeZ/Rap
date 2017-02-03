package io.github.jason1114.library;

import java.lang.reflect.Method;

/**
 * Created by Jason on 2017/1/31/0031.
 */
public class SharedPreferenceStorageProxy implements StorageProxy {
    @Override
    public boolean canHandleService(Class service) {
        return false;
    }

    @Override
    public <T> ProxyContext<T> createContext(Class<T> service) {
        return null;
    }

    @Override
    public ProxyMethod createProxyMethod(ProxyContext<?> ctx, Method method) {
        return null;
    }
}
