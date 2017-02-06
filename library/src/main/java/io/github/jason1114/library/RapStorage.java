package io.github.jason1114.library;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jason on 2017/1/30/0030.
 */
public class RapStorage<ServiceType> {

    static HashMap<Class, RapStorage> rapStorageCache = new HashMap<>();

    static HashMap<Method, ProxyMethod> methodCache = new HashMap<>();

    private Class<ServiceType> mServiceTypeClass;

    ProxyContext<ServiceType> mProxyContext;

    Rap mRap;

    private StorageProxy mStorageProxy;

    private RapStorage(Class<ServiceType> service, Rap rap) {
        mServiceTypeClass = service;
        mRap = rap;
        List<? extends StorageProxy> proxies = rap.getProxies();
        for (StorageProxy proxy : proxies) {
            boolean canHandleService = proxy.canHandleService(mServiceTypeClass);
            if (canHandleService) {
                mProxyContext = proxy.createContext(service, rap);
                mStorageProxy = proxy;
                break;
            }
        }
    }

    public ProxyMethod loadProxyMethod(Method method) {
        if (methodCache.containsKey(method)) {
            return methodCache.get(method);
        } else {
            ProxyMethod proxyMethod = mStorageProxy.createProxyMethod(mProxyContext, method);
            methodCache.put(method, proxyMethod);
            return proxyMethod;
        }
    }

    @SuppressWarnings("unchecked")
    public ServiceType api() {
        loadAllMethods();
        return (ServiceType) Proxy.newProxyInstance(mServiceTypeClass.getClassLoader(),
                new Class<?>[]{mServiceTypeClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        ProxyMethod proxyMethod = loadProxyMethod(method);
                        return proxyMethod.call(mProxyContext, args);
                    }
                });
    }

    public void loadAllMethods() {
        Method[] methods = mServiceTypeClass.getMethods();
        for (Method method : methods) {
            loadProxyMethod(method);
        }
    }

    @SuppressWarnings("unchecked")
    public static <ServiceType> RapStorage<ServiceType> findByServiceType(
            Class<ServiceType> service, Rap rap) {
        if (rapStorageCache.containsKey(service)) {
            return rapStorageCache.get(service);
        } else {
            RapStorage<ServiceType> rapStorage = new RapStorage<>(service, rap);
            rapStorageCache.put(service, rapStorage);
            return rapStorage;
        }
    }
}
