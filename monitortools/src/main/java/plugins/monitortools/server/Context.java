/**
 * Context.java.java
 * @author FengMy
 * @since 2015年11月27日
 */
package plugins.monitortools.server;

import java.util.HashMap;
import java.util.Map;

import plugins.monitortools.server.handlers.DefaultHttpHandler;
import plugins.monitortools.server.handlers.MachineInfoHandler;
import plugins.monitortools.server.handlers.ResourceUsageHttpHandler;

import com.sun.net.httpserver.HttpHandler;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月27日
 */
public class Context {
	
	private static final Map<String,Class<? extends HttpHandler>> handlers = new HashMap<String, Class<? extends HttpHandler>>();
	static{
		Context.registerHandler("/machine", MachineInfoHandler.class);
		Context.registerHandler("/usage", ResourceUsageHttpHandler.class);
	}

	public static String getContextPath(){
		return "/";
	}
	
	public static HttpHandler getHttpHandler(String requestPath) throws InstantiationException, IllegalAccessException{
		if(handlers.containsKey(requestPath)){
			return handlers.get(requestPath).newInstance();
		}
		return new DefaultHttpHandler();
	}
	
	public static void registerHandler(String path,Class<? extends HttpHandler> clazz){
		if(handlers.containsKey(path)){
			throw new RuntimeException("path : " + path + " has exists");
		}
		handlers.put(path, clazz);
	}
}
