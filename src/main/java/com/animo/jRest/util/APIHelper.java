package com.animo.jRest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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

import org.apache.log4j.Logger;

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
	private static Logger logger = Logger.getLogger("APIHelper");

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
				Annotation headersAnnotation = method.getAnnotation(HEADERS.class);
				REQUEST request = (REQUEST) requestAnnotation;
				if(request == null){
					throw new Exception("No Request Annotation found");
				}

				RequestBean<Object> myRequestBean = new RequestBean<>();
				StringBuilder urlBuilder = new StringBuilder(baseURL);
				urlBuilder.append(request.endpoint());
				
				/*List<Object> argsAsList = Arrays.asList(args);*/
				
				
				try{
					if(args!=null){
						List<Object> argsAsList = Arrays.asList(args);
						
						if(argsAsList!=null && !argsAsList.isEmpty()){
							Map<String, String> map =argsAsList.stream()
									.filter(v -> (v.getClass().getAnnotation(PATH.class))!=null)
									.collect(Collectors.toMap(v -> "{"+v.getClass().getAnnotation(PATH.class).value().toString()+"}", v -> v.toString()));

							if(map!=null && !map.isEmpty()){
								map.forEach((k,v) -> {
									Pattern pattern = Pattern.compile(k);
									Matcher matcher = pattern.matcher(urlBuilder);
									int start =0;
									while(matcher.find(start)){
										urlBuilder.replace(matcher.start(), matcher.end(), v);
										start = matcher.start() + v.length();
									}
								});
							}
						}else {
							logger.info("No Path Arguments found");
							throw new Exception("No Path Arguments found");
						}
					}
				}catch (Exception e){
					logger.error("Unable to form url from Path Parameters",e);
					throw e;
				}
				
				if(params!=null && params.size()>0){
					urlBuilder.append("?");
					params.forEach((k,v) -> {
						urlBuilder.append(k+"="+v+"&");
					});

				}
				/*logger.debug("final Url ",urlBuilder.toString());*/
				System.out.println("final Url "+urlBuilder.toString());
				myRequestBean.setUrl(urlBuilder.toString());
				//myRequestBean.setAccessToken((String) args[0]);
				myRequestBean.setHeaders(headers);
				myRequestBean.setAuthentication(auth);
				myRequestBean.setProxy(reqProxy);
				myRequestBean.setRequestType(request.type());
				if(request.type().equals(HTTP_METHOD.POST) ||
						request.type().equals(HTTP_METHOD.PATCH)){
					if(args!=null){
						List<Object> argsAsList = Arrays.asList(args);
					
					if(argsAsList!=null && argsAsList.size()>0){
						argsAsList.forEach(v -> {
							if((v.getClass().getAnnotation(Body.class))!=null){
								myRequestBean.setRequestObject(v);
							}
						});
					}
					else{
						logger.info("No request body found");
						throw new Exception("No request body found");
					}
					}
				}
				//Type returnType = method.getGenericReturnType();
				Class<?> clazz = APICall.class;
				Object object = clazz.newInstance();
				APICall<Object, ?> apiCall = (APICall<Object, ?>) object;
				apiCall.setRequestBean(myRequestBean);
				Type type =  method.getGenericReturnType();
				if(type instanceof ParameterizedType){
					ParameterizedType pType = (ParameterizedType) type;
					for(Type t:pType.getActualTypeArguments()){
						apiCall.setType(t);
					}
				}
				else
					apiCall.setType(type);

				return apiCall;
			}
			
		});

		return (S) object;
	}


}