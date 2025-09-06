package com.animo.jRest.util;

import com.animo.jRest.annotation.*;
import com.animo.jRest.model.APIRequestRecord;
import com.animo.jRest.model.RequestAuthentication;
import com.animo.jRest.model.RequestBean;
import com.animo.jRest.model.RequestProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class APIServiceHelper {

    private final static Logger logger = LogManager.getLogger(APIServiceHelper.class);

    public static class URLBuilder {

        private final Object[] requestArgs;
        private final Parameter[] parameters;
        private final StringBuilder finalUrl;
        private final RequestBean<Object> requestBean;

        public URLBuilder(final Object[] requestArgs,final Parameter[] parameters,final String baseUrl,final REQUEST request) {
            this.requestArgs = requestArgs;
            this.parameters = parameters;
            this.finalUrl = new StringBuilder(baseUrl).append(request.endpoint());
            this.requestBean = new RequestBean<>();
            this.requestBean.setRequestType(request.type());
        }

        public static URLBuilder builder(final Object[] requestArgs, final Parameter[] parameters, final String baseUrl, final REQUEST request) {
            return new URLBuilder(requestArgs,parameters,baseUrl,request);
        }

        public URLBuilder addPathParameters() throws Exception {
            for(int i = 0 ; i < parameters.length ; i++) {
                if(parameters[i].getAnnotation(PATH.class) != null) {
                    PATH path = (PATH) parameters[i].getAnnotation(PATH.class);
                    final String value = path.value();
                    final Pattern pattern = Pattern.compile("\\{" + value + "\\}");
                    final Matcher matcher = pattern.matcher(finalUrl);
                    int start = 0;
                    while(matcher.find(start)) {
                        finalUrl.replace(matcher.start(), matcher.end(), String.valueOf(requestArgs[i]));
                        start = matcher.start() + String.valueOf(requestArgs[i]).length();
                    }
                }
            }

            if(finalUrl.toString().contains("{") &&
                    finalUrl.toString().contains("}")) {
                throw new Exception("Undeclared PATH variable found ..Please declare them in the interface");
            }
            return this;
        }

        public URLBuilder addQueryParameters(APIRequestRecord apiRequestRecord) throws UnsupportedEncodingException {
            var params = prepareQueryParamMap(apiRequestRecord);
            if(params != null && !params.isEmpty()) {
                finalUrl.append("?");
                params.forEach((k, v) -> finalUrl.append(k).append("=").append(v).append("&"));
                finalUrl.deleteCharAt(finalUrl.length()-1);
            }
            return this;
        }

        private Map<String, String> prepareQueryParamMap(APIRequestRecord apiRequestRecord) throws UnsupportedEncodingException {
				/* put all the found query parameters in Query and QueryMap,
				into the paramters map to be converted into query string*/
            var queryParams = apiRequestRecord.queryParams();
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getAnnotation(Query.class) != null) {
                    if (queryParams == null) queryParams = new HashMap<>();
                    Query query = (Query) parameters[i].getAnnotation(Query.class);
                    String queryKey = query.value();
                    if (queryKey != null && !queryKey.isEmpty()) {

                        String queryValue = null;
                        try {
                            queryValue = (String) requestArgs[i];
                        } catch (ClassCastException ex) {
                            logger.error("Unable to add Query Params ", ex);
                            throw new InvalidParameterException("Query parameter should be passed in string format only ");
                        }

                        if (queryValue != null) {
                            if (!query.encoded()) {
                                queryKey = URLEncoder.encode(queryKey, StandardCharsets.UTF_8);
                                queryValue = URLEncoder.encode(queryValue, StandardCharsets.UTF_8);
                            }

                            queryParams.put(queryKey, queryValue);
                        }
                    }
                } else if (parameters[i].getAnnotation(QueryMap.class) != null) {
                    if (queryParams == null) queryParams = new HashMap<>();
                    QueryMap queryMap = (QueryMap) parameters[i].getAnnotation(QueryMap.class);
                    Map<String, String> queryMapValue = null;
                    try {
                        queryMapValue = (Map<String, String>) requestArgs[i];
                    } catch (ClassCastException ex) {
                        logger.error("Unable to add Query Params ", ex);
                        throw new InvalidParameterException("Query parameter should be passed in Map format only ");
                    }

                    if (queryMapValue != null && !queryMapValue.isEmpty()) {
                        queryParams.putAll(queryMapValue);
                    }
                }

            }
            logger.debug("Query queryParams fetched from Params " + queryParams);
            return queryParams;
        }

        //TODO: Needs refactoring
        public URLBuilder addHeaders(final Annotation headersAnnotation) {
            final HEADERS headers = (HEADERS) headersAnnotation;
            final String[] requestHeadersFromMethod;
            Map<String, String> requestHeadersMap = new HashMap<>();
            if(headers != null) {
                requestHeadersFromMethod = headers.value();

                logger.debug("Request Headers from Method {} " , Arrays.toString(requestHeadersFromMethod));
                requestHeadersMap = convertToHeadersMap(requestHeadersFromMethod);
            }

            final Map<String, String> requestHeadersFromParam = getParamHeaders();

            //String[] requestHeaders = concatenateHeaders(requestHeadersFromMethod,requestHeadersFromParam);
            logger.debug("Final Request Headers Map is {} " ,requestHeadersMap);
            requestHeadersMap.putAll(requestHeadersFromParam);
            requestBean.setHeaders(requestHeadersMap);
            return this;
        }

        private Map<String, String> getParamHeaders() {
            Map<String, String> paramValues = new HashMap<>();
            try {
                for(int i = 0,j = 0; i < parameters.length; i++) {
                    if(parameters[i].getAnnotation(HeaderMap.class)!=null) {
                        HeaderMap headerMap = parameters[i].getAnnotation(HeaderMap.class);
                        paramValues = (Map<String, String>) requestArgs[i];
                    }
                }
            }catch (ClassCastException ex) {
                logger.error("Unable to get ParamHeaders " + ex);
                throw new RuntimeException("Header Parameters should be passed in Map<key:value> format ");
            }


            logger.debug("Request Headers from Params {}" ,paramValues);
            return paramValues;
        }

        private Map<String, String> convertToHeadersMap(final String[] requestHeaders) {
            Map<String, String> headersMap = new HashMap<>();
            for(String header:requestHeaders) {
                if(!header.contains(":")) {
                    throw new RuntimeException("Header data invalid ...Should be using <key>:<value> String format " + header);
                }
                headersMap.put(header.split(":")[0], header.split(":")[1]);
            }
            return headersMap;
        }

        public URLBuilder addRequestBody(final REQUEST request) {
            if(request.type().equals(HTTP_METHOD.POST) ||
                    request.type().equals(HTTP_METHOD.PATCH) ||
                    request.type().equals(HTTP_METHOD.PUT)){
                for (int i = 0; i < parameters.length; i++) {
                    if(parameters[i].getAnnotation(Body.class)!=null){
                        Body body = (Body) parameters[i].getAnnotation(Body.class);
                        logger.debug("Going to set request body {}",requestArgs[i]);
                        if(requestArgs[i]==null){
                            throw new NullPointerException("Request Body cannot be Null");
                        }
                        //TODO: Add null check and throw exception
                        requestBean.setRequestObject(requestArgs[i]);
                    }
                }
            }
            return this;
        }

        public URLBuilder addAuthentication(final RequestAuthentication auth) {
            requestBean.setAuthentication(auth);
            return this;
        }

        public URLBuilder addProxy(final RequestProxy requestProxy) {
            requestBean.setProxy(requestProxy);
            return this;
        }

        public URLBuilder addFollowRedirects(FollowRedirects followRedirectsAnnotation) {
            if(followRedirectsAnnotation != null){
                requestBean.setFollowRedirects(followRedirectsAnnotation.value());
            }
            return this;
        }

        public URLBuilder addDisableSSLVerification(boolean disableSSLVerification) {
            requestBean.setDisableSSLVerification(disableSSLVerification);
            return this;
        }

        public RequestBean<Object> build() {
            requestBean.setUrl(finalUrl.toString());
            return requestBean;
        }
    }
}
