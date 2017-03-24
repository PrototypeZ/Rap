package io.github.jason1114.rap;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

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
        mBinding.isOpenBefore.setText(String.valueOf(appLevelStorage.isOpenedBefore()));
        appLevelStorage.setIsOpenedBefore(true);

        mBinding.set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appLevelStorage.setUserDraft("110", "hello!");
                appLevelStorage.setUserSetting("110", "show", "world!");
                mBinding.refreshAppStorage.performClick();
            }
        });
        mBinding.batchSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appLevelStorage.setUserDraftAndSetting(
                        "110", "HELLO!",
                        "show", "WORLD!"
                );
                mBinding.refreshAppStorage.performClick();
            }
        });
        mBinding.refreshAppStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.draft.setText(appLevelStorage.getUserDraft("110"));
                mBinding.optionShow.setText(appLevelStorage.getUserSetting("110", "show"));
            }
        });
        mBinding.refreshAppStorage.performClick();

        /**
         * Version level storage
         */
        versionLevelStorage = rap.getStorage(VersionStorage.class).api();
        mBinding.downloadApk.setText(versionLevelStorage.getDownloadApkName());
        mBinding.lastCheck.setText(String.valueOf(versionLevelStorage.getTimestampOfLastCheckingUpdate()));
        versionLevelStorage.setDownloadApkName("aaa.apk");
        versionLevelStorage.setTimestampOfLastCheckingUpdate(10000l);


        /**
         * User level storage
         */
        userLevelStorage = rap.getStorage(UserStorage.class).api();

        mBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.age = 10l;
                user.gender = "male";
                user.name = "Jack";
                userLevelStorage.setCurrentUser(user);

                List<User> userLikes = new ArrayList<>();
                User user1 = new User();
                user1.age = 10l;
                user1.gender = "male";
                user1.name = "Lucy";

                User user2 = new User();
                user2.age = 10l;
                user2.gender = "male";
                user2.name = "Lily";

                userLikes.add(user1);
                userLikes.add(user2);

                userLevelStorage.setLikes(userLikes);
                refreshUserInfo();
            }
        });

        mBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rap.invalidate(SCOPE_USER);
                refreshUserInfo();
            }
        });

        mBinding.gestureVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLevelStorage.setGestureValidate(true);
                refreshUserInfo();
            }
        });

        mBinding.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshUserInfo();
            }
        });

        refreshUserInfo();
    }

    private void refreshUserInfo() {
        User u = userLevelStorage.getCurrentUser();
        mBinding.age.setText(String.valueOf(u.age));
        mBinding.name.setText(u.name);
        mBinding.gender.setText(u.gender);
        mBinding.gesture.setText(String.valueOf(userLevelStorage.isGestureCodeValidate()));
        mBinding.likes.setText(userLevelStorage.getLikes().toString());
    }
}
