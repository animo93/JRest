package com.animo.jRest.util;

import java.util.concurrent.Future;
//TODO: Add documentation
public interface APIClient {

    <Response> APIResponse<Response> fetch() throws Exception;
    <Response> Future<APIResponse<Response>> fetchWithFuture();
    <Response> void fetchWithCallBack(APICallBack<Response> callBack);
}
