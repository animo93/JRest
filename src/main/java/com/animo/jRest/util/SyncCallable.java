package com.animo.jRest.util;

import java.util.concurrent.Callable;
//TODO: Refactor to use CompletableFuture
public class SyncCallable<Response> implements Callable<APIResponse<Response>> {

	private final RESTConnector restConnector;
	
	public SyncCallable(RESTConnector restConnector) {
		this.restConnector= restConnector;
	}

	@Override
	public APIResponse<Response> call() throws Exception {
		try {
			return restConnector.fetch();
		} catch(Exception e) {
			throw e;
		}
	}

}
