package com.jrest.java.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jrest.java.api.APIResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.*;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

public final class RESTClient implements APIClient {

    private static final Logger logger = LogManager.getLogger(RESTClient.class);

    @Override
    public <Response> APIResponse<Response> fetch(final APIRequestRecord apiRequestRecord) throws Exception {
        return sendRequest(apiRequestRecord);
    }

    private <Response> APIResponse<Response> sendRequest(final APIRequestRecord apiRequestRecord) throws Exception {
        if(apiRequestRecord == null)
            return null;

        try {
            final URL url = new URL(apiRequestRecord.url());
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(url.toURI());
            setRequestBody(builder,apiRequestRecord);
            setHeaders(builder,apiRequestRecord);
            setAuthentication(builder,apiRequestRecord);

            try(HttpClient client = HttpClient.newBuilder()
                    .followRedirects((apiRequestRecord.followRedirects())? HttpClient.Redirect.ALWAYS : HttpClient.Redirect.NEVER)
                    .proxy(apiRequestRecord.proxy().isPresent() ? ProxySelector.of(new InetSocketAddress(apiRequestRecord.proxy().get().url(), apiRequestRecord.proxy().get().port())) : ProxySelector.getDefault())
                    .build()) {
                HttpResponse<String> httpResponse = client
                        .send(builder.build(), HttpResponse.BodyHandlers.ofString());

                String responseJson = getResponseBody(httpResponse);
                logger.debug("response json {}" ,responseJson);
                GsonConverter converter = ConverterFactory.getInstance().getGsonConverter();
                Response response = converter.fromString(responseJson, apiRequestRecord.responseType());

                return new APIResponse<>(
                        response,
                        httpResponse.statusCode(),
                        httpResponse.headers().map()
                );
            } catch (Exception e) {
                logger.error("Unable to send request ", e);
                throw e;
            }
            //convertResponse(responseJson, apiResponse);
        } catch (Exception e) {
            logger.error("Could not make connection ", e);
            throw e;
        }
    }

    //TODO: Inject Converter (String , Json & GSON) as a dependency
    /*
	private void convertResponse(String repoJson, APIResponse<Response> apiResponse) throws Exception{
		Gson gson = new Gson();
		try {
			if(repoJson != null) {

				logger.debug("repoJson {}" ,repoJson);
				//TODO: Never tested
				if(!outputIsJson(repoJson)) {
					apiResponse.setResponse((Response) repoJson);
				} else {
					logger.debug("type {}" , type.getClass());
					final ObjectMapper mapper = new ObjectMapper();
					final Class<?> t = type2Class(type);
					Response res = (Response) mapper.readValue(repoJson, t);

					apiResponse.setResponse(res);
				}

			}
		} catch(Exception e) {
			logger.error("Error in json conversion ", e);
			throw e;
		}
	}*/

    //TODO: Return optional
    private String getResponseBody(HttpResponse<String> httpResponse) throws Exception {
        try {
            return httpResponse.body();
        }catch (Exception e){
            logger.error("Unable to get Response Body ",e);
            throw e;
        }
    }

    private void setRequestBody(final HttpRequest.Builder requestBuilder,final APIRequestRecord apiRequestRecord) throws IOException {
        if(apiRequestRecord.httpMethod().toString().equals("POST") || apiRequestRecord.httpMethod().toString().equals("PATCH")
                || apiRequestRecord.httpMethod().toString().equals("PUT")) {
            Optional<Object> requestObjectOptional = apiRequestRecord.requestObject();
            if(requestObjectOptional.isPresent()) {
                var requestObject = requestObjectOptional.get();
                StringBuilder builder = new StringBuilder();
                if(requestObject instanceof ParameterizedType) {
                    builder.append(new Gson().toJson(requestObject, TypeToken.getParameterized(requestObject.getClass(),String.class).getType()));
                }else{
                    builder.append(new Gson().toJson(requestObject));
                }
                final String json = builder.toString();
                logger.debug("request json {}" ,json);
                requestBuilder.method(apiRequestRecord.httpMethod().toString(),
                        HttpRequest.BodyPublishers.ofString(json));
            }//TODO Add else block for null values
        }else {
            requestBuilder.method(apiRequestRecord.httpMethod().toString(), HttpRequest.BodyPublishers.noBody());
        }
    }

    private void setAuthentication(final HttpRequest.Builder requestBuilder,final APIRequestRecord apiRequestRecord) {
        if(apiRequestRecord.authentication().isPresent()) {
            final RequestAuthenticationRecord auth = apiRequestRecord.authentication().get();
            if(auth.username().isPresent() && auth.password().isPresent()) {
                final String userPassword = auth.username().get() + ":" + auth.password().get();
                final String encodedAuthorization = Base64.encodeBase64String(userPassword.getBytes());
                requestBuilder.header("Authorization", "Basic " +
                        encodedAuthorization.replaceAll("\n", ""));
            }
        }
        if(apiRequestRecord.accessToken().isPresent())
            requestBuilder.header("Authorization", " token " + apiRequestRecord.accessToken().get());
    }

    private void setHeaders(final HttpRequest.Builder requestBuilder,final APIRequestRecord apiRequestRecord) {
        //Setting this to fix a bug in jdk which sets illegal "Accept" header
        //httpsURLConnection.setRequestProperty("Accept", "application/json");
        if(apiRequestRecord.headers().isPresent()) {
            for(Map.Entry<String, String> entry: apiRequestRecord.headers().get().entrySet()) {
                requestBuilder.header(entry.getKey(), entry.getValue());
            }
        }
    }

    private Class<?> type2Class(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof GenericArrayType) {
            // having to create an array instance to get the class is kinda nasty
            // but apparently this is a current limitation of java-reflection concerning array classes.
            return Array.newInstance(type2Class(((GenericArrayType)type).getGenericComponentType()), 0).getClass(); // E.g. T[] -> T -> Object.class if <T> or Number.class if <T extends Number & Comparable>
        } else if (type instanceof ParameterizedType) {
            return type2Class(((ParameterizedType) type).getRawType()); // Eg. List<T> would return List.class
        } else if (type instanceof TypeVariable) {
            final Type[] bounds = ((TypeVariable<?>) type).getBounds();
            return bounds.length == 0 ? Object.class : type2Class(bounds[0]); // erasure is to the left-most bound.
        } else if (type instanceof WildcardType) {
            final Type[] bounds = ((WildcardType) type).getUpperBounds();
            return bounds.length == 0 ? Object.class : type2Class(bounds[0]); // erasure is to the left-most upper bound.
        } else {
            throw new UnsupportedOperationException("cannot handle type class: " + type.getClass());
        }
    }

    private boolean outputIsJson(String repoJson) {

        return repoJson != null && repoJson.startsWith("{");
    }
}
