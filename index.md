# Introduction

JRest provides an opportunity to create an API contract via a Java Interface
```
public interface MyApiInterface {

    @REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    APICall<Void,ApiResponse> listRepos(@PATH(value = "user") String user);
   

}
```
The `ApiHelper` can be used to build an implementation of the `MyApiInterface`
```
APIHelper myApiHelper = APIHelper.APIBuilder
				.builder("https://api.github.com/")
				.build();
MyApiInterface myApiInterface = myApiHelper.createApi(MyApiInterface.class);
```

Next `APICall` needs to be called to instantiate the API call

```
APICall<Void, ApiResponse> call =  myApiInterface.listRepos("test");
```

One can either make a Synchronous or Asynchronous call via `APICallBack`

### Synchronous Call
```
APICall<Void, ApiResponse> response = call.callMeNow()
```

### Asynchronous Call
```
call.callMeLater(new APICallBack<Void, ApiResponse>() {
			
			@Override
			public void callBackOnSuccess(APICall<Void, ApiResponse> myCall) {
				System.out.println(myCall.getResponseCode());		
				
			}
			
			@Override
			public void callBackOnFailure(Exception e) {
				System.out.println("error");
				e.printStackTrace();
				
			}
		});
```
Use annotations to describe the HTTP request:
 1. URL parameter replacement
 2. Object conversion to request body
 3. Query Parameter Support
 
# API Declaration
 Annotations on the interface methods and its parameters indicate how a request will be handled.
 
### REQUEST METHOD
Every method must have an `REQUEST` annotation that provides the request method and relative URL. The relative URL of the resource is specified in the annotation via `endpoint` and Request type can be specified by the `type` attribute
```
 @REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
```

### URL MANIPULATION
A request URL can be updated dynamically using replacement blocks and parameters on the method. A replacement block is an alphanumeric string surrounded by { and }.
```
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    APICall<Void,ApiResponse> listRepos(@PATH(value = "user") String user);
```
Query Parameters can be added via the @Query Annotation
```
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    APICall<Void,ApiResponse> listRepos(@PATH(value = "user") String user,@Query("sortBy") String sortBy);
```
Complex Parameters can be represented via the @QueryMap Annotation
```
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    APICall<Void,ApiResponse> listRepos(@PATH(value = "user") String user,@QueryMap Map<String,String> queryMap);
```

### HEADER MANIPULATION
Headers can be added to a API Method in a static manner via `HEADERS` annotation
```
@HEADERS("X-Foo:Bar")
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    APICall<Void,ApiResponse> listRepos(@PATH(value = "user") String user);
```
Multiple Headers can also be set in a static manner via below 
```
@HEADERS({
		"X-Foo:Bar",
		"X-Ping:Pong"
	})
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    APICall<Void,ApiResponse> listRepos(@PATH(value = "user") String user);
```

A request Header can also be updated dynamically via the `HEADER` annotation set in the method Parameter
```
@HEADERS("X-Foo:Bar")
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    APICall<Void,ApiResponse> listRepos(@PATH(value = "user") String user,@HEADER Map<String, String> header);
```
Note that the Headers parameters can only be used via a Map


### REQUEST BODY
An object can be specified for use as an HTTP request body with the @Body annotation.

```
@REQUEST(endpoint = "/users/new",type = HTTP_METHOD.POST)
APICall<Void,String> createUser(@Body User user);
```
The Object would be converted via Google Gson into the required JSON data while posting

