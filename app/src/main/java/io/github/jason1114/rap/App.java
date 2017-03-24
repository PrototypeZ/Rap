package io.github.jason1114.rap;

import com.facebook.stetho.Stetho;

import android.app.Application;

/**
 * Created by lenovo on 2017/3/24.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
