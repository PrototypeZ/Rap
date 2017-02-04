package io.github.jason1114.rap;


import io.github.jason1114.annotation.Field;
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
}

