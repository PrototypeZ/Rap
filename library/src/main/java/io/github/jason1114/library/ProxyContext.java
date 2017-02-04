package io.github.jason1114.library;

/**
 * Created by Jason on 2017/1/31/0031.
 */
public class ProxyContext<T> {

    Class<T> mServiceClazz;
    Rap mRap;

    public ProxyContext(Class<T> clazz, Rap rap) {
        mServiceClazz = clazz;
        mRap = rap;
    }
}
