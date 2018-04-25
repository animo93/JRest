package com.animo.jRest;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAsyncTask<Request,Response> extends AsyncTask<MyRequestBean<Request>,MyCall<Request,Response>>{

	private static final String LOG_TAG = MyAsyncTask.class.getSimpleName();
	private MyCallBack myCallBack;
	private MyRequestBean<Request> bean;
	private Type type;

	public MyAsyncTask(MyRequestBean<Request> bean,Type type,MyCallBack myCallBack) {
		this.bean = bean;
		this.type=type;
		this.myCallBack = myCallBack;
	}


	@Override
	protected MyCall<Request,Response> runInBackground(MyRequestBean<Request> myRequestBean) throws Exception{
		if(myRequestBean == null)
			return null;
		MyRequestBean<Request> bean = myRequestBean;
		HttpURLConnection httpURLConnection = null;
		BufferedReader reader = null;
		String repoJson = null;
		MyCall<Request,Response> myCall = new MyCall<>();

		try{
			/*Uri buildUri = Uri.parse(bean.getUrl()).buildUpon()
                    .build();*/
                    URL url = new URL(bean.getUrl());
                    //Log.e(LOG_TAG, "url is " + url);


                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod(bean.getRequestType().toString());
                    httpURLConnection.setRequestProperty("Content-Type","application/json");
                    if(bean.getAccessToken()!=null)
                    	httpURLConnection.setRequestProperty("Authorization"," token " + bean.getAccessToken());

                    if(bean.getRequestType().toString().equals("POST") || bean.getRequestType().toString().equals("PATCH")){
                    	Request requestObject= bean.getRequestObject();
                    	String json = new Gson().toJson(requestObject,new TypeToken<Request>(){}.getType());
                    	//Log.d(LOG_TAG,"request json "+json);
                    	OutputStream os = httpURLConnection.getOutputStream();
                    	os.write(json.getBytes("UTF-8"));
                    	os.close();
                    }
                    httpURLConnection.connect();
                    //Log.d(LOG_TAG,"response code "+httpURLConnection.getResponseCode());
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
			//Log.e(LOG_TAG,"Could not make connection "+e);
			e.printStackTrace();
			//myCallBack.callBackOnFailure(e);
		} finally {
			if (httpURLConnection != null)
				httpURLConnection.disconnect();
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					//Log.e(LOG_TAG, "error", e);
				}
			}
		}

		Gson gson = new Gson();
		if(repoJson!=null){
			//Log.d(LOG_TAG,"Response Output "+repoJson);
			Response response = gson.fromJson(repoJson, type);
			myCall.setResponseBody(response);
		}
		return myCall;
	}

	@Override
	protected void postExecute(MyCall<Request,Response> myCall,Exception e) {
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
