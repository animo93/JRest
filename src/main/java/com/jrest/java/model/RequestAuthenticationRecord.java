package com.jrest.java.model;

import java.util.Optional;

/**
 * Immutable record to hold RequestAuthentication configuration.<br>
 * This is created per API method invocation
 * @param username
 * @param password
 * @param encryptionAlgo
 * @param encryptionKey
 */
public record RequestAuthenticationRecord(
        Optional<String> username,
        Optional<String> password,
        Optional<String> encryptionAlgo,
        Optional<String> encryptionKey) {}
