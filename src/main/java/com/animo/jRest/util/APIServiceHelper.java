package com.animo.jRest.util;

import com.animo.jRest.annotation.PATH;

import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIServiceHelper {

    private static void addPathParameters(final Object[] args, final StringBuilder urlBuilder, final Parameter[] parameters)
            throws Exception {
        for(int i = 0 ; i < parameters.length ; i++) {
            if(parameters[i].getAnnotation(PATH.class) != null) {
                PATH path = (PATH) parameters[i].getAnnotation(PATH.class);
                final String value = path.value();
                final Pattern pattern = Pattern.compile("\\{" + value + "\\}");
                final Matcher matcher = pattern.matcher(urlBuilder);
                int start = 0;
                while(matcher.find(start)) {
                    urlBuilder.replace(matcher.start(), matcher.end(), String.valueOf(args[i]));
                    start = matcher.start() + String.valueOf(args[i]).length();
                }
            }
        }

        if(urlBuilder.toString().contains("{") &&
                urlBuilder.toString().contains("}")) {
            throw new Exception("Undeclared PATH variable found ..Please declare them in the interface");
        }
    }
}
