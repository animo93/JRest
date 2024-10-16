package com.animo.jRest.exception;

import java.util.concurrent.ExecutionException;

@FunctionalInterface
public interface IThrowingFunction<T, R> {
    R apply(T t) throws InterruptedException, ExecutionException;
}

