package com.animo.jRest.util;

import java.util.concurrent.Callable;

public class AsyncCallable<Params,Result> implements Callable<Result>{
	
	private Params params;
	private AsyncTask<Params, Result> asyncTask;
	
	public AsyncCallable(Params params,AsyncTask<Params, Result> asyncTask) {
		this.params = params;
		this.asyncTask = asyncTask;
	}

	@Override
	public Result call() throws Exception {
		Result result=null;
		Exception exception = null;
		try{
			result = asyncTask.runInBackground(params);
		}catch(Exception e){
			exception =e;
		}	
		asyncTask.postExecute(result,exception);
		return result;
	}

}
