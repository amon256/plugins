/**
 * DefaultHttpHandler.java.java
 * @author FengMy
 * @since 2015年11月27日
 */
package plugins.monitortools.server.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import plugins.monitortools.server.ContentTypes;
import plugins.monitortools.server.Context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月27日
 */
public class DefaultHttpHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String url = exchange.getRequestURI().toString();
		String ctxPath = Context.getContextPath();
		String requestPath = url.substring(url.indexOf(ctxPath), url.indexOf("?") >= 0 ? url.indexOf("?") : url.length());
		
		//寻找requestPath在classpath中是否存在资源
		InputStream is = DefaultHttpHandler.class.getResourceAsStream(requestPath);
		if(is == null){
			//没有相应资源
			exchange.sendResponseHeaders(404,0);
			exchange.getResponseBody().flush();
			exchange.getRequestBody().close();
			return;
		}
		String fileSuffix = "";
		if(requestPath.lastIndexOf(".")>0){
			fileSuffix = requestPath.substring(requestPath.lastIndexOf(".") + 1);
		}
		exchange.getResponseHeaders().add("Content-Type", ContentTypes.getContentType(fileSuffix) + "; charset=UTF-8");
		exchange.sendResponseHeaders(200, is.available());
		OutputStream os = exchange.getResponseBody();
		byte[] buff = new byte[1024];
		int len = 0;
		while((len = is.read(buff)) != 0){
			os.write(buff, 0, len);
			os.flush();
		}
		os.flush();
		os.close();
	}

}
