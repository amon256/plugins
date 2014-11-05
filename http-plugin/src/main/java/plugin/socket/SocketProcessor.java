/**
 * SocketProcessor.java.java
 * @author FengMy
 * @since 2014年10月30日
 */
package plugin.socket;

import java.net.Socket;

/**  
 * 功能描述：socket处理接口
 * 
 * @author FengMy
 * @since 2014年10月30日
 */
public interface SocketProcessor {
	/**
	 * 处理接收到的socket
	 * @param socket
	 */
	public void process(Socket socket);
}
