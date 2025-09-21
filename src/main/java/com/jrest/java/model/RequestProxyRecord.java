package com.jrest.java.model;

public record RequestProxyRecord(
        String url,
        String username,
        String password,
        int port) {}
