package io.github.jason1114.library;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jason on 2017/1/30/0030.
 */
public class Rap {

    private final Builder mBuilder;

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
        return RapStorage.findByServiceType(clazzOfServiceType);
    }

    public static List<? extends StorageProxy> getBuiltInProxies() {
        return builtInProxies;
    }

    public static class Builder {

        Context mContext;

        public Builder() {

        }

        public Builder appContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Rap build() {
            return new Rap(this);
        }
    }
}
