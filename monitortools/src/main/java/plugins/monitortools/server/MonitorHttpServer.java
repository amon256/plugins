/**
 * MonitorHttpServer.java.java
 * @author FengMy
 * @since 2015年11月27日
 */
package plugins.monitortools.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import plugins.monitortools.server.handlers.MonitorHttpHandler;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

/**  
 * 功能描述：监控的httpserver
 * 
 * @author FengMy
 * @since 2015年11月27日
 */
public class MonitorHttpServer {
	private static final int DEFAULT_HTTP_PORT = 8121;
	private static final boolean PROTOTYPE_DEFAULT_HTTPS = false;
	private static final int DEFAULT_MAX_CONNECTIONS = 5;
	
	private int port = DEFAULT_HTTP_PORT;
	
	private boolean https = PROTOTYPE_DEFAULT_HTTPS;
	
	private String bindIp;
	
	public static void main(String[] args) throws IOException {
		new MonitorHttpServer().start();
	}
	
	public void start() throws IOException{
		HttpServerProvider provider = HttpServerProvider.provider();
		HttpServer server = null;
		InetSocketAddress addr = null;
		if(bindIp != null){
			addr = new InetSocketAddress(bindIp, DEFAULT_HTTP_PORT);
		}else{
			addr = new InetSocketAddress(DEFAULT_HTTP_PORT);
		}
		if(https){
			server = provider.createHttpsServer(addr, DEFAULT_MAX_CONNECTIONS);
		}else{
			server = provider.createHttpServer(addr, DEFAULT_MAX_CONNECTIONS);
		}
		server.createContext(Context.getContextPath(), new MonitorHttpHandler());
		server.start();
		System.out.println("java monitor server started.");
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


	public String getBindIp() {
		return bindIp;
	}

	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
	}

	public boolean isHttps() {
		return https;
	}

	public void setHttps(boolean https) {
		this.https = https;
	}
	
	
}
