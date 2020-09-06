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
import com.animo.jRest.annotation.HEADERS;
import com.animo.jRest.annotation.PATH;
import com.animo.jRest.annotation.REQUEST;



/**
 * Created by animo on 23/12/17.
 */

public class APIHelper {

	private String baseURL;
	private Map<String,String> params;
	private RequestAuthentication auth;
	private RequestProxy reqProxy;
	private Map<String,String> headers;
	private static Logger logger = LogManager.getLogger(APIHelper.class);

	private APIHelper(APIBuilder builder){
		this.baseURL = builder.baseURL;
		this.params = builder.params;
		this.auth = builder.auth;
		this.reqProxy = builder.proxy;
		this.headers= builder.headers;
	}



	public static class APIBuilder{
		private Map<String, String> headers;
		private String baseURL;
		private Map<String,String> params;
		private RequestAuthentication auth;
		private RequestProxy proxy;

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

		public APIBuilder addUsernameAndPassword(String username,String password){
			if(this.auth == null){
				this.auth = new RequestAuthentication();
			}
			this.auth.setUsername(username);
			this.auth.setPassword(password);
			return this;
		}

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
		public APIBuilder addHeaders(Map<String, String> headers){
			if(this.headers ==null){
				this.headers= new HashMap<String, String>();
			}
			this.headers.putAll(headers);
			return this;
		}

		public APIHelper build(){
			return new APIHelper(this);
		}

	}

	@SuppressWarnings("unchecked")
	public <S> S createApi(Class<S> clazz){
		ClassLoader loader = clazz.getClassLoader();
		Class[] interfaces = new Class[]{clazz};


		Object object = Proxy.newProxyInstance(loader, interfaces, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Annotation requestAnnotation = method.getAnnotation(REQUEST.class);
				Annotation[][] att=method.getParameterAnnotations();
				Class[] parameterTypes = method.getParameterTypes();


				Annotation headersAnnotation = method.getAnnotation(HEADERS.class);
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


				logger.debug("final Url ",urlBuilder.toString());
				myRequestBean.setUrl(urlBuilder.toString());

				myRequestBean.setHeaders(headers);
				myRequestBean.setAuthentication(auth);
				myRequestBean.setProxy(reqProxy);
				myRequestBean.setRequestType(request.type());

				addRequestBody(args, att, parameterTypes, request, myRequestBean);


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