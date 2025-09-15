package com.animo.jRest.util;

import com.animo.jRest.model.RequestAuthentication;
import com.animo.jRest.model.APIRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.Map.Entry;
import java.util.Optional;

public class RESTConnector {

	private static final Logger logger = LogManager.getLogger(RESTConnector.class);
	private final APIRequest apiRequest;

	public RESTConnector(APIRequest apiRequest) {
		this.apiRequest = apiRequest;
	}

	public <Response> APIResponse<Response> fetch() throws Exception {
		if(apiRequest == null)
			return null;
		APIResponse<Response> apiResponse = new APIResponse<>();

		try {
            final URL url = new URL(this.apiRequest.url());
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(url.toURI());
            setRequestBody(builder);
            setHeaders(builder);
            setAuthentication(builder);

			try(HttpClient client = HttpClient.newBuilder()
					.followRedirects((apiRequest.followRedirects())? HttpClient.Redirect.ALWAYS : HttpClient.Redirect.NEVER)
					.proxy(apiRequest.proxy().isPresent() ? ProxySelector.of(new InetSocketAddress(apiRequest.proxy().get().getUrl(), apiRequest.proxy().get().getPort())) : ProxySelector.getDefault())
					.build()) {
                HttpResponse<String> httpResponse = client
                        .send(builder.build(), HttpResponse.BodyHandlers.ofString());

                apiResponse.setResponseHeaders(httpResponse.headers().map());
                apiResponse.setResponseCode(httpResponse.statusCode());

                String responseJson = getResponseBody(httpResponse);
                logger.debug("response json {}" ,responseJson);
                GsonConverter converter = ConverterFactory.getInstance().getGsonConverter();
                Response response = converter.fromString(responseJson, apiRequest.responseType());
                apiResponse.setResponse(response);
            } catch (Exception e) {
                logger.error("Unable to send request ", e);
                throw e;
            }
			//convertResponse(responseJson, apiResponse);
		} catch (Exception e) {
			logger.error("Could not make connection ", e);
			throw e;
		}
		return apiResponse;
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

	private void setRequestBody(HttpRequest.Builder requestBuilder) throws IOException {
		if(apiRequest.httpMethod().toString().equals("POST") || apiRequest.httpMethod().toString().equals("PATCH")
				|| apiRequest.httpMethod().toString().equals("PUT")) {
			Optional<Object> requestObjectOptional = apiRequest.requestObject();
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
				requestBuilder.method(apiRequest.httpMethod().toString(),
						HttpRequest.BodyPublishers.ofString(json));
			}//TODO Add else block for null values
		}else {
			requestBuilder.method(apiRequest.httpMethod().toString(), HttpRequest.BodyPublishers.noBody());
		}
	}

	private void setAuthentication(HttpRequest.Builder requestBuilder) {
		if(apiRequest.authentication().isPresent()) {
			final RequestAuthentication auth = apiRequest.authentication().get();
			if(auth.getUsername() != null && auth.getPassword() != null) {
				final String userPassword = auth.getUsername() + ":" + auth.getPassword();
				final String encodedAuthorization = Base64.encodeBase64String(userPassword.getBytes());
				requestBuilder.header("Authorization", "Basic " +
						encodedAuthorization.replaceAll("\n", ""));
			}
		}
		if(apiRequest.accessToken().isPresent())
			requestBuilder.header("Authorization", " token " + apiRequest.accessToken().get());
	}

	private void setHeaders(HttpRequest.Builder requestBuilder) {
		//Setting this to fix a bug in jdk which sets illegal "Accept" header
		//httpsURLConnection.setRequestProperty("Accept", "application/json");
		if(apiRequest.headers().isPresent()) {
			for(Entry<String, String> entry: apiRequest.headers().get().entrySet()) {
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
