package io.github.jason1114.library;

/**
 * Created by Jason on 2017/1/31/0031.
 */
public class ProxyContext<T> {
    Class<T> serviceClazz;

    public ProxyContext(Class<T> clazz){
        serviceClazz = clazz;
    }
}