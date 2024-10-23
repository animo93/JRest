package com.animo.jRest.util;

import com.animo.jRest.model.RequestAuthentication;
import com.animo.jRest.model.RequestBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map.Entry;

public class APIAsyncTask<Request,Response> extends AsyncTask<RequestBean<Request>,APICall<Request,Response>>{

	private static final Logger logger = LogManager.getLogger(APIAsyncTask.class);
	protected APICallBack<APICall<Request, Response>> myCallBack;
	private final RequestBean<Request> bean;
	private final Type type;

	public APIAsyncTask(RequestBean<Request> bean, Type type, APICallBack<APICall<Request, Response>> myCallBack) {
		this.bean = bean;
		this.type = type;
		this.myCallBack = myCallBack;
	}

	public APIAsyncTask(RequestBean<Request> bean, Type type) {
		this.bean = bean;
		this.type = type;
	}

	@SneakyThrows
    @Override
	protected APICall<Request,Response> runInBackground(RequestBean<Request> myRequestBean) {
		if(myRequestBean == null)
			return null;
		final RequestBean<Request> bean = myRequestBean;
		final HttpURLConnection httpURLConnection = null;
		final BufferedReader reader = null;
		final String repoJson = null;
		APICall<Request, Response> myCall = new APICall<>();
		final URL url = new URL(this.bean.getUrl());
		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(url.toURI());
		setRequestBody(builder);
		setHeaders(builder);
		setAuthentication(builder);

		try {
			HttpResponse<String> response = HttpClient.newBuilder()
					.followRedirects((bean.isFollowRedirects())? HttpClient.Redirect.ALWAYS : HttpClient.Redirect.NEVER)
					.proxy(bean.getProxy() != null ? ProxySelector.of(new InetSocketAddress(bean.getProxy().getUrl(), bean.getProxy().getPort())) : ProxySelector.getDefault())
					.build()
					.send(builder.build(), HttpResponse.BodyHandlers.ofString());
			myCall.setResponseHeaders(response.headers().map());
			myCall.setResponseCode(response.statusCode());
			String responseJson = getResponseBody(response.statusCode(),response);
			convertResponse(responseJson, myCall);
		} catch (Exception e) {
			logger.error("Could not make connection ", e);
			throw e;
		}
		return myCall;
	}

	private void convertResponse(String repoJson, APICall<Request, Response> myCall) throws Exception{
		Gson gson = new Gson();
		try {
			if(repoJson != null) {

				logger.debug("repoJson {}" ,repoJson);

				if(!outputIsJson(repoJson)) {
					myCall.setResponseBody((Response) repoJson);
				} else {
					logger.debug("type {}" , type.getClass());
					final ObjectMapper mapper = new ObjectMapper();
					final Class<?> t = type2Class(type);
					Response res = (Response) mapper.readValue(repoJson, t);

					myCall.setResponseBody(res);
				}

			}
		} catch(Exception e) {
			logger.error("Error in json conversion ", e);
			throw e;
		}
	}

	private String getResponseBody(int status,HttpResponse<String> httpResponse) throws Exception {
		String repoJson = null;
		BufferedReader reader = null;
		try {
			repoJson = httpResponse.body();
		}catch (Exception e){
			logger.error("Unable to get Response Body ",e);
			throw e;
		}
		return repoJson;
	}

	private void setRequestBody(HttpRequest.Builder requestBuilder) throws IOException {
		if(bean.getRequestType().toString().equals("POST") || bean.getRequestType().toString().equals("PATCH")
				|| bean.getRequestType().toString().equals("PUT")) {
			Request requestObject = bean.getRequestObject();
			if(null != requestObject) {
				StringBuilder builder = new StringBuilder();
				if(requestObject instanceof ParameterizedType) {
					builder.append(new Gson().toJson(requestObject, TypeToken.getParameterized(requestObject.getClass(),String.class).getType()));
				}else{
					builder.append(new Gson().toJson(requestObject));
				}
				final String json = builder.toString();
				logger.debug("request json {}" ,json);
				requestBuilder.method(bean.getRequestType().toString(),
						HttpRequest.BodyPublishers.ofString(json));
			}
		}else {
			requestBuilder.method(bean.getRequestType().toString(), HttpRequest.BodyPublishers.noBody());
		}
	}

	private void setAuthentication(HttpRequest.Builder requestBuilder) {
		if(bean.getAuthentication() != null) {
			final RequestAuthentication auth = bean.getAuthentication();
			if(auth.getUsername() != null && auth.getPassword() != null) {
				final String userPassword = auth.getUsername() + ":" + auth.getPassword();
				final String encodedAuthorization = Base64.encodeBase64String(userPassword.getBytes());
				requestBuilder.header("Authorization", "Basic " +
						encodedAuthorization.replaceAll("\n", ""));
			}
		}
		if(bean.getAccessToken() != null)
			requestBuilder.header("Authorization", " token " + bean.getAccessToken());
	}

	private void setHeaders(HttpRequest.Builder requestBuilder) {
		//Setting this to fix a bug in jdk which sets illegal "Accept" header
		//httpsURLConnection.setRequestProperty("Accept", "application/json");
		if(bean.getHeaders() != null && !bean.getHeaders().isEmpty()) {
			for(Entry<String, String> entry:bean.getHeaders().entrySet()) {
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

	@Override
	protected void postExecute(APICall<Request, Response> myCall, Exception e) {
	}

	@Override
	protected void preExecute() {
		// TODO Auto-generated method stub
	}
}
