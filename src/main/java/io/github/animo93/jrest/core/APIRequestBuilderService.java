package io.github.animo93.jrest.core;

import io.github.animo93.jrest.api.APICallBack;
import io.github.animo93.jrest.api.APIResponse;
import io.github.animo93.jrest.api.HTTP_METHOD;
import io.github.animo93.jrest.api.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for building the APIRequestRecord object.<br>
 * It takes the method parameters and annotations and builds the final request object
 */
public final class APIRequestBuilderService {

    private final static Logger logger = LogManager.getLogger(APIRequestBuilderService.class);

    private final Object[] requestArgs;
    private final Parameter[] parameters;
    private final StringBuilder finalUrl;
    private final HTTP_METHOD httpMethod;
    private final Type returnType;
    private Map<String, String> requestHeadersMap;
    private Object requestBody;
    private RequestAuthenticationRecord auth;
    private RequestProxyRecord requestProxyRecord;
    private boolean followRedirects;
    private boolean disableSSLVerification;
    private Type responseType;
    private APICallBack<?> callBack;

    //TODO: Create a custom object that contains a map of
    private APIRequestBuilderService(final Object[] requestArgs, final Parameter[] parameters, final String baseUrl, final REQUEST request, final Type returnType) {
        this.requestArgs = requestArgs;
        this.parameters = parameters;
        this.finalUrl = new StringBuilder(baseUrl).append(request.endpoint());
        this.httpMethod = request.type();
        this.returnType = returnType;
    }

    public static APIRequestBuilderService builder(final Object[] requestArgs, final Parameter[] parameters, final String baseUrl, final REQUEST request, final Type returnType) {
        return new APIRequestBuilderService(requestArgs,parameters,baseUrl,request,returnType);
    }

    public APIRequestBuilderService addPathParameters() throws Exception {
        for(int i = 0 ; i < parameters.length ; i++) {
            if(parameters[i].getAnnotation(Path.class) != null) {
                Path path = (Path) parameters[i].getAnnotation(Path.class);
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

    public APIRequestBuilderService addQueryParameters(Map<String,String> queryParams) throws UnsupportedEncodingException {
        var params = prepareQueryParamMap(queryParams);
        if(params != null && !params.isEmpty()) {
            finalUrl.append("?");
            params.forEach((k, v) -> finalUrl.append(k).append("=").append(v).append("&"));
            finalUrl.deleteCharAt(finalUrl.length()-1);
        }
        return this;
    }

    private Map<String, String> prepareQueryParamMap(Map<String,String> queryParams) throws UnsupportedEncodingException {
        /* put all the found query parameters in Query and QueryMap, into the paramters map to be converted into query string*/
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
    public APIRequestBuilderService addHeaders(final Annotation headersAnnotation) {
        final Headers headers = (Headers) headersAnnotation;
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
        this.requestHeadersMap = requestHeadersMap;
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

    public APIRequestBuilderService addRequestBody(final REQUEST request) {
        //TODO: Should we only allow request body for GET Methods ?
        if(request.type().equals(HTTP_METHOD.POST) ||
                request.type().equals(HTTP_METHOD.PATCH) ||
                request.type().equals(HTTP_METHOD.PUT)) {
            for (int i = 0; i < parameters.length; i++) {
                if(parameters[i].getAnnotation(Body.class)!=null){
                    Body body = (Body) parameters[i].getAnnotation(Body.class);
                    logger.debug("Going to set request body {}",requestArgs[i]);
                    if(requestArgs[i]==null){
                        throw new NullPointerException("Request Body cannot be Null");
                    }
                    //TODO: Add null check and throw exception
                    this.requestBody = requestArgs[i];
                }
            }
        }
        return this;
    }

    public APIRequestBuilderService addAuthentication(final RequestAuthenticationRecord auth) {
        this.auth = auth;
        return this;
    }

    public APIRequestBuilderService addProxy(final RequestProxyRecord requestProxyRecord) {
        this.requestProxyRecord = requestProxyRecord;
        return this;
    }

    public APIRequestBuilderService addFollowRedirects(FollowRedirects followRedirectsAnnotation) {
        if(followRedirectsAnnotation != null){
            this.followRedirects = followRedirectsAnnotation.value();
        }
        return this;
    }

    public APIRequestBuilderService addDisableSSLVerification(boolean disableSSLVerification) {
        this.disableSSLVerification = disableSSLVerification;
        return this;
    }

    public APIRequestBuilderService addResponseType() throws Exception {
        final Type returnType = this.returnType;
        if(returnType instanceof ParameterizedType pType &&
                (pType.getRawType().equals(APIResponse.class) ||
                        pType.getRawType().equals(Future.class))) {
            this.responseType = getResponseType(pType);
        } else if (returnType.getTypeName().equals("void")) {
            addCallback();
        } else{
            throw new IllegalArgumentException("Method return type must be of type APIResponse or Future<APIResponse> or void");
        }
        return this;
    }

    private Class<?> getResponseType(ParameterizedType pType) throws Exception {
        var responseType = pType.getActualTypeArguments()[0];
        Class<?> responseClass;
        if(responseType instanceof ParameterizedType){
            responseClass = (Class<?>) ((ParameterizedType) responseType).getRawType();
        } else if (responseType instanceof Class){
            responseClass = (Class<?>) responseType;
        } else{
            throw new Exception("Invalid Response Type");
        }
        return responseClass;
    }

    private void addCallback() {
        if(returnType.getTypeName().equals("void")) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getType().equals(APICallBack.class)) {
                    Type callBackType = parameters[i].getParameterizedType();
                    if(callBackType instanceof ParameterizedType pType && pType.getActualTypeArguments().length == 1){
                        this.responseType = pType.getActualTypeArguments()[0];
                        this.callBack = ((APICallBack<?>) requestArgs[i]);
                    } else{
                        throw new RuntimeException("Invalid CallBack Type");
                    }
                }
            }
            if(this.callBack == null) {
                throw new RuntimeException("Callback parameter not found");
            }
        }
    }

    public APIRequestRecord build() {
        return new APIRequestRecord(
                Optional.empty(),
                this.httpMethod,
                finalUrl.toString(),
                requestBody != null ? Optional.of(requestBody) : Optional.empty(),
                auth != null ? Optional.of(auth) : Optional.empty(),
                requestProxyRecord != null ? Optional.of(requestProxyRecord) : Optional.empty(),
                requestHeadersMap != null && !requestHeadersMap.isEmpty() ? Optional.of(requestHeadersMap) : Optional.empty(),
                disableSSLVerification,
                followRedirects,
                callBack !=null ? Optional.of(callBack) : Optional.empty(),
                this.returnType,
                this.responseType
        );
    }
}
