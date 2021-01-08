package com.animo.jRest.util;

import java.util.concurrent.Callable;

public class SyncCallable<Params, Result> implements Callable<Result> {

	private final Params params;
	private final AsyncTask<Params, Result> asyncTask;
	
	public SyncCallable(Params params, AsyncTask<Params, Result> asyncTask) {
		this.params = params;
		this.asyncTask = asyncTask;
	}

	@Override
	public Result call() throws Exception {
		Result result;
		try {
			result = asyncTask.runInBackground(params);
		} catch(Exception e) {
			throw e;
		}
		return result;
	}

}
