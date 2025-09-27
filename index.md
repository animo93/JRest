# Introduction

JRest provides an opportunity to create an API contract via a Java Interface
```
public interface GithubInterface {

    @REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    APIResponse<Map<String, Object>> listRepos(@Path(value = "user") String user);
}
```
The `JRest` class can be used to build an implementation of your `GithubInterface`
```
GithubInterface githubInterface = new JRest.APIBuilder("https://postman-echo.com")
				.build(TestPostmanEchoAPIInterface.class);
APIResponse<Map<String, Object>> response = githubInterface.listRepos("testUser");
```

One can either make a Synchronous or Asynchronous request either via Callback or via Future

### Synchronous Call
```
APIResponse<Map<String, Object>> response = githubInterface.listRepos("testUser");
```

### Asynchronous Call via Callback
```
public interface GithubInterface {

    @REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    void listReposWithCallback(@Path(value = "user") String user, APICallBack<Map<String, Object>>);
}

githubInterface.listReposWithCallback("testUser",new APICallBack<Void, ApiResponse>() {
			
			@Override
			public void callBackOnSuccess(APIResponse<Map<String, Object>> result) {
				System.out.println(myCall.getResponseCode());		
				
			}
			
			@Override
			public void callBackOnFailure(Throwable e) {
				System.out.println("error");
				e.printStackTrace();
				
			}
		});
```

### Asynchronous Call via Future
```
public interface GithubInterface {
    @REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
    Future<APIResponse<Map<String, Object>>> listReposWithFuture(@Path(value = "user") String user);
}

Future<APIResponse<Map<String, Object>>> response = githubInterface.listReposWithFuture("testUser");
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
APIResponse<Map<String, Object>> listRepos(@Path(value = "user") String user);
```
Query Parameters can be added via the @Query Annotation
```
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
APIResponse<Map<String, Object>> listRepos(@Path(value = "user") String user,@Query("sortBy") String sortBy);
```
Complex Parameters can be represented via the @QueryMap Annotation
```
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
APIResponse<Map<String, Object>> listRepos(@Path(value = "user") String user,@QueryMap Map<String,String> queryMap);
```

### HEADER MANIPULATION
Headers can be added to a API Method in a static manner via `Headers` annotation
```
@Headers("X-Foo:Bar")
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
APIResponse<Map<String, Object>> listRepos(@Path(value = "user") String user);
```
Multiple Headers can also be set in a static manner via below 
```
@Headers({
		"X-Foo:Bar",
		"X-Ping:Pong"
	})
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
APIResponse<Map<String, Object>> listRepos(@Path(value = "user") String user);
```

A request Header can also be updated dynamically via the `HeaderMap` annotation set in the method Parameter
```
@Headers("X-Foo:Bar")
@REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
APIResponse<Map<String, Object>> listRepos(@Path(value = "user") String user,@HeaderMap Map<String, String> header);
```
Note that the Headers parameters can only be used via a Map


### REQUEST BODY
An object can be specified for use as an HTTP request body with the @Body annotation.

```
@REQUEST(endpoint = "/users/new",type = HTTP_METHOD.POST)
APIResponse<Map<String, Object>> createUser(@Body User user);
```
The Object would be converted via Google Gson into the required JSON data while posting

