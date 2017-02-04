package io.github.jason1114.library;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.github.jason1114.builtin.sharedpreference.SharedPreferenceStorageProxy;

/**
 * Created by Jason on 2017/1/30/0030.
 */
public class Rap {

    public static final String VERSION_STORAGE_IDENTIFIER_PREFIX = "__VERSION_STORAGE_IDENTIFIER_PREFIX__";

    public final Builder mBuilder;

    private static List<? extends StorageProxy> builtInProxies = Arrays.asList(
            new SharedPreferenceStorageProxy()
    );


    public static Builder builder() {
        return new Builder();
    }

    private Rap(Builder builder) {
        mBuilder = builder;

        /**
         * 处理 VersionStorage 过期问题
         */
        int version = 0;
        try {
            PackageInfo pInfo = mBuilder.appContext().getPackageManager()
                    .getPackageInfo(mBuilder.appContext().getPackageName(), 0);
            version = pInfo.versionCode;
            if (version != 0) {
                FileInputStream fis = mBuilder.mContext
                        .openFileInput(VERSION_STORAGE_IDENTIFIER_PREFIX + version);
                fis.close();
            }
        } catch (FileNotFoundException fne) {
            try {
                invalidate(BuiltInScope.SCOPE_VERSION);
                FileOutputStream fos = mBuilder.mContext
                        .openFileOutput(VERSION_STORAGE_IDENTIFIER_PREFIX + version, Context.MODE_PRIVATE);
                fos.close();
            } catch (Throwable e) {
                Log.e("Rap", e.getMessage(), e);
            }
        } catch (Throwable e) {
            Log.e("Rap", e.getMessage(), e);
        }
    }

    public <ServiceType> RapStorage<ServiceType> getStorage(Class<ServiceType> clazzOfServiceType) {
        return RapStorage.findByServiceType(clazzOfServiceType, this);
    }

    public void invalidate(String scope) {
        for (StorageProxy proxy : getProxies()) {
            proxy.clearStorageByScope(mBuilder.appContext(), scope);
        }
    }

    private static List<? extends StorageProxy> getBuiltInProxies() {
        return builtInProxies;
    }

    List<? extends StorageProxy> getProxies() {
        List<? extends StorageProxy> proxies = getBuiltInProxies();
        proxies.addAll(new ArrayList(mBuilder.mStorageProxies));
        return proxies;
    }

    public static class Builder {

        Context mContext;
        List<String> mCustomScopes = new LinkedList<>();
        List<StorageProxy> mStorageProxies = new LinkedList<>();

        Builder() {

        }

        public Builder appContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Context appContext() {
            return this.mContext;
        }

        public Builder addCustomScope(String scope) {
            if (!TextUtils.isEmpty(scope)) {
                mCustomScopes.add(scope);
            }
            return this;
        }

        public Builder addCustomStorage(StorageProxy storageProxy) {
            if (storageProxy != null) {
                mStorageProxies.add(storageProxy);
            }
            return this;
        }

        public List<StorageProxy> customStorage() {
            return mStorageProxies;
        }

        public Rap build() {
            return new Rap(this);
        }
    }
}
