package com.jrest.java.core;

/**
 * Immutable record to hold RequestProxy configuration.<br>
 * This is created per API method invocation
 * @param url
 * @param username
 * @param password
 * @param port
 */
public record RequestProxyRecord(
        String url,
        String username,
        String password,
        int port) {}
