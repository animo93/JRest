package com.animo.jRest.test;
import java.util.Map;

import com.animo.jRest.util.APIHelper;
import com.animo.jRest.util.APICall;
import com.animo.jRest.util.APICallBack;

public class SemitiTest {
	
	public static void main(String args[]) throws Exception{
		APIHelper myApiHelper = APIHelper.APIBuilder
				.builder("https://api.github.com/repos/animo93/gi")
				.build();
		MyApiInterface myApiInterface = myApiHelper.createApi(MyApiInterface.class);
		APICall<Void, ApiResponse> call =  myApiInterface.testCall();
		call.callMeLater(new APICallBack<Void, ApiResponse>() {
			
			@Override
			public void callBackOnSuccess(APICall<Void, ApiResponse> myCall) {
				// TODO Auto-generated method stub
				System.out.println(myCall.getResponseBody().getDocumentation_url());
				
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
