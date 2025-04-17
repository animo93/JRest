package com.animo.jRest.util;

import com.animo.jRest.model.RequestBean;

import java.util.concurrent.*;

public abstract class AsyncTask<Response> {

    //TODO: Check if multithreaded execution is helpful
    private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    protected abstract APIResponse<Response> runInBackground(RequestBean<Object> requestBean);
    //TODO: this method needs to redesigned as a Consumer to be supplied along with callmeNow and callmeLater function
    protected abstract void postExecute(Response result, Exception e);
    //TODO: this method needs to redesigned as a Consumer to be supplied along with callmeNow and callmeLater function
    protected abstract void preExecute();


    public final void executeLater(RequestBean<Object> params, APICallBack<Response> callback){
        CompletableFuture.supplyAsync(() -> this.runInBackground(params))
                .thenAccept(callback::callBackOnSuccess)
                .exceptionally(e -> {
                    callback.callBackOnFailure(e);
                    return null;
                });
    };

    public final APIResponse<Response> executeNow(RequestBean<Object> params) throws Exception {
        try {
            Future<APIResponse<Response>> future = executor.submit(new SyncCallable<>(params,this));
            return future.get();
        } catch(Exception e) {
            throw e;
        } finally {
            executor.shutdown();
        }
    }

}
