package com.animo.jRest.util;

import com.animo.jRest.annotation.FollowRedirects;
import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.REQUEST;
import com.animo.jRest.model.APIClientRecord;
import com.animo.jRest.model.RequestAuthenticationRecord;
import com.animo.jRest.model.APIRequestRecord;
import com.animo.jRest.model.RequestProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * API Service method which is used for initializing and building the initial API request.
 * <p>It adapts a Java Interface to HTTP calls by using annotation on the declared method. Instances can be created
 * by passing the builder method to generate an implementation.<p>
 * For example :-
 * <pre><code>
 * APIService apiService = APIService.APIBuilder
 *			.builder("https://api.github.com/")
 *			.build();
 * MyApiInterface myApiInterface = apiService.createApi(MyApiInterface.class);
 * </code></pre>
 * 
 * @author animo
 */
public final class JRest {
	private final static Logger logger = LogManager.getLogger(JRest.class);

	private JRest() {}
    //TODO: Rename to APIClientBuilder
    //TODO: Check what would happen if two threads are trying to execute
    //TODO: Test what would happen if two separate JRest instances are created for the same interface
	public static class APIBuilder {
		private final String baseURL;
		private Map<String,String> queryParams;
		private RequestAuthenticationRecord auth;
		private RequestProxy proxy;
		private boolean disableSSLVerification;

		public APIBuilder(final String baseURL){
			this.baseURL = baseURL;
		}

		public APIBuilder addQueryParameter(final String key, final String value){
			if(queryParams ==null){
				queryParams = new HashMap<>();
			}
			queryParams.put(key, value);
			return this;
		}

		public APIBuilder addQueryMap(final Map<String,String> params) {
			if(this.queryParams == null){
				this.queryParams = new HashMap<>();
			}
			this.queryParams.putAll(params);
			return this;
		}

		/**
		 * Username and Password used for making REST calls via Basic authentication
		 * @param username String used for username
		 * @param password String used for password
		 * @return APIBuilder object with adjusted fields
		 */
		public APIBuilder addUsernameAndPassword(final String username,final String password) {
			if(this.auth == null){
				this.auth = new RequestAuthenticationRecord(Optional.of(username),
                        Optional.of(password),
                        Optional.empty(),
                        Optional.empty());
			}
			return this;
		}

		/**
		 * Proxy details used while building the APICall , if the client is behind any Proxy 
		 * @param proxyURL String used for the proxy URL
		 * @param username String used for the username
		 * @param password String used for the password
		 * @param port integer port number
		 * @return APIBuilder object with adjusted fields
		 */
		public APIBuilder addProxy(final String proxyURL, final String username, final String password, final int port) {
			if(this.proxy ==null){
				this.proxy = new RequestProxy();
			}
			this.proxy.setUrl(proxyURL);
			this.proxy.setUsername(username);
			this.proxy.setPassword(password);
			this.proxy.setPort(port);
			return this;
		}


		/**
		 * Disable any certificates or Hostname verification checks used for making HTTPS calls .
		 * <p>Avoid using this in Production setting
		 * @param disableSSLVerification boolean value for disableSSLVerification
		 * @return APIBuilder object with adjusted fields
		 */

		public APIBuilder setDisableSSLVerification(final boolean disableSSLVerification) {
			this.disableSSLVerification = disableSSLVerification;
			return this;
		}

		public <S> S build(final Class<S> interfaceClass) {
            var apiClientRecord = new APIClientRecord(baseURL, queryParams,auth,proxy,disableSSLVerification);
			return createApi(interfaceClass,apiClientRecord);
		}

        public <S> S buildDynamic(final Class<S> interfaceClass,final String methodName,final Class... parameterTypes) throws NoSuchMethodException {
            var apiClientRecord = new APIClientRecord(baseURL, queryParams,auth,proxy,disableSSLVerification);
            return createDynamicApi(interfaceClass,apiClientRecord,methodName,parameterTypes);
        }

	}

