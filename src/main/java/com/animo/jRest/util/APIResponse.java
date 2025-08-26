package com.animo.jRest.util;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

//TODO: Convert to record
@Getter @Setter
public final class APIResponse<Response> {
    private Response response;
    private int responseCode;
    private Map<String, List<String>> responseHeaders;
    private Type responseType;
}
