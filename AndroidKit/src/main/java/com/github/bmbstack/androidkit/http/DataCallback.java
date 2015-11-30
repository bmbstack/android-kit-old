package com.github.bmbstack.androidkit.http;

import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class DataCallback<T> {
    public Type mType;

    public static final DataCallback<String> DEFAULT_RESULT_CALLBACK = new DataCallback<String>() {
        @Override
        public void onFailure(Request request, Exception e) {
        }

        @Override
        public void onSuccess(String response) {
        }
    };

    public DataCallback() {
        mType = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public void onStart(Request request) {
    }

    public void onProgress(float progress) {
    }

    public abstract void onFailure(Request request, Exception e);

    public abstract void onSuccess(T response);

    public void onFinish() {

    }
}