	/**
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     * <p>The relative path for a given method is obtained from an annotation on the method describing
     * the request type.The built-in methods are {GET},{PUT} ,{POST},{PATCH} , {DELETE}
     * <p>Method parameters can be used to replace parts of the URL by annotating them with {Path}. Replacement sections are denoted by an identifier surrounded by
     * curly braces (e.g., "{foo}").
     *
     * <p>The body of a request is denoted by the {@link com.animo.jRest.annotation.Body @Body} annotation.
     * The body would be converted to JSON via Google GSON
     *
     * <p>By default, methods return a {@link APIExecutorService APIRequest} which represents the HTTP request. The generic
     * parameter of the call is the response body type and will be converted by Jackson Object Mapper
     *
     * <p>For example :
     * <pre><code>
     * public interface MyApiInterface {
     *
     * 	&#64;REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
     *    {@code APIRequest<ApiResponse> listRepos(@PATH(value = "user") String user);}
     *
     * }</code></pre>
     *
     * @param <S>              Service Class
     * @param interfaceClass            service.class
     * @param apiClientRecord
     * @return {@code service}
     */
	@SuppressWarnings("unchecked")
	private static <S> S createApi(final Class<S> interfaceClass, APIClientRecord apiClientRecord) {
        //TODO: Add test scenario where a random interface is passed which doesn't have any REQUEST annotation
		final ClassLoader loader = interfaceClass.getClassLoader();
		final Class[] interfaces = new Class[]{interfaceClass};

		final Object object = Proxy.newProxyInstance(loader, interfaces,setInvocationHandler(null, apiClientRecord));

		return (S) object;
	}

	/**
	 * Create a dynamic runtime implementation of the API endpoints defined by the {@code service} interface.
	 * <p>The {@code service} interface should extend JRestDynamicAPiInterface&#60;T&#62; , if dynamic implementation is required</p>
	 * <p>This should be used to dynamically invoke any of the APIs already defined in the {@code service} interface.</p>
	 * <p>The Service interface APIs should be created as usual ,and can be invoked by providing the name and arguments </p>
	 *
	 *
	 * <p>For example : (Service Definition)
	 * <pre><code>
	 * public interface MyApiInterface extends JRestDynamicAPiInterface&#60;ApiResponse&#62;{
	 *
	 *	&#64;REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
	 *	{@code APIRequest<ApiResponse> listRepos(@PATH(value = "user") String user);}
	 *  {@code APIRequest<ApiResponse> dynamicApiInvocation(Object... args);}
	 *
	 *}</code></pre>
	 *
	 * <p> For example : (Service Execution) </p>
	 * <pre><code>
	 *     MyApiInterface testInterface = apiService.createDynamicApi(MyApiInterface.class,"listRepos");
	 *     {@code APIRequest<Map<String,Object> call = testInterface.dynamicAPIInvocation("testUser");}
	 *     {@code APIResponse<Map<String,Object>> response = call.callMeNow();}
	 * </code></pre>
	 *
	 * @param interfaceClass service.class
	 * @param <S> Service Class
	 * @param methodName The method name which is going to be dynamically invoked
	 * @param parameterTypes The parameter types for the method going to be dynamically invoked
	 * @throws NoSuchMethodException When the method doesn't exists in service class
	 * @return {@code service}
	 */
	private static <S> S createDynamicApi(final Class<S> interfaceClass, final APIClientRecord apiClientRecord, final String methodName, Class... parameterTypes) throws NoSuchMethodException {

		final ClassLoader loader = interfaceClass.getClassLoader();
		final Class[] interfaces = new Class[]{interfaceClass};

		Method methodToCall = interfaceClass.getMethod(methodName,parameterTypes);
		final Object object = Proxy.newProxyInstance(loader, interfaces,setInvocationHandler(methodToCall, apiClientRecord));

		return (S) object;

	}

	private static InvocationHandler setInvocationHandler(final Method methodToCall, APIClientRecord apiClientRecord) {
		return new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(methodToCall != null) {
					method = methodToCall;
					args = (Object[]) args[0];
				}
				final Annotation requestAnnotation = method.getAnnotation(REQUEST.class);
				final Annotation headersAnnotation = method.getAnnotation(HEADERS.class);
				FollowRedirects followRedirectsAnnotation = method.getAnnotation(FollowRedirects.class);
				REQUEST request = (REQUEST) requestAnnotation;

				if(request == null){
					throw new Exception("No Request Annotation found");
				}
				final Parameter[] parameters = method.getParameters();
                final APIRequestRecord apiRequestRecord = APIRequestBuilderService.builder(args,parameters, apiClientRecord.baseUrl(),request,method.getGenericReturnType())
                        .addPathParameters()
                        .addQueryParameters(apiClientRecord.queryParams())
                        .addHeaders(headersAnnotation)
                        .addRequestBody(request)
                        .addResponseType()
                        .addAuthentication(apiClientRecord.auth())
                        .addProxy(apiClientRecord.reqProxy())
                        .addFollowRedirects(followRedirectsAnnotation)
                        .addDisableSSLVerification(apiClientRecord.disableSSLVerification())
                        .build();

                final var apiExecutor = new APIExecutorService();
                final var apiResponseOptional = apiExecutor.executeAPI(apiRequestRecord);
                return apiResponseOptional.orElse(null);
			}
		};
	}
}
