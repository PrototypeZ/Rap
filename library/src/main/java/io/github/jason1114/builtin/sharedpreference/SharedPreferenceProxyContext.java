package io.github.jason1114.builtin.sharedpreference;

import android.content.Context;

import io.github.jason1114.library.ProxyContext;
import io.github.jason1114.library.Rap;

/**
 * Created by Jason on 2017/2/3.
 */

public class SharedPreferenceProxyContext<T> extends ProxyContext<T> {

    SharedPreferences annotation;
    String fileName;
    android.content.SharedPreferences sp;

    public static final String __META_SCOPE__ = "__META_SCOPE__";

    public SharedPreferenceProxyContext(Class<T> clazz, Rap rap) {
        super(clazz, rap);
        annotation = clazz.getAnnotation(SharedPreferences.class);
        fileName = annotation.value();
        sp = rap.mBuilder.appContext()
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);

        ensureMetaInfo();
    }

    public void ensureMetaInfo() {
        if (!sp.contains(__META_SCOPE__)) {
            sp.edit().putString(__META_SCOPE__, mScope).apply();
        }
    }
}
