package io.github.jason1114.rap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.jason1114.library.Rap;

public class MainActivity extends AppCompatActivity {


    Rap rap;

    AppStorage appLevelStorage;
    VersionStorage versionLevelStorage;
    UserStorage userLevelStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rap = Rap.builder()
                .appContext(getApplicationContext())
                .build();

        appLevelStorage = rap.getStorage(AppStorage.class).api();


        versionLevelStorage = rap.getStorage(VersionStorage.class).api();


        userLevelStorage = rap.getStorage(UserStorage.class).api();

    }
}
