/**
 * MachineInfoHandler.java.java
 * @author FengMy
 * @since 2015年11月27日
 */
package plugins.monitortools.server.handlers;

import java.io.IOException;
import java.io.OutputStream;

import plugins.monitortools.beans.SystemInfo;
import plugins.monitortools.server.ContentTypes;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月27日
 */
public class MachineInfoHandler implements HttpHandler {
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try{
			SystemInfo systemInfo = (SystemInfo) new SystemInfo().instance();
			String json = JSON.toJSONString(systemInfo);
			exchange.getResponseHeaders().add(ContentTypes.CONTENT_TYPE, "text/json;charset=UTF-8");
			exchange.sendResponseHeaders(200, json.getBytes("utf-8").length);
			OutputStream os = exchange.getResponseBody();
			os.write(json.getBytes("utf-8"));
			os.flush();
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
