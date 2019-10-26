package com.animo.jRest.util;

/**
 * Created by animo on 21/12/17.
 */

public interface APICallBack<Request,Response> {
    void callBackOnSuccess(APICall<Request,Response> myCall);
    void callBackOnFailure(Exception e);
}
