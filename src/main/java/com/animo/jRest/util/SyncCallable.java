package com.animo.jRest.util;

import com.animo.jRest.model.RequestBean;

import java.util.concurrent.Callable;

public class SyncCallable<Response> implements Callable<APIResponse<Response>> {

	private final RequestBean<Object> params;
	private final AsyncTask<Response> asyncTask;
	
	public SyncCallable(RequestBean<Object> params, AsyncTask<Response> asyncTask) {
		this.params = params;
		this.asyncTask = asyncTask;
	}

	@Override
	public APIResponse<Response> call() throws Exception {
		try {
			return asyncTask.runInBackground(params);
		} catch(Exception e) {
			throw e;
		}
	}

}
