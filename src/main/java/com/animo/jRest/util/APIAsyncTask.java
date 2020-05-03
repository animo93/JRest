package com.animo.jRest.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.Map.Entry;

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
					httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
				}

			}else{
				httpURLConnection = (HttpURLConnection) url.openConnection();
			}

			httpURLConnection.setRequestMethod(bean.getRequestType().toString());
			httpURLConnection.setRequestProperty("Content-Type","application/json");
			if(bean.getHeaders()!=null && !bean.getHeaders().isEmpty()){
				for(Entry<String, String> entry:bean.getHeaders().entrySet()){
					httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			if(bean.getAuthentication()!=null){
				RequestAuthentication auth = bean.getAuthentication();
				if(auth.getUsername()!=null && auth.getPassword()!=null){
					String userPassword = auth.getUsername()+":"+auth.getPassword();
					String encodedAuthorization = Base64.encodeBase64String(userPassword.getBytes());
					httpURLConnection.setRequestProperty("Authorization", "Basic "+
							encodedAuthorization.replaceAll("\n","")); 
				}
			}


			if(bean.getAccessToken()!=null)
				httpURLConnection.setRequestProperty("Authorization"," token " + bean.getAccessToken());

			if(bean.getRequestType().toString().equals("POST") || bean.getRequestType().toString().equals("PATCH")){

				Request requestObject= bean.getRequestObject();
				if(null!=requestObject){
					String json = new Gson().toJson(requestObject,new TypeToken<Request>(){}.getType());
					logger.debug("request json "+json);
					/*System.out.println("request json "+json);*/

					OutputStream os = httpURLConnection.getOutputStream();
					os.write(json.getBytes("UTF-8"));
					os.close();
				}
			}
			httpURLConnection.connect();

			logger.debug("response code "+httpURLConnection.getResponseCode());
			/*System.out.println("response code "+httpURLConnection.getResponseCode());*/
			int status = httpURLConnection.getResponseCode();
			myCall.setResponseCode(status);
			InputStream inputStream;
			if(status!=HttpURLConnection.HTTP_OK && status!=HttpURLConnection.HTTP_CREATED)
				inputStream = httpURLConnection.getErrorStream();
			else{
				inputStream = httpURLConnection.getInputStream();
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
			if (httpURLConnection != null)
				httpURLConnection.disconnect();
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
				logger.debug("Response type "+type);
				ObjectMapper mapper = new ObjectMapper();
				/*Class<? extends Type> s = type.getClass();*/
				Class<?> t = (Class<?>) type;
				Response res = (Response) mapper.readValue(repoJson, t);

				myCall.setResponseBody(res);
				/*System.out.println(myCall.getResponseBody());*/
			}
		}catch(Exception e){
			logger.error("Error in json conversion ",e);
			throw e;
		}

		return myCall;
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