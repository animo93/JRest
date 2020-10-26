package com.animo.jRest.test;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APICallBack;
import com.animo.jRest.util.APIHelper;

public class SemitiTest {
	
	@Test
	public void testFollowRedirectsFalse() throws Exception {

		APIHelper myApiHelper = APIHelper.APIBuilder
				.builder("https://picsum.photos/200/300")
				.build();
		TestApiInterface myApiInterface = myApiHelper.createApi(TestApiInterface.class);
		APICall<Void, String> call =  myApiInterface.testFollowRedirectFalse();
		//int[] responseCode= {0};

		AtomicInteger responseCode = new AtomicInteger();
		call.callMeLater(new APICallBack<Void, String>() {
			
			@Override
			public void callBackOnSuccess(APICall<Void, String> myCall) {
				System.out.println(myCall.getResponseCode());
				responseCode.set(myCall.getResponseCode());		
				
			}
			
			@Override
			public void callBackOnFailure(Exception e) {
				// TODO Auto-generated method stub
				System.out.println("error");
				e.printStackTrace();
				
			}
		});
		Thread.sleep(10000);
		Assert.assertEquals(302, responseCode.intValue());
	
	}
	
	@Test
	public void testFollowRedirectsTrue() throws Exception {

		APIHelper myApiHelper = APIHelper.APIBuilder
				.builder("https://picsum.photos/200/300")
				.build();
		TestApiInterface myApiInterface = myApiHelper.createApi(TestApiInterface.class);
		APICall<Void, String> call =  myApiInterface.testFollowRedirectTrue();
		//int[] responseCode= {0};

		AtomicInteger responseCode = new AtomicInteger();
		call.callMeLater(new APICallBack<Void, String>() {
			
			@Override
			public void callBackOnSuccess(APICall<Void, String> myCall) {
				System.out.println(myCall.getResponseCode());
				responseCode.set(myCall.getResponseCode());		
				
			}
			
			@Override
			public void callBackOnFailure(Exception e) {
				// TODO Auto-generated method stub
				System.out.println("error");
				e.printStackTrace();
				
			}
		});
		Thread.sleep(10000);
		Assert.assertEquals(200, responseCode.intValue());
	
	}
	
	@Test
	public void testFollowRedirectsNone() throws Exception {

		APIHelper myApiHelper = APIHelper.APIBuilder
				.builder("https://picsum.photos/200/300")
				.build();
		TestApiInterface myApiInterface = myApiHelper.createApi(TestApiInterface.class);
		APICall<Void, String> call =  myApiInterface.testFollowRedirectNone();
		//int[] responseCode= {0};

		AtomicInteger responseCode = new AtomicInteger();
		call.callMeLater(new APICallBack<Void, String>() {
			
			@Override
			public void callBackOnSuccess(APICall<Void, String> myCall) {
				System.out.println(myCall.getResponseCode());
				responseCode.set(myCall.getResponseCode());		
				
			}
			
			@Override
			public void callBackOnFailure(Exception e) {
				// TODO Auto-generated method stub
				System.out.println("error");
				e.printStackTrace();
				
			}
		});
		Thread.sleep(10000);
		Assert.assertEquals(200, responseCode.intValue());
	
	}

}
