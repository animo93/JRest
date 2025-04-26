package com.animo.jRest.util;

import java.util.concurrent.Future;

public interface APIClient<Response> {

    APIResponse<Response> fetch();
    Future<APIResponse<Response>> fetchWithFuture();
    void fetchWithCallBack(APICallBack<Response> callBack);
}
