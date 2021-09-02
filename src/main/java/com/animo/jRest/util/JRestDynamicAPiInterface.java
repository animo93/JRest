package com.animo.jRest.util;

import java.util.Map;

public interface JRestDynamicAPiInterface<T> {

    /**
     * This is the method which should be used for dynamically invoking any JREST API , which is
     * already defined in the {@code service} interface
     * <p>To use this method , {@link com.animo.jRest.util.JRestDynamicAPiInterface} must be implemented
     * in the service interface </p>
     * <p> For example : (Service Execution) </p>
     * 	 <pre><code>
     * 	     MyApiInterface testInterface = testAPIHelper.createDynamicApi(MyApiInterface.class,"listRepos");
     * 	     APICall&#60;Void, Map&#60;String,Object&#62;&#62; call = testInterface.dynamicAPIInvocation("testUser");
     * 	     APICall&#60;Void,Map&#60;String,Object&#62;&#62; response = call.callMeNow();
     * 	 </code></pre>
     * @param args The actual arguments which are going to be passed to the method to be invoked
     * @return {@link com.animo.jRest.util.APICall}
     */
    APICall<Void, T> dynamicAPIInvocation(Object... args);
}
