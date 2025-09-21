package com.animo.jRest.model;

public record RequestProxyRecord(
        String url,
        String username,
        String password,
        int port) {}
