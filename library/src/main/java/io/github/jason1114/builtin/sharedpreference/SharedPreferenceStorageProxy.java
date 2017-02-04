package io.github.jason1114.builtin.sharedpreference;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import io.github.jason1114.library.ProxyContext;
import io.github.jason1114.library.ProxyMethod;
import io.github.jason1114.library.Rap;
import io.github.jason1114.library.StorageProxy;

import static io.github.jason1114.library.Constants.META_SCOPE;

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

    @Override
    public void clearStorageByScope(Context context, String scope) {
        File prefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        if (prefsDir.exists() && prefsDir.isDirectory()) {
            String[] list = prefsDir.list();
            for (String fileName : list) {
                String storageName = fileName.replace(".xml", "");
                removeSharedPreferenceByScope(context, scope, storageName);
            }
        }
    }

    private void removeSharedPreferenceByScope(Context context, String scope, String storageName) {
        android.content.SharedPreferences sp = context.getSharedPreferences(storageName,
                Context.MODE_PRIVATE);
        if (sp.contains(META_SCOPE)) {
            try {
                String testScope = sp.getString(META_SCOPE, "");
                if (TextUtils.equals(scope, testScope)) {
                    sp.edit().clear().apply();
                }
            } catch (Throwable throwable) {
                Log.e("Rap", throwable.getMessage(), throwable);
            }
        }
    }
}
