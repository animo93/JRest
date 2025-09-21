package com.jrest.java.core;

public record RequestProxyRecord(
        String url,
        String username,
        String password,
        int port) {}
