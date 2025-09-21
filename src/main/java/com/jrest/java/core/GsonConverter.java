package com.jrest.java.core;

import com.google.gson.Gson;

import java.lang.reflect.Type;
/**
 * Implementation of Converter interface using Gson library for JSON serialization and deserialization.
 */
public final class GsonConverter implements Converter {

    private static final Gson gson = new Gson();

    @Override
    public String toString(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T fromString(String value, Type type) {
        return gson.fromJson(value, type);
    }
}
