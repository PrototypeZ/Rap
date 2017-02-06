package io.github.jason1114.rap;


import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.jason1114.annotation.Expires;
import io.github.jason1114.annotation.Field;
import io.github.jason1114.annotation.Scope;
import io.github.jason1114.builtin.sharedpreference.SharedPreferences;

/**
 *
 * 用户级别存储
 *
 * Created by Jason on 2017/1/28/0028.
 */
@Scope(MainActivity.SCOPE_USER)
@SharedPreferences("io.github.jason1114.user")
public interface UserStorage {

    /**
     * 当前登录用户的信息
     */
    String USER = "USER";

    @Field(USER)
    User getCurrentUser();

    @Field(USER)
    void setCurrentUser(User user);

    /**
     * 手势密码，只有 3 分钟有效
     */
    String GESTURE_CODE_VALIDATE = "GESTURE_CODE_VALIDATE";

    @Field(GESTURE_CODE_VALIDATE)
    @Expires(value = 3, timeUnit = TimeUnit.SECONDS)
    boolean isGestureCodeValidate();

    @Field(GESTURE_CODE_VALIDATE)
    void setGestureValidate(Boolean isValidate);

    /**
     * 关注的人
     */
    String LIKES = "LIKES";

    @Field(LIKES)
    List<User> getLikes();

    @Field(LIKES)
    void setLikes(List<User> users);
}

