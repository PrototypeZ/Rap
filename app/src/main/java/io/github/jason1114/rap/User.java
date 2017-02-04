package io.github.jason1114.rap;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jason on 2017/1/29/0029.
 */

public class User {

    @SerializedName("name")
    String name;

    @SerializedName("gender")
    String gender;

    @SerializedName("age")
    Long age;
}
