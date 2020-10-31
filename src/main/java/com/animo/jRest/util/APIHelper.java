package com.animo.jRest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.animo.jRest.annotation.Body;
import com.animo.jRest.annotation.FollowRedirects;
import com.animo.jRest.annotation.HEADER;
import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.PATH;
import com.animo.jRest.annotation.REQUEST;



/**
 * Helper method which is used for initializing and building the initial API request.
 * <p>It adapts a Java Interface to HTTP calls by using annotation on the declared method. Instances can be created
 * by passing the builder method to generate an implementation.<p>
 * For example :-
 * <pre><code>
 * APIHelper myApiHelper = APIHelper.APIBuilder
 *			.builder("https://api.github.com/")
 *			.build();
 * MyApiInterface myApiInterface = myApiHelper.createApi(MyApiInterface.class);
 * </code></pre>
 * 
 * @author animo
 */

public class APIHelper {

	private String baseURL;
	private Map<String,String> params;
	private RequestAuthentication auth;
	private RequestProxy reqProxy;
	private boolean disableSSLVerification;
	private static Logger logger = LogManager.getLogger(APIHelper.class);

	private APIHelper(APIBuilder builder){
		this.baseURL = builder.baseURL;
		this.params = builder.params;
		this.auth = builder.auth;
		this.reqProxy = builder.proxy;
		this.disableSSLVerification = builder.disableSSLVerification;
	}



	public static class APIBuilder{
		private String baseURL;
		private Map<String,String> params;
		private RequestAuthentication auth;
		private RequestProxy proxy;
		private boolean disableSSLVerification;

		public APIBuilder(String baseURL){
			this.baseURL = baseURL;
		}

		public static APIBuilder builder(String baseURL){
			return new APIBuilder(baseURL);
		}

		public APIBuilder addParameter(String key,String value){
			if(params==null){
				params = new HashMap<String, String>();
				System.out.println("key"+key);
			}
			params.put(key, value);
			return this;

		}

		public APIBuilder addAllParameters(Map<String,String> params){
			if(this.params==null){
				this.params = new HashMap<String, String>();
			}
			this.params.putAll(params);
			return this;
		}

		/**
		 * Username and Password used for making REST calls via Basic authentication
		 * @param username
		 * @param password
		 * @return
		 */
		public APIBuilder addUsernameAndPassword(String username,String password){
			if(this.auth == null){
				this.auth = new RequestAuthentication();
			}
			this.auth.setUsername(username);
			this.auth.setPassword(password);
			return this;
		}

		/**
		 * Proxy details used while building the APICall , if the client is behind any Proxy 
		 * @param proxyURL
		 * @param username
		 * @param password
		 * @param port
		 * @return
		 */

		public APIBuilder addProxy(String proxyURL , String username, String password , int port){
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
		 * @param disableSSLVerification
		 * @return
		 */

		public APIBuilder setDisableSSLVerification(boolean disableSSLVerification) {
			this.disableSSLVerification = disableSSLVerification;
			return this;
		}

		public APIHelper build(){
			return new APIHelper(this);
		}

	}

