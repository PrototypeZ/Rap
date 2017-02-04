package io.github.jason1114.rap;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.github.jason1114.library.Rap;
import io.github.jason1114.rap.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String SCOPE_USER = "SCOPE_USER";

    Rap rap;

    AppStorage appLevelStorage;
    VersionStorage versionLevelStorage;
    UserStorage userLevelStorage;

    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        rap = Rap.builder()
                .appContext(getApplicationContext())
                .addCustomScope(SCOPE_USER)
                .build();

        /**
         * App level storage
         */
        appLevelStorage = rap.getStorage(AppStorage.class).api();
        mBinding.isNotFirstOpen.setText(String.valueOf(appLevelStorage.isOpenedBefore()));
        appLevelStorage.setIsOpenedBefore(true);

        /**
         * Version level storage
         */
        versionLevelStorage = rap.getStorage(VersionStorage.class).api();
        mBinding.downloadApk.setText(versionLevelStorage.getDownloadApkName());
        mBinding.downloadApk.setText(String.valueOf(versionLevelStorage.getTimestampOfLastCheckingUpdate()));
        versionLevelStorage.setDownloadApkName("aaa.apk");
        versionLevelStorage.setTimestampOfLastCheckingUpdate(10000l);


        /**
         * User level storage
         */
        userLevelStorage = rap.getStorage(UserStorage.class).api();

        User user = new User();
        user.age = 10l;
        user.gender = "male";
        user.name = "Jack";

        userLevelStorage.setCurrentUser(user);

        User u = userLevelStorage.getCurrentUser();

        mBinding.age.setText(String.valueOf(u.age));
        mBinding.name.setText(u.name);
        mBinding.gender.setText(u.gender);
    }
}
