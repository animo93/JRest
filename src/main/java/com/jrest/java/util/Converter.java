package com.jrest.java.util;

import java.lang.reflect.Type;

public interface Converter {
    /**
     * Converts the given object to a string representation.
     *
     * @param object the object to convert
     * @return the string representation of the object
     */
    String toString(Object object);

    /**
     * Converts the given string to an object of the specified type.
     *
     * @param <T>   the type of the object
     * @param value the string value to convert
     * @param type  the class of the type to convert to
     * @return the converted object
     */
    <T> T fromString(String value, Type type);
}
