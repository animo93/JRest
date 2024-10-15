package com.animo.jRest.test;

import com.animo.jRest.util.AsyncCallable;
import com.animo.jRest.util.AsyncTask;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static org.junit.jupiter.api.Assertions.*;

class AsyncCallableTest {

    @Test
    void testAsyncCallableSuccess() throws Exception {
        // Define the AsyncTask with success scenario
        AsyncTask<String, Integer> asyncTask = new AsyncTask<>() {
            @Override
            public Integer runInBackground(String params) {
                // Simulate task: counting characters
                return params.length();
            }

            @Override
            public void postExecute(Integer result, Exception exception) {
                assertNull(exception); // Ensure there are no exceptions
                assertEquals(13, result); // The length of "Hello, World!" is 13
            }

            @Override
            protected void preExecute() {

            }
        };

        // Create an AsyncCallable with valid input
        AsyncCallable<String, Integer> asyncCallable = new AsyncCallable<>("Hello, World!", asyncTask);

        // Execute the AsyncCallable with ExecutorService
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(asyncCallable);

        // Wait for the result and verify
        Integer result = future.get();
        assertEquals(13, result); // Ensure the result matches

        executor.shutdown();
    }
}
