package com.animo.jRest.util;

public final class ConverterFactory {

    private static ConverterFactory instance;
    private static GsonConverter gsonConverter;

    public static ConverterFactory getInstance() {
        if(instance == null) {
            instance = new ConverterFactory();
        }
        return instance;
    }
    private ConverterFactory() {}

    public GsonConverter getGsonConverter() {
        if(gsonConverter == null) {
            gsonConverter = new GsonConverter();
        }
        return gsonConverter;
    }

}
