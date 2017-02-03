package io.github.jason1114.rap;

import android.database.Observable;

import io.github.jason1114.annotation.Field;
import io.github.jason1114.annotation.Scope;
import io.github.jason1114.annotation.SharedPreference;
import io.github.jason1114.library.BuiltInScope;
import io.github.jason1114.library.RapResult;

/**
 *
 * 应用级别的存储
 *
 * Created by Jason on 2017/1/28/0028.
 */
@Scope(BuiltInScope.SCOPE_APP)
@SharedPreference("io.github.jason1114.rap")
public interface AppStorage {

    /**
     * 是否不是第一次打开应用
     */
    String IS_NOT_FIRST_OPEN = "IS_NOT_FIRST_OPEN";

    @Field(IS_NOT_FIRST_OPEN)
    Observable<RapResult> setIsNotFirstOpen(Boolean timestamp);

    @Field(IS_NOT_FIRST_OPEN)
    Observable<Boolean> isNotFirstOpen();
}

