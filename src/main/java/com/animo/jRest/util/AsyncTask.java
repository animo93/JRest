package com.animo.jRest.util;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AsyncTask<Params,Result> {

    //TODO: Check if multithreaded execution is helpful
    private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    protected abstract Result runInBackground(Params params);
    //TODO: this method needs to redesigned as a Consumer to be supplied along with callmeNow and callmeLater function
    protected abstract void postExecute(Result result,Exception e);
    //TODO: this method needs to redesigned as a Consumer to be supplied along with callmeNow and callmeLater function
    protected abstract void preExecute();


    public void executeLater(Params params, APICallBack<Result> callback){
        CompletableFuture.supplyAsync(() -> {
            Result result = this.runInBackground(params);
            return result;
        }).thenAccept(result -> {
            callback.callBackOnSuccess(result);
        }).exceptionally(e -> {
            callback.callBackOnFailure(e);
            return null;
        });
    };

    public Result executeNow(Params params) throws Exception {
        try {
            Future<Result> future = executor.submit(new SyncCallable<>(params, this));
            return future.get();
        } catch(Exception e) {
            throw e;
        } finally {
            executor.shutdown();
        }
    }

}
