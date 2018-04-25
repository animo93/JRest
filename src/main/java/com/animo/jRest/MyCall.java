package com.animo.jRest;

import java.lang.reflect.Type;

/**
 * Created by animo on 21/12/17.
 */

public class MyCall<Request,Response> {
    private Response responseBody;
    private int responseCode;
    private MyRequestBean<Request>  requestBean;
    private Type type;

    public MyRequestBean<Request> getRequestBean() {
        return requestBean;
    }

    public void setRequestBean(MyRequestBean<Request> requestBean) {
        this.requestBean = requestBean;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Response getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Response responseBody) {
        this.responseBody = responseBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void callMeNow(MyCallBack<Request,Response> callBack){

        MyAsyncTask<Request,Response> asyncTask = new MyAsyncTask<>(requestBean,type,callBack);
        asyncTask.execute(requestBean);
    }

}
