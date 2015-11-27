/**
 * MonitorHttpHandler.java.java
 * @author FengMy
 * @since 2015年11月27日
 */
package plugins.monitortools.server.handlers;

import java.io.IOException;

import plugins.monitortools.server.Context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月27日
 */
public class MonitorHttpHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String url = exchange.getRequestURI().toString();
		String ctxPath = Context.getContextPath();
		String requestPath = url.substring(url.indexOf(ctxPath), url.indexOf("?") >= 0 ? url.indexOf("?") : url.length());
		HttpHandler handler;
		try {
			handler = Context.getHttpHandler(requestPath);
			handler.handle(exchange);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

}
