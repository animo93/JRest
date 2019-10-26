package com.animo.jRest.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AsyncTask<Params,Result>{

	private ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	protected abstract Result runInBackground(Params params) throws Exception;
	protected abstract void postExecute(Result result,Exception e);
	protected abstract void preExecute();

	public void executeLater(Params params) throws Exception{
		try {
			preExecute();
			Future<Result>future  = executor.submit(new AsyncCallable<Params, Result>(params, this));
		} catch(Exception e) {
			throw e;
		} finally {
			executor.shutdown();
		}
				
	}
	
	public Result executeNow(Params params) throws Exception{
		try {
			preExecute();
			Future<Result>future  = executor.submit(new SyncCallable<Params, Result>(params, this));
			return future.get();
		} catch(Exception e) {
			throw e;
		} finally {
			executor.shutdown();
		}
				
	}

}
