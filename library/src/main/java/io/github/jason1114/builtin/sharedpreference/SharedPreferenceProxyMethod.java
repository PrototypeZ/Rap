package io.github.jason1114.builtin.sharedpreference;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.jason1114.annotation.Expires;
import io.github.jason1114.annotation.Field;
import io.github.jason1114.annotation.Key;
import io.github.jason1114.library.ProxyContext;
import io.github.jason1114.library.ProxyMethod;

import static io.github.jason1114.library.Constants.TIMESTAMP_KEY;

/**
 * Created by Jason on 2017/2/3.
 */

public class SharedPreferenceProxyMethod extends ProxyMethod {

    private Field mFieldAnnotation;
    private Expires mExpiresAnnotation;
    private String[] mFieldNames;

    /**
     * 两个维度，第一个维度代表第几个 @Field 标签里的带占位符的 KEY_NAME
     * 第二个维度代表在这个带占位符的 KEY_NAME 里，占位符对应具体传参参数在函数声明里的位置，
     *
     * 例如：
     * <p>
     *
     * @Field("USER_NAME_{userId}", "ARTICLE_INFO_{articleId}")
     * void setUserNameAndArticleInfo(
     * @Key("userId")String userId, String username,
     * @Key("articleId") String articleId, String articleInfo
     * );
     * </p>
     * 对应的二位数组：
     * [
     * [0],
     * [2]
     * ]
     */
    private int[][] mPlaceholderPositionForFieldName;


    private MethodType mMethodType;
    private Class mReturnType;
    private Annotation[][] mParameterAnnotations;
    private HashMap<Class, java.lang.reflect.Field[]> typeFieldsCache = new HashMap<>();
    Pattern mPattern = Pattern.compile("\\{(\\w+)\\}");
    Pattern mPatternForReplace = Pattern.compile("\\{\\w+\\}");

    Gson mGson = new Gson();