	/**
	 * Create an implementation of the API endpoints defined by the {@code service} interface.
	 * <p>The relative path for a given method is obtained from an annotation on the method describing
	 * the request type.The built in methods are {@link com.animo.jRest.util.HTTP_METHOD.GET GET},
	 * {@link com.animo.jRest.util.HTTP_METHOD.PUT PUT} ,{@link com.animo.jRest.util.HTTP_METHOD.POST POST},
	 * {@link com.animo.jRest.util.HTTP_METHOD.PATCH PATCH} , {@link com.animo.jRest.util.HTTP_METHOD.DELETE DELETE}
	 * 
	 * <p>Method parameters can be used to replace parts of the URL by annotating them with {@link
	 * com.animo.jRest.annotation.Path @Path}. Replacement sections are denoted by an identifier surrounded by
	 * curly braces (e.g., "{foo}").
	 * 
	 * <p>The body of a request is denoted by the {@link com.animo.jRest.annotation.Body @Body} annotation.
	 * The body would be converted to JSON via Google GSON
	 * 
	 * <p>By default, methods return a {@link com.animo.jRest.util.APICall APICall} which represents the HTTP request. The generic
	 * parameter of the call is the response body type and will be converted by Jackson Object Mapper
	 * 
	 * <p>For example :
	 * <pre><code>
	 * public interface MyApiInterface {
	 *
	 *	&#64;REQUEST(endpoint = "/users/{user}/repos",type = HTTP_METHOD.GET)
	 *	APICall<Void,ApiResponse> listRepos(@PATH(value = "user") String user);
	 *
	 *}</code></pre>
	 * 
	 * @param service.class
	 * @return {@code service}
	 */
	@SuppressWarnings("unchecked")
	public <S> S createApi(Class<S> clazz){
		ClassLoader loader = clazz.getClassLoader();
		Class[] interfaces = new Class[]{clazz};


		Object object = Proxy.newProxyInstance(loader, interfaces, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Annotation requestAnnotation = method.getAnnotation(REQUEST.class);
				Annotation[][] parameterAnnotation=method.getParameterAnnotations();
				Class[] parameterTypes = method.getParameterTypes();


				Annotation headersAnnotation = method.getAnnotation(HEADERS.class);

				FollowRedirects followRedirectsAnnotation = method.getAnnotation(FollowRedirects.class);



				REQUEST request = (REQUEST) requestAnnotation;
				//Annotation t=att[0][0];
				if(request == null){
					throw new Exception("No Request Annotation found");
				}

				RequestBean<Object> myRequestBean = new RequestBean<>();
				StringBuilder urlBuilder = new StringBuilder(baseURL);
				urlBuilder.append(request.endpoint());

				Parameter[] parameters = method.getParameters();

				addPathParameters(args, urlBuilder, parameters);

				addQueryParameters(urlBuilder);

				addHeaders(myRequestBean,headersAnnotation,parameters,args);


				logger.debug("final Url "+urlBuilder.toString());
				myRequestBean.setUrl(urlBuilder.toString());

				
				myRequestBean.setAuthentication(auth);
				myRequestBean.setProxy(reqProxy);
				myRequestBean.setRequestType(request.type());
				myRequestBean.setDisableSSLVerification(disableSSLVerification);

				addRequestBody(args, parameterAnnotation, parameterTypes, request, myRequestBean);

				if(followRedirectsAnnotation!=null)
					myRequestBean.setFollowRedirects(followRedirectsAnnotation.value());



				Class<?> clazz = APICall.class;
				Object object = clazz.newInstance();
				APICall<Object, ?> myCall = (APICall<Object, ?>) object;
				myCall.setRequestBean(myRequestBean);
				Type type =  method.getGenericReturnType();
				if(type instanceof ParameterizedType){
					ParameterizedType pType = (ParameterizedType) type;
					for(Type t:pType.getActualTypeArguments()){
						myCall.setType(t);	
					}
				}
				else
					myCall.setType(type);

				return myCall;

			}

			private void addHeaders(RequestBean<Object> myRequestBean, Annotation headersAnnotation, Parameter[] parameters, Object[] args) {
				
				HEADERS headers = (HEADERS) headersAnnotation;
				String[] requestHeadersFromMethod = new String[] {};
				Map<String,String> requestHeadersMap = new HashMap<String, String>();
				if(headers!=null) {
					requestHeadersFromMethod = headers.value();
					
					logger.debug("Request Headers from Method"+requestHeadersFromMethod);
					requestHeadersMap = convertToHeadersMap(requestHeadersFromMethod);
				}
				
				
				Map<String, String> requestHeadersFromParam = getParamHeaders(parameters,args);
				
				//String[] requestHeaders = concatenateHeaders(requestHeadersFromMethod,requestHeadersFromParam);
				
				requestHeadersMap.putAll(requestHeadersFromParam);
				
				myRequestBean.setHeaders(requestHeadersMap);
				
			}

			private String[] concatenateHeaders(String[] requestHeadersFromMethod, String[] requestHeadersFromParam) {
				
				String[] requestHeaders = new String[] {};
				System.arraycopy(requestHeadersFromMethod, 0, requestHeaders, 0, requestHeadersFromParam.length);
				System.arraycopy(requestHeadersFromParam, 0, requestHeaders, requestHeadersFromParam.length, requestHeadersFromParam.length);
				
				return requestHeaders;
			}

			private Map<String,String> getParamHeaders(Parameter[] parameters, Object[] args) {
				Map<String,String> paramValues = new HashMap<String, String>();
				try {
					for(int i=0,j=0;i<parameters.length;i++) {
						if(parameters[i].getAnnotation(HEADER.class)!=null) {
							HEADER header = parameters[i].getAnnotation(HEADER.class);
							paramValues = (Map<String,String>) args[i];
						}
					}
				}catch (ClassCastException ex) {
					logger.error("Unable to get ParamHeaders "+ex);
					throw new RuntimeException("Header Parameters should be passed in Map<key:value> format ");
				}
				
				
				logger.debug("Request Headers from Params "+paramValues);
				return paramValues;
			}

			private Map<String, String> convertToHeadersMap(String[] requestHeaders) {
				Map<String,String> headersMap = new HashMap<String,String>();
				for(String header:requestHeaders) {
					if(!header.contains(":")) {
						throw new RuntimeException("Header data invalid ...Should be using <key>:<value> String format "+header);
					}
					headersMap.put(header.split(":")[0], header.split(":")[1]);
				}
				logger.debug("Final Request Headers Map "+headersMap);
				return headersMap;
			}

			private void addRequestBody(Object[] args, Annotation[][] att, Class[] parameterTypes, REQUEST request,
					RequestBean<Object> myRequestBean) throws Exception {
				if(request.type().equals(HTTP_METHOD.POST) ||
						request.type().equals(HTTP_METHOD.PATCH) ||
						request.type().equals(HTTP_METHOD.PUT)){
					int i=0;
					for(Annotation[] annotations : att){
						Class parameterType = parameterTypes[i++];

						for(Annotation annotation : annotations){
							if(annotation instanceof Body){
								Body myAnnotation = (Body) annotation;
								logger.debug("param: " , parameterType.getName());

								if(args!=null){
									List<Object> argsAsList = Arrays.asList(args);
									logger.debug("argsASList :",argsAsList);
									if(argsAsList!=null && argsAsList.size()>0){
										argsAsList.forEach(arg -> {
											if(arg.getClass().equals(parameterType)){
												myRequestBean.setRequestObject(arg);
											}
										});
									}
								}
							}
						}
					}
					//Commenting this out , since some post requests can be made without a body
					/*if(myRequestBean.getRequestObject()==null){
						logger.error("No request body found");
						throw new Exception("No request body found");
					}*/

				}
			}

			private void addQueryParameters(StringBuilder urlBuilder) {
				if(params!=null && params.size()>0){
					urlBuilder.append("?");
					params.forEach((k,v) -> {
						urlBuilder.append(k+"="+v+"&");
					});

				}
			}

			private void addPathParameters(Object[] args, StringBuilder urlBuilder, Parameter[] parameters)
					throws Exception {
				for(int i=0;i<parameters.length;i++){
					if(parameters[i].getAnnotation(PATH.class)!=null){
						PATH path = (PATH) parameters[i].getAnnotation(PATH.class);
						String value = path.value();
						Pattern pattern = Pattern.compile("\\{"+value+"\\}");
						Matcher matcher = pattern.matcher(urlBuilder);
						int start =0;
						while(matcher.find(start)){
							urlBuilder.replace(matcher.start(), matcher.end(), String.valueOf(args[i]));
							start = matcher.start() + String.valueOf(args[i]).length();
						}
					}
				}

				if(urlBuilder.toString().contains("{") &&
						urlBuilder.toString().contains("}")){
					throw new Exception("Undeclared PATH variable found ..Please declare them in the interface");
				}
			}

		});

		return (S) object;
	}


}