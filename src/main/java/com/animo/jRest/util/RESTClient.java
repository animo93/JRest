package com.animo.jRest.util;

import com.animo.jRest.model.APIRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RESTClient implements APIClient {

    private final RESTConnector connector;

    public RESTClient(APIRequest apiRequest){
        this.connector = new RESTConnector(apiRequest);
    }
    //TODO: Check if multithreaded execution is helpful
    //TODO: Add support for virtual threads
    private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public <Response> APIResponse<Response> fetch() throws Exception {
        return connector.fetch();
    }

    @Override
    public <Response> Future<APIResponse<Response>> fetchWithFuture() {
        return executor.submit(new SyncCallable<>(connector));
    }

    @Override
    public <Response> void fetchWithCallBack(APICallBack<Response> callBack) {
        CompletableFuture.supplyAsync(() -> {
                    try {
                        return (APIResponse<Response>) connector.fetch();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
        })
                .thenAccept(callBack::callBackOnSuccess)
                .exceptionally(e -> {
                    callBack.callBackOnFailure(e);
                    //TODO: Check if there is alternative to returning null
                    return null;
                });
    }
}
