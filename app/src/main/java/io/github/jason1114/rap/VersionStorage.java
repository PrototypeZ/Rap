package io.github.jason1114.rap;

import io.github.jason1114.annotation.Field;
import io.github.jason1114.annotation.Scope;
import io.github.jason1114.annotation.SharedPreference;
import io.github.jason1114.library.BuiltInScope;
import io.github.jason1114.library.RapResult;
import rx.Observable;

/**
 *
 * 版本级别的存储
 *
 * Created by Jason on 2017/1/28/0028.
 */
@Scope(BuiltInScope.SCOPE_VERSION)
@SharedPreference("io.github.jason1114.rap")
public interface VersionStorage {

    /**
     * 下载的 APK 文件名
     */
    String DOWNLOAD_APK = "DOWNLOAD_APK";

    @Field(DOWNLOAD_APK)
    Observable<RapResult> setDownloadApkName(Long timestamp);

    @Field(DOWNLOAD_APK)
    Observable<String> getDownloadApkName();


    /**
     *  上一次检查更新的事件
     */
    String TIMESTAMP_OF_LAST_CHECK_UPDATE = "TIMESTAMP_OF_LAST_CHECK_UPDATE";

    @Field(TIMESTAMP_OF_LAST_CHECK_UPDATE)
    Observable<RapResult> setTimestampOfLastCheckingUpdate(Long timestamp);

    @Field(TIMESTAMP_OF_LAST_CHECK_UPDATE)
    Observable<Long> getTimestampOfLastCheckingUpdate();
}
