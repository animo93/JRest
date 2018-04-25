package com.animo.jRest.test;
import java.util.Map;

import com.animo.jRest.MyApiResponse;
import com.animo.jRest.MyCall;
import com.animo.jRest.MyCallBack;

public class SemitiTest {
	
	public static void main(String args[]){
		MyApiInterface myApiInterface = MyApiResponse.createApi(MyApiInterface.class);
		MyCall<Void, Map<String, String>> call =  myApiInterface.testCall(null, "https://api.github.com/repos/animo93/gitA");
		call.callMeNow(new MyCallBack<Void, Map<String,String>>() {
			
			@Override
			public void callBackOnSuccess(MyCall<Void, Map<String, String>> myCall) {
				// TODO Auto-generated method stub
				System.out.println(myCall.getResponseBody());
				
			}
			
			@Override
			public void callBackOnFailure(Exception e) {
				// TODO Auto-generated method stub
				System.out.println("error");
				e.printStackTrace();
				
			}
		});
	}

}
