/**
 * HttpServer.java.java
 * @author FengMy
 * @since 2014年10月30日
 */
package plugin.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**  
 * 功能描述：http服务
 * 
 * @author FengMy
 * @since 2014年10月30日
 */
public class SocketBinder extends Thread{

	private static final int EFFECT_STATE = 1;//服务状态-有效
	private static final int INVALID_STATE = 0;//服务状态-失效
	private int listenPort;//监听端口
	private int threadPoolSize;//消息处理线程池大小
	private ExecutorService threadPool;
	private ServerSocket serverSocket;//socket
	private SocketProcessor socketProcessor;//socket处理者
	private int state;//服务状态:  0表示中止，1表示有效
	
	public SocketBinder(int listenPort,int threadPoolSize,SocketProcessor socketProcessor){
		this.listenPort = listenPort;
		this.state = 1;
		this.threadPoolSize = threadPoolSize;
		this.socketProcessor = socketProcessor;
	}
	
	private void init() throws IOException{
		serverSocket = new ServerSocket(listenPort);
		this.threadPool = Executors.newFixedThreadPool(this.threadPoolSize);
	}
	
	public void unBind(){
		this.threadPool.shutdown();
		this.state = INVALID_STATE;
	}

	@Override
	public void run() {
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		while(this.state == EFFECT_STATE){
			try{
				Socket socket = this.serverSocket.accept();
				this.threadPool.execute(new SocketExecuteThread(socket, socketProcessor));
			}catch(Exception e){
				e.printStackTrace();
				if(this.socketProcessor == null || this.serverSocket.isClosed()){
					try {
						init();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	/**  
	 * 功能描述：socket接收处理线程
	 * 
	 * @author FengMy
	 * @since 2014年10月30日
	 */
	private class SocketExecuteThread extends Thread{
		private Socket socket;
		private SocketProcessor socketProcessor;
		private SocketExecuteThread(Socket socket,SocketProcessor socketProcessor){
			this.socket = socket;
			this.socketProcessor = socketProcessor;
		}
		@Override
		public void run() {
			this.socketProcessor.process(this.socket);
			if(!this.socket.isClosed()){
				try {
					this.socket.close();
				} catch (IOException e) {
					//TODO
					e.printStackTrace();
				}
			}
		}
	}
}
