package com.animo.jRest.util;

import java.util.concurrent.Callable;

public class SyncCallable<Response> implements Callable<APIResponse<Response>> {

	private final RESTConnector<Response> restConnector;
	
	public SyncCallable(RESTConnector<Response> restConnector) {
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
