package com.animo.jRest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Created by animo on 23/12/17.
 */

public class MyApiResponse {

    @SuppressWarnings("unchecked")
	public static <S> S createApi(Class<S> clazz){
        ClassLoader loader = clazz.getClassLoader();
        Class[] interfaces = new Class[]{clazz};
        

        Object object = Proxy.newProxyInstance(loader, interfaces, new InvocationHandler() {
			@Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Annotation requestAnnotation = method.getAnnotation(REQUEST.class);
                Annotation headersAnnotation = method.getAnnotation(HEADERS.class);
                REQUEST request = (REQUEST) requestAnnotation;
                if(request == null){
                	request = new REQUEST() {
						
						@Override
						public Class<? extends Annotation> annotationType() {
							// TODO Auto-generated method stub
							return null;
						}
						
						@Override
						public String url() {
							// TODO Auto-generated method stub
							return null;
						}
						
						@Override
						public HTTP_METHOD type() {
							// TODO Auto-generated method stub
							return null;
						}
					};
                }

                MyRequestBean<Object> myRequestBean = new MyRequestBean<>();
                myRequestBean.setAccessToken((String) args[0]);
                myRequestBean.setUrl((String) args[1]);
                myRequestBean.setRequestType(request.type());
                if(request.type().equals(HTTP_METHOD.POST) ||
                        request.type().equals(HTTP_METHOD.PATCH)){
                    if(!(args.length==3)){
                        throw new Exception("No request Body found ");
                    }else{
                        myRequestBean.setRequestObject(args[2]);
                    }
                }
                //Type returnType = method.getGenericReturnType();
                Class<?> clazz = MyCall.class;
                Object object = clazz.newInstance();
                MyCall<Object, ?> myCall = (MyCall<Object, ?>) object;
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
        });

        return (S) object;
    }


}
