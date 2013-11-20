package org.jbpm.ee.services.ejb.interceptors;

import java.util.Map;

import org.jboss.ejb.client.EJBClientInterceptor;
import org.jboss.ejb.client.EJBClientInvocationContext;
import org.jbpm.ee.services.model.LazyDeserializingMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapSerializationInterceptor implements EJBClientInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(MapSerializationInterceptor.class);
	
	@Override
	public void handleInvocation(EJBClientInvocationContext context) throws Exception {
		if(!InterceptorUtil.requiresClassloaderInterception(context.getInvokedMethod())) {
			LOG.info("Method: "+context.getInvokedMethod().getName()+" does not require preprocessing.");
			context.sendRequest();
			return;
		}
		LOG.info("Method: "+context.getInvokedMethod().getName()+"does require preprocessor.");
		
		
		//look for the map objects, and convert them to lazy deserializing map.
		for(int i=0, j=context.getParameters().length; i<j; i++) {
			Object parameter = context.getParameters()[i];
			
			if(Map.class.isAssignableFrom(parameter.getClass())) {
				LOG.info("Map found in parameters of method: "+context.getInvokedMethod().getName());
				LazyDeserializingMap map = new LazyDeserializingMap();
				map.putAll((Map<String, Object>)parameter);
				
				context.getParameters()[i] = map;
				LOG.debug("Replaced map with serializable map.");
			}
		}

        context.sendRequest();
	}
	
		

	@Override
	public Object handleInvocationResult(EJBClientInvocationContext context) throws Exception {
		return context.getResult();
	}

}
