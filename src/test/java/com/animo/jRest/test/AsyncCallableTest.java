package com.animo.jRest.test;

import com.animo.jRest.util.AsyncCallable;
import com.animo.jRest.util.AsyncTask;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.support.ReflectionSupport;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AsyncCallableTest {

    private AsyncTask asyncTask;
    private AsyncCallable asyncCallable;
    String params = "testParams";

    @BeforeEach
    public void setUp() {
        asyncTask = Mockito.mock(AsyncTask.class);
        asyncCallable = Mockito.mock(AsyncCallable.class);
    }

    @Test
    public void asyncCallableTest() {
        String expectedParams = "testParams";
        asyncCallable = new AsyncCallable<>(expectedParams, asyncTask);
        assertEquals(expectedParams, asyncCallable.getParams(), "Params should be initialized correctly");
        assertEquals(asyncTask, asyncCallable.getAsyncTask(), "AsyncTask should be initialized correctly");
    }
}