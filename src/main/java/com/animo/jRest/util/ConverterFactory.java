package com.animo.jRest.util;

public final class ConverterFactory {

    private static ConverterFactory instance;

    public static ConverterFactory getInstance() {
        if(instance == null) {
            instance = new ConverterFactory();
        }
        return instance;
    }
    private ConverterFactory() {}

    public static GsonConverter getGsonConverter() {
        return new GsonConverter();
    }

}
