package io.github.jason1114.library;

import android.content.Context;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.github.jason1114.builtin.sharedpreference.SharedPreferenceStorageProxy;

/**
 * Created by Jason on 2017/1/30/0030.
 */
public class Rap {

    public final Builder mBuilder;

    private static List<? extends StorageProxy> builtInProxies = Arrays.asList(
            new SharedPreferenceStorageProxy()
    );


    public static Builder builder() {
        return new Builder();
    }

    private Rap(Builder builder) {
        mBuilder = builder;
    }

    public <ServiceType> RapStorage<ServiceType> getStorage(Class<ServiceType> clazzOfServiceType) {
        return RapStorage.findByServiceType(clazzOfServiceType, this);
    }

    public static List<? extends StorageProxy> getBuiltInProxies() {
        return builtInProxies;
    }

    public static class Builder {

        Context mContext;
        List<String> mCustomScopes = new LinkedList<>();
        List<StorageProxy> mStorageProxies = new LinkedList<>();

        public Builder() {

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
