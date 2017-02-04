package io.github.jason1114.builtin.sharedpreference;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import io.github.jason1114.library.ProxyContext;
import io.github.jason1114.library.Rap;

import static io.github.jason1114.library.Constants.META_SCOPE;

/**
 * Created by Jason on 2017/2/3.
 */

public class SharedPreferenceProxyContext<T> extends ProxyContext<T> {

    SharedPreferences annotation;
    String fileName;
    android.content.SharedPreferences sp;
    Set<String> expireKeySet = new HashSet<>();

    public SharedPreferenceProxyContext(Class<T> clazz, Rap rap) {
        super(clazz, rap);
        annotation = clazz.getAnnotation(SharedPreferences.class);
        fileName = annotation.value();
        sp = rap.mBuilder.appContext()
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);

        ensureMetaInfo();
    }

    public void ensureMetaInfo() {
        if (!sp.contains(META_SCOPE)) {
            sp.edit().putString(META_SCOPE, mScope).apply();
        }
    }
}
