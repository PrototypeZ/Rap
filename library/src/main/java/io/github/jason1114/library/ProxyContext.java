package io.github.jason1114.library;

import io.github.jason1114.annotation.Scope;

/**
 * Created by Jason on 2017/1/31/0031.
 */
public class ProxyContext<T> {

    protected Class<T> mServiceClazz;
    protected Rap mRap;

    protected Scope mScopeAnnotation;
    protected String mScope;

    public ProxyContext(Class<T> clazz, Rap rap) {
        mServiceClazz = clazz;
        mRap = rap;
        mScopeAnnotation = clazz.getAnnotation(Scope.class);
        mScope = (mScopeAnnotation == null) ? BuiltInScope.SCOPE_APP : mScopeAnnotation.value();
    }
}
