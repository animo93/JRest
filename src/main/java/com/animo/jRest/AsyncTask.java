package com.animo.jRest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AsyncTask<Params,Result>{

	private ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	protected abstract Result runInBackground(Params params) throws Exception;
	protected abstract void postExecute(Result result,Exception e);
	protected abstract void preExecute();

	public void execute(Params params) {
		preExecute();
		Future<Result>future  = executor.submit(new AsyncCallable<Params, Result>(params, this));
		executor.shutdown();		
	}

}
