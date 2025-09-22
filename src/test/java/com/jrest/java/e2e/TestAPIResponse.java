package com.jrest.java.e2e;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class TestAPIResponse {

    private Map<String,String> args;
    private Map<String,String> headers;
    private String url;
}
