package com.animo.jRest;

/**
 * Created by animo on 21/12/17.
 */

public interface MyCallBack<Request,Response> {
    void callBackOnSuccess(MyCall<Request,Response> myCall);
    void callBackOnFailure(Exception e);
}
