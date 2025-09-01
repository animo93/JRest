package com.animo.jRest.util;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.animo.jRest.model.RequestAuthentication;
import com.animo.jRest.model.RequestBean;
import com.animo.jRest.model.RequestProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.animo.jRest.annotation.Body;
import com.animo.jRest.annotation.FollowRedirects;
import com.animo.jRest.annotation.HEADER;
import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.PATH;
import com.animo.jRest.annotation.Query;
import com.animo.jRest.annotation.QueryMap;
import com.animo.jRest.annotation.REQUEST;


/**
 * Helper method which is used for initializing and building the initial API request.
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
//TODO: Rename to APIClient or APIService
public final class APIService {

	private final String baseURL;
	private Map<String, String> params;
	private final RequestAuthentication auth;
	private final RequestProxy reqProxy;
	private final boolean disableSSLVerification;
	private final static Logger logger = LogManager.getLogger(APIService.class);

	private APIService(APIBuilder builder) {
		this.baseURL = builder.baseURL;
		this.params = builder.params;
		this.auth = builder.auth;
		this.reqProxy = builder.proxy;
		this.disableSSLVerification = builder.disableSSLVerification;
	}
    //TODO: Rename to APIClientBuilder
	public static class APIBuilder {
		private final String baseURL;
		private Map<String,String> params;
		private RequestAuthentication auth;
		private RequestProxy proxy;
		private boolean disableSSLVerification;

		public APIBuilder(final String baseURL){
			this.baseURL = baseURL;
		}

		public static APIBuilder builder(final String baseURL){
			return new APIBuilder(baseURL);
		}

		public APIBuilder addParameter(final String key,final String value){
			if(params==null){
				params = new HashMap<>();
			}
			params.put(key, value);
			return this;
		}

		public APIBuilder addAllParameters(final Map<String,String> params) {
			if(this.params == null){
				this.params = new HashMap<>();
			}
			this.params.putAll(params);
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
				this.auth = new RequestAuthentication();
			}
			this.auth.setUsername(username);
			this.auth.setPassword(password);
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

		public APIService build(){
			return new APIService(this);
		}

	}

	/**
	 * Create an implementation of the API endpoints defined by the {@code service} interface.
	 * <p>The relative path for a given method is obtained from an annotation on the method describing
	 * the request type.The built in methods are {GET},{PUT} ,{POST},{PATCH} , {DELETE}
	 * <p>Method parameters can be used to replace parts of the URL by annotating them with {Path}. Replacement sections are denoted by an identifier surrounded by
	 * curly braces (e.g., "{foo}").
	 * 
	 * <p>The body of a request is denoted by the {@link com.animo.jRest.annotation.Body @Body} annotation.
	 * The body would be converted to JSON via Google GSON
	 * 
	 * <p>By default, methods return a {@link APIRequest APIRequest} which represents the HTTP request. The generic
	 * parameter of the call is the response body type and will be converted by Jackson Object Mapper
	 * 
	 * <p>For example :
	 * <pre><code>
	 * public interface MyApiInterface {
	 *
	 *	&#64;REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
	 *	{@code APIRequest<ApiResponse> listRepos(@PATH(value = "user") String user);}
	 *
	 *}</code></pre>
	 * 
	 * @param clazz service.class
	 * @param <S> Service Class
	 * @return {@code service}
	 */
	@SuppressWarnings("unchecked")
	public final <S> S createApi(final Class<S> clazz) {
        //TODO: Add test scenario where a random interface is passed which doesn't have any REQUEST annotation
		final ClassLoader loader = clazz.getClassLoader();
		final Class[] interfaces = new Class[]{clazz};

		final Object object = Proxy.newProxyInstance(loader, interfaces,setInvocationHandler(null));

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
	 *
	 *  {@code APIRequest<ApiResponse> dynamicApiInvocation(Object... args)}
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
	 * @param clazz service.class
	 * @param <S> Service Class
	 * @param methodName The method name which is going to be dynamically invoked
	 * @param parameterTypes The parameter types for the method going to be dynamically invoked
	 * @throws NoSuchMethodException When the method doesn't exists in service class
	 * @return {@code service}
	 */
	//TODO Check why do we need methodName here
	public final <S> S createDynamicApi(final Class<S> clazz,final String methodName,Class... parameterTypes) throws NoSuchMethodException {

		final ClassLoader loader = clazz.getClassLoader();
		final Class[] interfaces = new Class[]{clazz};

		Method methodToCall = clazz.getMethod(methodName,parameterTypes);
		final Object object = Proxy.newProxyInstance(loader, interfaces,setInvocationHandler(methodToCall));

		return (S) object;

	}

	private InvocationHandler setInvocationHandler(final Method methodToCall){
		return new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(methodToCall != null){
					method = methodToCall;
					args = (Object[]) args[0];
				}
				final Annotation requestAnnotation = method.getAnnotation(REQUEST.class);
				final Annotation[][] parameterAnnotation = method.getParameterAnnotations();
				final Class[] parameterTypes = method.getParameterTypes();

				final Annotation headersAnnotation = method.getAnnotation(HEADERS.class);

				FollowRedirects followRedirectsAnnotation = method.getAnnotation(FollowRedirects.class);

				REQUEST request = (REQUEST) requestAnnotation;
				//Annotation t=att[0][0];
				if(request == null){
					throw new Exception("No Request Annotation found");
				}

				final RequestBean<Object> requestBean = new RequestBean<>();
				final StringBuilder urlBuilder = new StringBuilder(baseURL);
				urlBuilder.append(request.endpoint());

				final Parameter[] parameters = method.getParameters();

				addPathParameters(args, urlBuilder, parameters);

				addQueryParameters(args, urlBuilder, parameters);

				addHeaders(requestBean, headersAnnotation, parameters, args);

				logger.debug("final Url {}",urlBuilder.toString());
				requestBean.setUrl(urlBuilder.toString());

				requestBean.setAuthentication(auth);
				requestBean.setProxy(reqProxy);
				requestBean.setRequestType(request.type());
				requestBean.setDisableSSLVerification(disableSSLVerification);

				addRequestBody(args, request, requestBean,parameters);

				if(followRedirectsAnnotation != null){
                    requestBean.setFollowRedirects(followRedirectsAnnotation.value());
                }
				final Type apiResponseType =  method.getGenericReturnType();
                /** TODO: Validate the return type
                 * If the return type is of type APIResponse , execute synchronously
                 * If the return type is of type Future<APIResponse> , execute asynchronously and return a Future
                 * If the return type is of type void and has APICallBack as parameter , execute asynchronously and return via callback
                 **/
                // Return type should always be of type APIRequest<Response>
				if(apiResponseType instanceof ParameterizedType pType && pType.getRawType().equals(APIRequest.class)){
                    // Since APIRequest<Response> has only one genericType , returning the first one
                    var responseType = pType.getActualTypeArguments()[0];
                    Class<?> responseClass;
                    if(responseType instanceof ParameterizedType){
                        responseClass = (Class<?>) ((ParameterizedType) responseType).getRawType();
                    } else if (responseType instanceof Class){
                        responseClass = (Class<?>) responseType;
                    } else{
                        throw new Exception("Invalid Response Type");
                    }
                    //TODO: invoking a method should ideally make the api call and return a result
					return new APIRequest<>(requestBean,responseClass);
				}else{
					throw new Exception("Invalid method declared in Interface");
				}
			}

			private void addHeaders(final RequestBean<Object> myRequestBean, final Annotation headersAnnotation, final Parameter[] parameters, final Object[] args) {
				final HEADERS headers = (HEADERS) headersAnnotation;
				final String[] requestHeadersFromMethod;
				Map<String, String> requestHeadersMap = new HashMap<>();
				if(headers != null) {
					requestHeadersFromMethod = headers.value();

					logger.debug("Request Headers from Method {} " , Arrays.toString(requestHeadersFromMethod));
					requestHeadersMap = convertToHeadersMap(requestHeadersFromMethod);
				}

				final Map<String, String> requestHeadersFromParam = getParamHeaders(parameters, args);

				//String[] requestHeaders = concatenateHeaders(requestHeadersFromMethod,requestHeadersFromParam);

				requestHeadersMap.putAll(requestHeadersFromParam);

				myRequestBean.setHeaders(requestHeadersMap);

			}

			private String[] concatenateHeaders(final String[] requestHeadersFromMethod, final String[] requestHeadersFromParam) {
				final String[] requestHeaders = new String[] {};
				System.arraycopy(requestHeadersFromMethod, 0, requestHeaders, 0, requestHeadersFromParam.length);
				System.arraycopy(requestHeadersFromParam, 0, requestHeaders, requestHeadersFromParam.length, requestHeadersFromParam.length);

				return requestHeaders;
			}

			private Map<String, String> getParamHeaders(final Parameter[] parameters, final Object[] args) {
				Map<String, String> paramValues = new HashMap<>();
				try {
					for(int i = 0,j = 0; i < parameters.length; i++) {
						if(parameters[i].getAnnotation(HEADER.class)!=null) {
							HEADER header = parameters[i].getAnnotation(HEADER.class);
							paramValues = (Map<String, String>) args[i];
						}
					}
				}catch (ClassCastException ex) {
					logger.error("Unable to get ParamHeaders " + ex);
					throw new RuntimeException("Header Parameters should be passed in Map<key:value> format ");
				}


				logger.debug("Request Headers from Params {}" ,paramValues);
				return paramValues;
			}

			private Map<String, String> convertToHeadersMap(final String[] requestHeaders) {
				Map<String, String> headersMap = new HashMap<>();
				for(String header:requestHeaders) {
					if(!header.contains(":")) {
						throw new RuntimeException("Header data invalid ...Should be using <key>:<value> String format " + header);
					}
					headersMap.put(header.split(":")[0], header.split(":")[1]);
				}
				logger.debug("Final Request Headers Map {} " ,headersMap);
				return headersMap;
			}

			private void addRequestBody(final Object[] args,final REQUEST request,
										final RequestBean<Object> myRequestBean,final Parameter[] parameters) {
				if(request.type().equals(HTTP_METHOD.POST) ||
						request.type().equals(HTTP_METHOD.PATCH) ||
						request.type().equals(HTTP_METHOD.PUT)){
					for (int i = 0; i < parameters.length; i++) {
						if(parameters[i].getAnnotation(Body.class)!=null){
							Body body = (Body) parameters[i].getAnnotation(Body.class);
							logger.debug("Going to set request body {}",args[i]);
							if(args[i]==null){
								throw new NullPointerException("Request Body cannot be Null");
							}
							//TODO: Add null check and throw exception
							myRequestBean.setRequestObject(args[i]);
						}
					}
				}
			}

			private void prepareQueryParamMap(final Object[] args, final Parameter[] parameters) throws UnsupportedEncodingException {
				/* put all the found query parameters in Query and QueryMap,
				into the paramters map to be converted into query string*/
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i].getAnnotation(Query.class) != null) {
						if (params == null) params = new HashMap<>();
						Query query = (Query) parameters[i].getAnnotation(Query.class);
						String queryKey = query.value();
						if (queryKey != null && !queryKey.isEmpty()) {

							String queryValue = null;
							try {
								queryValue = (String) args[i];
							} catch (ClassCastException ex) {
								logger.error("Unable to add Query Params ", ex);
								throw new InvalidParameterException("Query parameter should be passed in string format only ");
							}

							if (queryValue != null) {
								if (!query.encoded()) {
									queryKey = URLEncoder.encode(queryKey, StandardCharsets.UTF_8);
									queryValue = URLEncoder.encode(queryValue, StandardCharsets.UTF_8);
								}

								params.put(queryKey, queryValue);
							}
						}
					} else if (parameters[i].getAnnotation(QueryMap.class) != null) {
						if (params == null) params = new HashMap<>();
						QueryMap queryMap = (QueryMap) parameters[i].getAnnotation(QueryMap.class);
						Map<String, String> queryMapValue = null;
						try {
							queryMapValue = (Map<String, String>) args[i];
						} catch (ClassCastException ex) {
							logger.error("Unable to add Query Params ", ex);
							throw new InvalidParameterException("Query parameter should be passed in Map format only ");
						}

						if (queryMapValue != null && !queryMapValue.isEmpty()) {
							params.putAll(queryMapValue);
						}
					}

				}
				logger.debug("Query params fetched from Params " + params);
			}

			private void addQueryParameters(final Object[] args, final StringBuilder urlBuilder, final Parameter[] parameters) throws UnsupportedEncodingException {
				prepareQueryParamMap(args,parameters);
				if(params != null && params.size() > 0) {
					urlBuilder.append("?");
					params.forEach((k, v) -> urlBuilder.append(k).append("=").append(v).append("&"));
					urlBuilder.deleteCharAt(urlBuilder.length()-1);
				}
			}

			private void addPathParameters(final Object[] args, final StringBuilder urlBuilder, final Parameter[] parameters)
					throws Exception {
				for(int i = 0 ; i < parameters.length ; i++) {
					if(parameters[i].getAnnotation(PATH.class) != null) {
						PATH path = (PATH) parameters[i].getAnnotation(PATH.class);
						final String value = path.value();
						final Pattern pattern = Pattern.compile("\\{" + value + "\\}");
						final Matcher matcher = pattern.matcher(urlBuilder);
						int start = 0;
						while(matcher.find(start)) {
							urlBuilder.replace(matcher.start(), matcher.end(), String.valueOf(args[i]));
							start = matcher.start() + String.valueOf(args[i]).length();
						}
					}
				}

				if(urlBuilder.toString().contains("{") &&
						urlBuilder.toString().contains("}")) {
					throw new Exception("Undeclared PATH variable found ..Please declare them in the interface");
				}
			}
		};
	}
}