    SharedPreferenceProxyMethod(ProxyContext context, Method method) {
        mFieldAnnotation = method.getAnnotation(Field.class);
        mExpiresAnnotation = method.getAnnotation(Expires.class);
        if (mFieldAnnotation == null) {
            throw new IllegalStateException("Each method should has a @Field annotation.");
        }
        mFieldNames = mFieldAnnotation.value();
        if (mFieldNames.length == 0) {
            throw new IllegalArgumentException("Field names should not be empty");
        }
        mPlaceholderPositionForFieldName = new int[mFieldNames.length][];
        mParameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < mFieldNames.length; i++) {
            setUpPlaceholderForField(i);
        }
        mReturnType = method.getReturnType();
        if (mReturnType == void.class) {
            // setter method
            mMethodType = MethodType.SETTER;
        } else {
            // getter method
            mMethodType = MethodType.GETTER;
        }
        if (mExpiresAnnotation != null) {
            ensureExpiresKeyStored(mFieldNames, ((SharedPreferenceProxyContext) context).expireKeySet);
        }
    }

    private void setUpPlaceholderForField(int fieldIndex) {
        String field = mFieldNames[fieldIndex];
        if (TextUtils.isEmpty(field)) return;
        Matcher matcher = mPattern.matcher(field);
        int i = 0;
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            int index = getPlaceHolderIndexInMethodDeclaration(placeholder);
            if (index < 0) {
                throw new IllegalArgumentException("Can't find placeholder " + placeholder);
            }
            if (mPlaceholderPositionForFieldName[fieldIndex] == null) {
                // TODO 目前最多支持5个可变参数
                mPlaceholderPositionForFieldName[fieldIndex] = new int[5];
            }
            mPlaceholderPositionForFieldName[fieldIndex][i++] = index;
        }
    }

    private int getPlaceHolderIndexInMethodDeclaration(String placeholder) {
        for (int j = 0; j < mParameterAnnotations.length; j++) {
            Annotation[] annotations = mParameterAnnotations[j];
            for (int k = 0; k < annotations.length; k++) {
                Annotation a = annotations[k];
                if (a instanceof Key && TextUtils.equals(placeholder, ((Key) a).value())) {
                    return j;
                }
            }
        }
        return -1;
    }

    @Override
    public Object call(ProxyContext context, Object... args) {
        if (context instanceof SharedPreferenceProxyContext) {
            ((SharedPreferenceProxyContext) context).ensureMetaInfo();
            if (mExpiresAnnotation != null) {
                ensureExpiresKeyStored(mFieldNames, ((SharedPreferenceProxyContext) context).expireKeySet);
            }
            switch (mMethodType) {
                case GETTER:
                    return invokeGetterMethod((SharedPreferenceProxyContext) context, args);
                case SETTER:
                    invokeSetterMethod((SharedPreferenceProxyContext) context, args);
                    return null;
                default:
                    throw new IllegalStateException();
            }
        } else {
            throw new IllegalStateException("ProxyContext should be " + SharedPreferenceProxyContext.class.getCanonicalName());
        }
    }

    private void ensureExpiresKeyStored(String[] fieldNames, Set<String> set) {
        for (String fieldName : fieldNames) {
            set.add(fieldName);
        }
    }

    private String getKeyForStorage(int fieldIndex, Object... args) {
        int[] positions = mPlaceholderPositionForFieldName[fieldIndex];
        if (positions != null && positions.length > 0) {
            Matcher matcher = mPatternForReplace.matcher(mFieldNames[fieldIndex]);
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while (matcher.find()) {
                matcher.appendReplacement(
                        sb,
                        String.valueOf(args[positions[i++]])
                );
            }
            matcher.appendTail(sb);
            return sb.toString();
        } else {
            return mFieldNames[fieldIndex];
        }
    }

    private Object invokeGetterMethod(SharedPreferenceProxyContext context, Object... args) {
        String getterKey = getKeyForStorage(0, args);
        if (mExpiresAnnotation != null) {
            long duration = mExpiresAnnotation.value();
            TimeUnit unit = mExpiresAnnotation.timeUnit();
            boolean crossTimeUnit = mExpiresAnnotation.crossTimeUnit();

            long lastTimestamp = context.sp.getLong(getTimestampKeyByField(getterKey), 0);
            if (lastTimestamp != 0 && isTimeExpires(unit, crossTimeUnit, lastTimestamp, duration)) {
                context.sp.edit().remove(getterKey).commit();
            }
        }
        if (mReturnType == Long.class || mReturnType == long.class) {
            return context.sp.getLong(getterKey, 0);
        } else if (mReturnType == Integer.class || mReturnType == int.class) {
            return context.sp.getInt(getterKey, 0);
        } else if (mReturnType == Boolean.class || mReturnType == boolean.class) {
            return context.sp.getBoolean(getterKey, false);
        } else if (mReturnType == Float.class || mReturnType == float.class) {
            return context.sp.getFloat(getterKey, 0f);
        } else if (mReturnType == String.class) {
            return context.sp.getString(getterKey, "");
        } else if (Set.class.isAssignableFrom(mReturnType)) {
            return context.sp.getStringSet(getterKey, Collections.<String>emptySet());
        } else if (List.class.isAssignableFrom(mReturnType)) {
            return mGson.fromJson(context.sp.getString(getterKey, "[]"), List.class);
        } else {
            return getCustomObject(context);
        }
    }

    private boolean isTimeExpires(TimeUnit unit, boolean crossTimeUnit, long lastTimestamp, long duration) {
        long current = System.currentTimeMillis();
        if (crossTimeUnit) {
            Calendar lastCalendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();
            lastCalendar.setTimeInMillis(lastTimestamp);
            currentCalendar.setTimeInMillis(current);
            switch (unit) {
                case DAYS:
                    return (currentCalendar.get(Calendar.DAY_OF_YEAR) - lastCalendar.get(Calendar.DAY_OF_YEAR) >= duration);
                case HOURS:
                    if (currentCalendar.get(Calendar.DAY_OF_YEAR) > lastCalendar.get(Calendar.DAY_OF_YEAR)) {
                        return true;
                    } else {
                        return currentCalendar.get(Calendar.HOUR_OF_DAY) - lastCalendar.get(Calendar.HOUR_OF_DAY) >= duration;
                    }
                default:
                    throw new IllegalArgumentException("cross time unit " + unit + " not supported");
            }

        } else {
            long durationInMiles = unit.toMillis(duration);
            if (lastTimestamp + durationInMiles < current) {
                // The key expires
                return true;
            } else {
                return false;
            }
        }
    }

    private String getTimestampKeyByField(String field) {
        return TIMESTAMP_KEY + field;
    }

    private Object getCustomObject(SharedPreferenceProxyContext context) {
        Object dataToBeReturned = null;
        try {
            dataToBeReturned = mReturnType.newInstance();
            java.lang.reflect.Field[] fields = loadFieldsForClass(mReturnType);
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field field = fields[i];
                SerializedName fieldAnnotation = field.getAnnotation(SerializedName.class);
                assignValueToField(context, dataToBeReturned, field, fieldAnnotation);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return dataToBeReturned;
    }

    private void setCustomObject(String objectFieldName, Object arg, SharedPreferences.Editor editor) {
        java.lang.reflect.Field[] fields = loadFieldsForClass(arg.getClass());
        for (int i = 0; i < fields.length; i++) {
            java.lang.reflect.Field field = fields[i];
            SerializedName fieldAnnotation = field.getAnnotation(SerializedName.class);
            String keyInSpFile = getInnerKeyOfCustomObj(objectFieldName, fieldAnnotation);
            Class fieldType = field.getType();
            try {
                if (fieldType == Long.class || fieldType == long.class) {
                    editor.putLong(keyInSpFile, fieldType.isPrimitive() ? field.getLong(arg) : (Long) field.get(arg));
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    editor.putInt(keyInSpFile, fieldType.isPrimitive() ? field.getInt(arg) : (Integer) field.get(arg));
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    editor.putBoolean(keyInSpFile, fieldType.isPrimitive() ? field.getBoolean(arg) : (Boolean) field.get(arg));
                } else if (fieldType == Float.class || fieldType == float.class) {
                    editor.putFloat(keyInSpFile, fieldType.isPrimitive() ? field.getFloat(arg) : (Float) field.get(arg));
                } else if (fieldType == String.class) {
                    editor.putString(keyInSpFile, (String) field.get(arg));
                } else if (Set.class.isAssignableFrom(fieldType)) {
                    editor.putStringSet(keyInSpFile, (Set<String>) field.get(arg));
                } else if (List.class.isAssignableFrom(fieldType)) {
                    editor.putString(keyInSpFile, mGson.toJson(field.get(arg)));
                } else {
                    continue;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private java.lang.reflect.Field[] loadFieldsForClass(Class type) {
        java.lang.reflect.Field[] fields = typeFieldsCache.get(type);
        if (fields == null) {
            fields = filterAnnotatedFields(type.getDeclaredFields());
        }
        return fields;
    }

    private void assignValueToField(SharedPreferenceProxyContext context, Object instance,
                                    java.lang.reflect.Field field, SerializedName fieldAnnotation) {

        Class fieldType = field.getType();
        String keyInSpFile = getInnerKeyOfCustomObj(mFieldAnnotation.value()[0], fieldAnnotation);
        Object data;
        if (fieldType == Long.class || fieldType == long.class) {
            data = context.sp.getLong(keyInSpFile, 0);
        } else if (fieldType == Integer.class || fieldType == int.class) {
            data = context.sp.getInt(keyInSpFile, 0);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            data = context.sp.getBoolean(keyInSpFile, false);
        } else if (fieldType == Float.class || fieldType == float.class) {
            data = context.sp.getFloat(keyInSpFile, 0f);
        } else if (fieldType == String.class) {
            data = context.sp.getString(keyInSpFile, "");
        } else if (Set.class.isAssignableFrom(fieldType)) {
            data = context.sp.getStringSet(keyInSpFile, Collections.<String>emptySet());
        } else if (List.class.isAssignableFrom(fieldType)) {
            data = mGson.fromJson(context.sp.getString(keyInSpFile, "[]"), List.class);
        } else {
            return;
        }
        try {
            field.set(instance, data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String getInnerKeyOfCustomObj(String context, SerializedName fieldAnnotation) {
        return context + "_" + fieldAnnotation.value();
    }

    private java.lang.reflect.Field[] filterAnnotatedFields(java.lang.reflect.Field[] fields) {
        Vector<java.lang.reflect.Field> vector = new Vector<>();
        for (int i = 0; i < fields.length; i++) {
            java.lang.reflect.Field field = fields[i];
            field.setAccessible(true);
            if (field.getAnnotation(SerializedName.class) != null
                    && !Modifier.isTransient(field.getModifiers())
                    && !Modifier.isStatic(field.getModifiers())) {
                vector.add(field);
            }
        }
        java.lang.reflect.Field[] results = new java.lang.reflect.Field[vector.size()];
        vector.copyInto(results);
        return results;
    }

    @SuppressWarnings("unchecked")
    private void invokeSetterMethod(final SharedPreferenceProxyContext context, final Object... args) {
        if (args == null || mFieldNames == null) {
            throw new IllegalArgumentException("Field names and arguments length mismatch");
        }
        SharedPreferences.Editor editor = context.sp.edit();
        int setterIndex = 0;
        for (int i = 0; i < args.length; i++) {
            if (mParameterAnnotations[i] != null && mParameterAnnotations[i].length > 0) {
                // 这个位置的参数被 Key 注解标记，不做 set 处理
                continue;
            }
            Object arg = args[i];
            String keyForSetter = getKeyForStorage(setterIndex, args);
            // 如果参数为 null， 就删除该 key 值
            if (arg == null) {
                editor.remove(keyForSetter);
            } else {
                if (arg.getClass() == Long.class || arg.getClass() == long.class) {
                    editor.putLong(keyForSetter, (Long) arg);
                } else if (arg.getClass() == Integer.class || arg.getClass() == int.class) {
                    editor.putInt(keyForSetter, (Integer) arg);
                } else if (arg.getClass() == Boolean.class || arg.getClass() == boolean.class) {
                    editor.putBoolean(keyForSetter, (Boolean) arg);
                } else if (arg.getClass() == Float.class || arg.getClass() == float.class) {
                    editor.putFloat(keyForSetter, (Float) arg);
                } else if (arg instanceof String) {
                    editor.putString(keyForSetter, (String) arg);
                } else if (arg instanceof Set) {
                    editor.putStringSet(keyForSetter, (Set<String>) arg);
                } else if (arg instanceof List) {
                    editor.putString(keyForSetter, mGson.toJson(arg));
                } else {
                    setCustomObject(keyForSetter, arg, editor);
                }
                // 更新时间戳
                if (context.expireKeySet.contains(mFieldNames[setterIndex])) {
                    // update timestamp of expires key
                    context.sp.edit()
                            .putLong(getTimestampKeyByField(keyForSetter), System.currentTimeMillis())
                            .apply();
                }
            }
            setterIndex++;
        }
        editor.apply();
    }

    private enum MethodType {
        GETTER,
        SETTER
    }
}
