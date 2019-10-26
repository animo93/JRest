package com.animo.jRest.util;

import java.lang.reflect.Type;

/**
 * Created by animo on 21/12/17.
 */

public class APICall<Request,Response> {
    private Response responseBody;
    private int responseCode;
    private RequestBean<Request>  requestBean;
    private Type type;

    public RequestBean<Request> getRequestBean() {
        return requestBean;
    }

    public void setRequestBean(RequestBean<Request> requestBean) {
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

    public APICall<Request,Response> callMeNow() throws Exception{

        APIAsyncTask<Request,Response> asyncTask = new APIAsyncTask<>(requestBean,type);
        return asyncTask.executeNow(requestBean);
    }
    
    public void callMeLater(APICallBack<Request, Response> callBack) throws Exception {
    	APIAsyncTask<Request,Response> asyncTask = new APIAsyncTask<>(requestBean,type,callBack);
        asyncTask.executeLater(requestBean);
    }

}
