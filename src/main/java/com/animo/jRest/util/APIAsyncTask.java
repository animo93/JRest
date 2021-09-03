package com.animo.jRest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.*;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class APIAsyncTask<Request,Response> extends AsyncTask<RequestBean<Request>,APICall<Request,Response>>{

	private static final Logger logger = LogManager.getLogger(APIAsyncTask.class);
	private APICallBack<Request, Response> myCallBack;
	private final RequestBean<Request> bean;
	private final Type type;

	public APIAsyncTask(RequestBean<Request> bean, Type type, APICallBack<Request, Response> myCallBack) {
		this.bean = bean;
		this.type = type;
		this.myCallBack = myCallBack;
	}

	public APIAsyncTask(RequestBean<Request> bean, Type type) {
		this.bean = bean;
		this.type = type;
	}

	@Override
	protected APICall<Request,Response> runInBackground(RequestBean<Request> myRequestBean) throws Exception {
		if(myRequestBean == null)
			return null;
		final RequestBean<Request> bean = myRequestBean;
		final HttpURLConnection httpURLConnection = null;
		final BufferedReader reader = null;
		final String repoJson = null;
		APICall<Request, Response> myCall = null;
		final URL url = new URL(this.bean.getUrl());
		if(url.getProtocol().equals("https")){
			myCall = httpsConnection(url);
		}else {
			myCall = httpConnection(url);
		}

		return myCall;
	}

	private APICall<Request,Response> httpConnection(URL url) throws Exception {
		HttpURLConnection httpURLConnection = null;
		String repoJson = null;
		APICall<Request, Response> myCall = new APICall<>();

		try{
			httpURLConnection = getHttpConnection();

			httpURLConnection.setRequestMethod(bean.getRequestType().toString());

			setHeaders(httpURLConnection);

			setAuthentication(httpURLConnection);

			setRequestBody(httpURLConnection);

			httpURLConnection.setInstanceFollowRedirects(bean.isFollowRedirects());

			httpURLConnection.connect();

			logger.debug("Response Headers " + httpURLConnection.getHeaderFields());
			myCall.setResponseHeaders(httpURLConnection.getHeaderFields());

			logger.debug("response code " + httpURLConnection.getResponseCode());

			int status = httpURLConnection.getResponseCode();
			myCall.setResponseCode(status);

			repoJson = getResponseBody(status,httpURLConnection);

		} catch (Exception e) {
			logger.error("Could not make connection ", e);
			throw e;
		}

		convertResponse(repoJson, myCall);

		return myCall;
	}

	private HttpURLConnection getHttpConnection() throws IOException{
		try {
			final URL url = new URL(bean.getUrl());
			logger.debug("Going to make connection for " + url.toString());
			if(bean.getProxy() != null){
				logger.debug("proxy " + bean.getProxy());
				if(bean.getProxy().getUrl() != null) {
					final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
							bean.getProxy().getUrl(), bean.getProxy().getPort()));
					if(bean.getProxy().getUsername() != null && bean.getProxy().getPassword() != null) {
						final Authenticator auth = new Authenticator() {
							public PasswordAuthentication getPasswordAuthentication(){
								return(new PasswordAuthentication(
										bean.getProxy().getUsername(), bean.getProxy().getPassword().toCharArray()));
							}
						};
						Authenticator.setDefault(auth);
					}
					return (HttpURLConnection) url.openConnection(proxy);
				}else {
					throw new IllegalArgumentException("Proxy Url is not provided ");
				}

			}else{
				return (HttpURLConnection) url.openConnection();
			}
		}catch (MalformedURLException e) {
			logger.error("Url is malformed "+bean.getUrl(), e);
			throw e;
		} catch (IOException e) {
			logger.error("Unable to establish connection to "+bean.getUrl(), e);
			throw e;
		}

	}

	private APICall<Request, Response> httpsConnection(URL url) throws Exception {
		HttpsURLConnection httpsURLConnection = null;
		String repoJson = null;
		APICall<Request, Response> myCall = new APICall<>();
		
		try{
			httpsURLConnection = getConnection();

			if(url.getProtocol().equals("https")){
				if(bean.isDisableSSLVerification()) {
					//Install all -trusting host verifier ...Very risky , never use in prod
					httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String hostname, SSLSession session) {
							logger.debug("Hostname is " + hostname);
							return true;
						}
					});
				}
			}


			httpsURLConnection.setRequestMethod(bean.getRequestType().toString());

			setHeaders(httpsURLConnection);

			setAuthentication(httpsURLConnection);

			setRequestBody(httpsURLConnection);

			httpsURLConnection.setInstanceFollowRedirects(bean.isFollowRedirects());

			httpsURLConnection.connect();
			
			logger.debug("Response Headers " + httpsURLConnection.getHeaderFields());
			myCall.setResponseHeaders(httpsURLConnection.getHeaderFields());

			logger.debug("response code " + httpsURLConnection.getResponseCode());
			
			int status = httpsURLConnection.getResponseCode();
			myCall.setResponseCode(status);

			repoJson = getResponseBody(status,httpsURLConnection);

		} catch (Exception e) {
			logger.error("Could not make connection ", e);
			throw e;
		}

		convertResponse(repoJson, myCall);

		return myCall;
	}

	private void convertResponse(String repoJson, APICall<Request, Response> myCall) throws Exception{
		Gson gson = new Gson();
		try {
			if(repoJson != null) {

				logger.debug("repoJson " + repoJson);

				if(!outputIsJson(repoJson)) {
					myCall.setResponseBody((Response) repoJson);
				} else {
					logger.debug("type " + type.getClass());
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

	private String getResponseBody(int status,HttpURLConnection httpsURLConnection) throws Exception {
		String repoJson = null;
		BufferedReader reader = null;
		try {
			final InputStream inputStream;
			if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_CREATED)
				inputStream = httpsURLConnection.getErrorStream();
			else {
				inputStream = httpsURLConnection.getInputStream();
			}
			final StringBuffer stringBuffer = new StringBuffer();
			if (inputStream == null)
				repoJson = null;
			else {
				reader = new BufferedReader(new InputStreamReader(inputStream));

				String line;
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line).append("\n");
				}
				if (stringBuffer.length() == 0)
					repoJson = null;

				repoJson = stringBuffer.toString();
				inputStream.close();
			}
		}catch (Exception e){
			logger.error("Unable to get Response Body ",e);
			throw e;
		} finally {
			if (httpsURLConnection != null)
				httpsURLConnection.disconnect();
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("error in closing conn", e);
					throw e;
				}
			}
		}
		return repoJson;
	}

	private void setRequestBody(HttpURLConnection httpsURLConnection) throws IOException {
		if(bean.getRequestType().toString().equals("POST") || bean.getRequestType().toString().equals("PATCH")
				|| bean.getRequestType().toString().equals("PUT")) {
			Request requestObject = bean.getRequestObject();
			if(null != requestObject) {
				final String json = new Gson().toJson(requestObject, new TypeToken<Request>(){}.getType());
				logger.debug("request json " + json);
				/*System.out.println("request json "+json);*/
				httpsURLConnection.setDoOutput(true);

				final OutputStream os = httpsURLConnection.getOutputStream();
				os.write(json.getBytes("UTF-8"));
				os.close();
			}
		}
	}

	private void setAuthentication(HttpURLConnection httpsURLConnection) {
		if(bean.getAuthentication() != null) {
			final RequestAuthentication auth = bean.getAuthentication();
			if(auth.getUsername() != null && auth.getPassword() != null) {
				final String userPassword = auth.getUsername() + ":" + auth.getPassword();
				final String encodedAuthorization = Base64.encodeBase64String(userPassword.getBytes());
				httpsURLConnection.setRequestProperty("Authorization", "Basic " +
						encodedAuthorization.replaceAll("\n", ""));
			}
		}

		if(bean.getAccessToken() != null)
			httpsURLConnection.setRequestProperty("Authorization", " token " + bean.getAccessToken());
	}

	private void setHeaders(HttpURLConnection httpsURLConnection) {
		httpsURLConnection.setRequestProperty("Content-Type", "application/json");
		if(bean.getHeaders() != null && !bean.getHeaders().isEmpty()) {
			for(Entry<String, String> entry:bean.getHeaders().entrySet()) {
				httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	private HttpsURLConnection getConnection() throws IOException {
		try {
			final URL url = new URL(bean.getUrl());
			logger.debug("Going to make connection for " + url.toString());
			if(bean.getProxy() != null){
				logger.debug("proxy " + bean.getProxy());
				if(bean.getProxy().getUrl() != null) {
					final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
							bean.getProxy().getUrl(), bean.getProxy().getPort()));
					if(bean.getProxy().getUsername() != null && bean.getProxy().getPassword() != null) {
						final Authenticator auth = new Authenticator() {
							public PasswordAuthentication getPasswordAuthentication(){
								return(new PasswordAuthentication(
										bean.getProxy().getUsername(), bean.getProxy().getPassword().toCharArray()));
							}
						};
						Authenticator.setDefault(auth);
					}
					return (HttpsURLConnection) url.openConnection(proxy);
				}else {
					throw new IllegalArgumentException("Proxy Url is not provided ");
				}

			}else{
				return (HttpsURLConnection) url.openConnection();
			}
		}catch (MalformedURLException e) {
			logger.error("Url is malformed "+bean.getUrl(), e);
			throw e;
		} catch (IOException e) {
			logger.error("Unable to establish connection to "+bean.getUrl(), e);
			throw e;
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
	protected void postExecute(APICall<Request,Response> myCall,Exception e) {
		if(e != null)
			myCallBack.callBackOnFailure(e);
		else
			myCallBack.callBackOnSuccess(myCall);
	}

	@Override
	protected void preExecute() {
		// TODO Auto-generated method stub
	}
}
