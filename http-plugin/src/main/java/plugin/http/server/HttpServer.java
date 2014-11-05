/**
 * HttpServer.java.java
 * @author FengMy
 * @since 2014年10月30日
 */
package plugin.http.server;

import plugin.socket.SocketBinder;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2014年10月30日
 */
public class HttpServer {

	private static final int DEFAULT_PORT = 8971;//默认http端口监听
	private static final int DEFAULT_THREAD_SIZE = 5;//默认http处理线程池大小

	private int port;//http监听端口
	private int threadSize;//http请求处理线程池
	private SocketBinder socketBinder;
	public HttpServer(){
		this(DEFAULT_PORT,DEFAULT_THREAD_SIZE);
	}
	
	public HttpServer(int port,int threadSize){
		this.port = port;
		this.threadSize = threadSize;
		this.startListener();
		System.out.println("http服务己启动，监听端口:" + this.port);
	}
	
	private void startListener(){
		this.socketBinder = new SocketBinder(this.port, this.threadSize, new HttpSocketProcessor());
		this.socketBinder.start();
	}
	
	
	public static void main(String[] args) {
		new HttpServer(8899, 2);
	}
}
