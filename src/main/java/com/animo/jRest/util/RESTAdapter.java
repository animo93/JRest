package com.animo.jRest.util;

import com.animo.jRest.model.RequestBean;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//@AllArgsConstructor
public class RESTAdapter<Response> implements APIClient<Response>{

    private final RESTConnector<Response> connector;

    public RESTAdapter(RequestBean<Object> requestBean, Type responseType){
        this.connector = new RESTConnector<>(requestBean,responseType);
    }
    //TODO: Check if multithreaded execution is helpful
    //TODO: Add support for virtual threads
    private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public APIResponse<Response> fetch() {
        return connector.fetch();
    }

    @Override
    public Future<APIResponse<Response>> fetchWithFuture() {
        return executor.submit(new SyncCallable<>(connector));
    }

    @Override
    public void fetchWithCallBack(APICallBack<Response> callBack) {
        CompletableFuture.supplyAsync(connector::fetch)
                .thenAccept(callBack::callBackOnSuccess)
                .exceptionally(e -> {
                    callBack.callBackOnFailure(e);
                    //TODO: Check if there is alternative to returning null
                    return null;
                });
    }
}
