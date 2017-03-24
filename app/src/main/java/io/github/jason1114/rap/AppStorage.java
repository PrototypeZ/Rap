package io.github.jason1114.rap;


import java.util.concurrent.TimeUnit;

import io.github.jason1114.annotation.Expires;
import io.github.jason1114.annotation.Field;
import io.github.jason1114.annotation.Key;
import io.github.jason1114.annotation.Scope;
import io.github.jason1114.builtin.sharedpreference.SharedPreferences;
import io.github.jason1114.library.BuiltInScope;

/**
 *
 * 应用级别的存储
 *
 * Created by Jason on 2017/1/28/0028.
 */
@Scope(BuiltInScope.SCOPE_APP)
@SharedPreferences("io.github.jason1114.rap")
public interface AppStorage {

    /**
     * 是否不是第一次打开应用
     */
    String IS_NOT_FIRST_OPEN = "isOpenedBefore";

    @Field(IS_NOT_FIRST_OPEN)
    void setIsOpenedBefore(boolean isOpenedBefore);

    @Field(IS_NOT_FIRST_OPEN)
    boolean isOpenedBefore();

    String USER_DRAFT = "USER_DRAFT_{userId}";

    @Expires(value = 3, timeUnit = TimeUnit.SECONDS)
    @Field(USER_DRAFT)
    String getUserDraft(@Key("userId") String userId);

    @Field(USER_DRAFT)
    void setUserDraft(@Key("userId") String userId, String draft);

    String USER_SETTING = "USER_SETTING_{userId}_{option}";

    @Field(USER_SETTING)
    String getUserSetting(@Key("userId") String userId, @Key("option") String option);

    @Field(USER_SETTING)
    void setUserSetting(@Key("userId") String userId, @Key("option") String option, String value);

    @Field({USER_DRAFT, USER_SETTING})
    void setUserDraftAndSetting(
            @Key("userId") String userId, String draft,
            @Key("option") String option, String value
    );
}

