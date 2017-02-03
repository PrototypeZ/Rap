package io.github.jason1114.rap;


import java.util.concurrent.TimeUnit;

import io.github.jason1114.annotation.Expires;
import io.github.jason1114.annotation.Field;
import io.github.jason1114.annotation.Scope;
import io.github.jason1114.annotation.SharedPreference;
import io.github.jason1114.library.RapResult;
import rx.Observable;

/**
 *
 * 用户级别存储
 *
 * Created by Jason on 2017/1/28/0028.
 */
@Scope("User")
@SharedPreference("io.github.jason1114.rap")
public interface UserStorage {

    /**
     * 当前登录用户的信息
     */
    String USER = "USER";

    @Field(USER)
    Observable<User> getCurrentUser();

    /**
     * 手势密码，只有 3 分钟有效
     */
    String GESTURE_CODE_VALIDATE = "GESTURE_CODE_VALIDATE";

    @Field(GESTURE_CODE_VALIDATE)
    @Expires(value = 3, timeUnit = TimeUnit.MINUTES)
    Observable<Boolean> isGestureCodeValidate();

    Observable<RapResult> setGestureValidate(Boolean isValidate);
}

