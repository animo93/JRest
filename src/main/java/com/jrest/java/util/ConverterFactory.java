package com.jrest.java.util;

/**
 * Factory class to provide instances of various converters
 */
public final class ConverterFactory {

    private static ConverterFactory instance;
    private static GsonConverter gsonConverter;

    /**
     * Provides a singleton instance of ConverterFactory
     * @return ConverterFactory instance
     */
    public static ConverterFactory getInstance() {
        if(instance == null) {
            instance = new ConverterFactory();
        }
        return instance;
    }
    private ConverterFactory() {}

    /**
     * Provides a singleton instance of GsonConverter
     * @return GsonConverter instance
     */
    public GsonConverter getGsonConverter() {
        if(gsonConverter == null) {
            gsonConverter = new GsonConverter();
        }
        return gsonConverter;
    }

}
