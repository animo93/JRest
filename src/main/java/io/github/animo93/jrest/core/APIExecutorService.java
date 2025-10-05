package io.github.animo93.jrest.core;

import io.github.animo93.jrest.api.APICallBack;
import io.github.animo93.jrest.api.APIResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * A singleton service class to execute API calls.
 * <p>Calls may be executed synchronously , asynchronously or with callback.
 */
public final class APIExecutorService {

    private static volatile APIExecutorService instance;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private APIExecutorService() {}

    public static APIExecutorService getInstance() {
        if(instance == null) {
            synchronized (APIExecutorService.class) {
                if(instance == null) {
                    instance = new APIExecutorService();
                }
            }
        }
        return instance;
    }

    /**
     * If the return type is of type APIResponse , execute synchronously
     * If the return type is of type Future<APIResponse> , execute asynchronously and return a Future
     * If the return type is of type void and has APICallBack as parameter , execute asynchronously and return via callback
     * **/
    public Optional<?> executeAPI(final APIRequestRecord apiRequestRecord) throws Exception {
        final Type returnType = apiRequestRecord.returnType();
        if(returnType instanceof ParameterizedType pType) {
            if(pType.getRawType().equals(APIResponse.class)) {
                return Optional.of(execute(apiRequestRecord));
            }else if(pType.getRawType().equals(Future.class)) {
                return Optional.of(executeWithFuture(apiRequestRecord));
            }else {
                throw new IllegalArgumentException("Method return type must be of type APIResponse or Future<APIResponse>");
            }
        }else if(returnType.getTypeName().equals("void")) {
            executeWithCallBack(apiRequestRecord.callBack().orElseThrow(() -> new IllegalArgumentException("APICallBack parameter required for void return type")), apiRequestRecord);
        }else {
            throw new IllegalArgumentException("Method return type must be of type APIResponse or Future<APIResponse>");
        }
        return Optional.empty();
    }

    /**
     * Synchronous implementation of {@link APIExecutorService APIRequest} , which invokes a blocking call to webserver
     * . And waits for the APIRequest to complete
     *
     * @return {@link APIResponse<>}
     * @throws Exception Exception if issue with asyncTask executeNow method
     */
    private <Response> APIResponse<Response> execute(final APIRequestRecord apiRequestRecord) throws Exception {

        final APIClient client = new RESTClient();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (APIResponse<Response>) client.fetch(apiRequestRecord);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        },executor).get();
    }

    /**
     * Asynchronous implementation of {@link APIExecutorService APIRequest} via Future.
     * @return a Future of {@link APIResponse}
     */
    private <Response> Future<APIResponse<Response>> executeWithFuture(final APIRequestRecord apiRequestRecord) {
        final APIClient client = new RESTClient();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return client.fetch(apiRequestRecord);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        },executor);
    }

    /**
     * Asynchronous implementation of {@link APIExecutorService APIRequest} , which invokes a non-blocking call to webserver
     * . It accepts {@link APICallBack APICallBack} as a parameter
     *
     * @param callBack APICallBack
     */
    @SuppressWarnings("unchecked")
    private <Response> void executeWithCallBack(APICallBack<Response> callBack, final APIRequestRecord apiRequestRecord) {
        
        final APIClient client = new RESTClient();
        CompletableFuture.supplyAsync(() -> {
                    try {
                        return (APIResponse<Response>) client.fetch(apiRequestRecord);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },executor)
                .thenAccept(callBack::callBackOnSuccess)
                .exceptionally(e -> {
                    callBack.callBackOnFailure(e);
                    throw new CompletionException(e);
                });
    }
}
