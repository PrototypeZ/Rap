package io.github.jason1114.builtin.sharedpreference;

import com.google.gson.annotations.SerializedName;

import android.content.SharedPreferences;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import io.github.jason1114.annotation.Field;
import io.github.jason1114.library.ProxyContext;
import io.github.jason1114.library.ProxyMethod;

/**
 * Created by Jason on 2017/2/3.
 */

public class SharedPreferenceProxyMethod extends ProxyMethod {

    private Field mFieldAnnotation;
    private String[] mFieldNames;
    private MethodType mMethodType;
    private Class mReturnType;
    private HashMap<Class, java.lang.reflect.Field[]> typeFieldsCache = new HashMap<>();

    private java.lang.reflect.Field returnTypeFields;

    SharedPreferenceProxyMethod(Method method) {
        mFieldAnnotation = method.getAnnotation(Field.class);
        if (mFieldAnnotation == null) {
            throw new IllegalStateException("Each method should has a @Field annotation.");
        }
        mFieldNames = mFieldAnnotation.value();
        if (mFieldNames.length == 0) {
            throw new IllegalArgumentException("Field names should not be empty");
        }
        mReturnType = method.getReturnType();
        if (mReturnType == void.class) {
            // setter method
            mMethodType = MethodType.SETTER;
        } else {
            // getter method
            mMethodType = MethodType.GETTER;
        }
    }

    @Override
    public Object call(ProxyContext context, Object... args) {
        if (context instanceof SharedPreferenceProxyContext) {
            ((SharedPreferenceProxyContext) context).ensureMetaInfo();
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

    private Object invokeGetterMethod(SharedPreferenceProxyContext context, Object... args) {
        if (mReturnType == Long.class || mReturnType == long.class) {
            return context.sp.getLong(mFieldNames[0], 0);
        } else if (mReturnType == Integer.class || mReturnType == int.class) {
            return context.sp.getInt(mFieldNames[0], 0);
        } else if (mReturnType == Boolean.class || mReturnType == boolean.class) {
            return context.sp.getBoolean(mFieldNames[0], false);
        } else if (mReturnType == Float.class || mReturnType == float.class) {
            return context.sp.getFloat(mFieldNames[0], 0f);
        } else if (mReturnType == String.class) {
            return context.sp.getString(mFieldNames[0], "");
        } else if (mReturnType == Set.class) {
            return context.sp.getStringSet(mFieldNames[0], Collections.<String>emptySet());
        } else {
            return getCustomObject(context);
        }
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
                } else if (fieldType == Set.class) {
                    editor.putStringSet(keyInSpFile, (Set<String>) field.get(arg));
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
        } else if (fieldType == Set.class) {
            data = context.sp.getStringSet(keyInSpFile, Collections.<String>emptySet());
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
            if (field.getAnnotation(SerializedName.class) != null && !Modifier.isTransient(field.getModifiers())) {
                vector.add(field);
            }
        }
        java.lang.reflect.Field[] results = new java.lang.reflect.Field[vector.size()];
        vector.copyInto(results);
        return results;
    }

    @SuppressWarnings("unchecked")
    private void invokeSetterMethod(final SharedPreferenceProxyContext context, final Object... args) {
        if (args == null || mFieldNames == null || args.length != mFieldNames.length) {
            throw new IllegalArgumentException("Field names and arguments length mismatch");
        }
        SharedPreferences.Editor editor = context.sp.edit();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg.getClass() == Long.class || arg.getClass() == long.class) {
                editor.putLong(mFieldNames[i], (Long) arg);
            } else if (arg.getClass() == Integer.class || arg.getClass() == int.class) {
                editor.putInt(mFieldNames[i], (Integer) arg);
            } else if (arg.getClass() == Boolean.class || arg.getClass() == boolean.class) {
                editor.putBoolean(mFieldNames[i], (Boolean) arg);
            } else if (arg.getClass() == Float.class || arg.getClass() == float.class) {
                editor.putFloat(mFieldNames[i], (Float) arg);
            } else if (arg.getClass() == String.class) {
                editor.putString(mFieldNames[i], (String) arg);
            } else if (arg.getClass() == Set.class) {
                editor.putStringSet(mFieldNames[i], (Set<String>) arg);
            } else {
                setCustomObject(mFieldNames[i], arg, editor);
            }
        }
        editor.apply();
    }

    private enum MethodType {
        GETTER,
        SETTER
    }
}
