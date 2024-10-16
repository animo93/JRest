package com.animo.jRest.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import com.animo.jRest.exception.IThrowingFunction;

public abstract class AsyncTask<Params,Result> {

    private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    protected abstract Result runInBackground(Params params) throws Exception;
    protected abstract void postExecute(Result result,Exception e);
    protected abstract void preExecute();


    Consumer<Params> executeLater = (params) ->{
        try {
            preExecute();
            Future<Result> future  = executor.submit(new AsyncCallable<>(params, this));
        } catch(Exception e) {
            throw e;
        } finally {
            executor.shutdown();
        }
    };

    IThrowingFunction<Params, Result> executeNow = (params) -> {
        preExecute();
        Future<Result> future = executor.submit(new SyncCallable<>(params, this));
        try {
            return future.get();
        } finally {
            executor.shutdown();
        }
    };

}
