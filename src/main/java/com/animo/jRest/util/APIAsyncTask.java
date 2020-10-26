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
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
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
	private APICallBack<Request,Response> myCallBack;
	private RequestBean<Request> bean;
	private Type type;

	public APIAsyncTask(RequestBean<Request> bean,Type type,APICallBack<Request, Response> myCallBack) {
		this.bean = bean;
		this.type=type;
		this.myCallBack = myCallBack;
	}

	public APIAsyncTask(RequestBean<Request> bean,Type type) {
		this.bean = bean;
		this.type=type;
	}


	@Override
	protected APICall<Request,Response> runInBackground(RequestBean<Request> myRequestBean) throws Exception{
		if(myRequestBean == null)
			return null;
		RequestBean<Request> bean = myRequestBean;
		HttpURLConnection httpURLConnection = null;
		BufferedReader reader = null;
		String repoJson = null;
		APICall<Request,Response> myCall = new APICall<>();
		
		myCall=httpsConnection();

		return myCall;
	}

	
	private APICall<Request, Response> httpsConnection() throws Exception {
		HttpsURLConnection httpsURLConnection = null;
		BufferedReader reader = null;
		String repoJson = null;
		APICall<Request,Response> myCall = new APICall<>();
		
		try{

			URL url = new URL(bean.getUrl());
			logger.debug("Going to make connection for "+url.toString());
			if(bean.getProxy()!=null){
				logger.debug("proxy "+bean.getProxy());
				if(bean.getProxy().getUrl()!=null){
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
							bean.getProxy().getUrl(), bean.getProxy().getPort()));
					if(bean.getProxy().getUsername()!=null && bean.getProxy().getPassword()!=null){
						Authenticator auth = new Authenticator() {
							public PasswordAuthentication getPasswordAuthentication(){
								return(new PasswordAuthentication(
										bean.getProxy().getUsername(),bean.getProxy().getPassword().toCharArray()));
							}
						};
						Authenticator.setDefault(auth);
					}
					httpsURLConnection = (HttpsURLConnection) url.openConnection(proxy);
				}

			}else{
				httpsURLConnection = (HttpsURLConnection) url.openConnection();
			}
			
			if(bean.isDisableSSLVerification()) {
				//Install all -trusting host verifier ...Very risky , never use in prod
				httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname,SSLSession session) {
						logger.debug("Hostname is "+hostname);
						return true;
					}
				});
			}

			httpsURLConnection.setRequestMethod(bean.getRequestType().toString());
			httpsURLConnection.setRequestProperty("Content-Type","application/json");
			if(bean.getHeaders()!=null && !bean.getHeaders().isEmpty()){
				for(Entry<String, String> entry:bean.getHeaders().entrySet()){
					httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			if(bean.getAuthentication()!=null){
				RequestAuthentication auth = bean.getAuthentication();
				if(auth.getUsername()!=null && auth.getPassword()!=null){
					String userPassword = auth.getUsername()+":"+auth.getPassword();
					String encodedAuthorization = Base64.encodeBase64String(userPassword.getBytes());
					httpsURLConnection.setRequestProperty("Authorization", "Basic "+
							encodedAuthorization.replaceAll("\n","")); 
				}
			}


			if(bean.getAccessToken()!=null)
				httpsURLConnection.setRequestProperty("Authorization"," token " + bean.getAccessToken());

			if(bean.getRequestType().toString().equals("POST") || bean.getRequestType().toString().equals("PATCH") 
					|| bean.getRequestType().toString().equals("PUT")){

				Request requestObject= bean.getRequestObject();
				if(null!=requestObject){
					String json = new Gson().toJson(requestObject,new TypeToken<Request>(){}.getType());
					logger.debug("request json "+json);
					/*System.out.println("request json "+json);*/
					httpsURLConnection.setDoOutput(true);

					OutputStream os = httpsURLConnection.getOutputStream();
					os.write(json.getBytes("UTF-8"));
					os.close();
				}
			}
			
			httpsURLConnection.setInstanceFollowRedirects(bean.isFollowRedirects());
			httpsURLConnection.connect();
			
			logger.debug("Response Headers "+httpsURLConnection.getHeaderFields());
			myCall.setResponseHeaders(httpsURLConnection.getHeaderFields());

			logger.debug("response code "+httpsURLConnection.getResponseCode());
			
			int status = httpsURLConnection.getResponseCode();
			myCall.setResponseCode(status);
			InputStream inputStream;
			if(status!=HttpURLConnection.HTTP_OK && status!=HttpURLConnection.HTTP_CREATED)
				inputStream = httpsURLConnection.getErrorStream();
			else{
				inputStream = httpsURLConnection.getInputStream();
			}
			StringBuffer stringBuffer = new StringBuffer();
			if (inputStream == null)
				repoJson = null;
			else{
				reader = new BufferedReader(new InputStreamReader(inputStream));

				String line;
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line + "\n");
				}
				if (stringBuffer.length() == 0)
					repoJson = null;

				repoJson = stringBuffer.toString();
				inputStream.close();
			}
		} catch (Exception e) {
			logger.error("Could not make connection ",e);
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

		Gson gson = new Gson();
		try{
			if(repoJson!=null){
				/*String repo=repoJson.replace("-", "");*/
				//Response response = gson.fromJson(repoJson, type);
				logger.debug("repoJson "+repoJson);
				/*System.out.println("repJson:"+repoJson);*/
				
				if(!outputIsJson(repoJson)) {
					myCall.setResponseBody((Response) repoJson);
				}else {
					logger.debug("type "+type.getClass());
					ObjectMapper mapper = new ObjectMapper();
					Class<?> t = type2Class(type);
					Response res = (Response) mapper.readValue(repoJson, t);

					myCall.setResponseBody(res);
				}
				
			}
		}catch(Exception e){
			logger.error("Error in json conversion ",e);
			throw e;
		}

		return myCall;
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
		       Type[] bounds = ((TypeVariable<?>) type).getBounds();
		       return bounds.length == 0 ? Object.class : type2Class(bounds[0]); // erasure is to the left-most bound.
		    } else if (type instanceof WildcardType) {
		       Type[] bounds = ((WildcardType) type).getUpperBounds();
		       return bounds.length == 0 ? Object.class : type2Class(bounds[0]); // erasure is to the left-most upper bound.
		    } else { 
		       throw new UnsupportedOperationException("cannot handle type class: " + type.getClass());
		    }
		} 

	private boolean outputIsJson(String repoJson) {
		
		return (repoJson!=null && repoJson.startsWith("{")) ? true : false;
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