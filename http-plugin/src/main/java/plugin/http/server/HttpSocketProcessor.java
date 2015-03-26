/**
 * HttpSocketProcessor.java.java
 * @author FengMy
 * @since 2014年10月30日
 */
package plugin.http.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import plugin.socket.SocketProcessor;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2014年10月30日
 */
public class HttpSocketProcessor implements SocketProcessor {

	@Override
	public void process(Socket socket) {
		System.out.println("in接收到来自:" + socket.getInetAddress().toString() + "的请求.");
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while((line = br.readLine()) != null){
				System.out.println(line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			HttpRequestWrap request = new HttpRequestWrap(socket.getInputStream()) {
				
			};
			request.setClientAddress(socket.getInetAddress().getHostAddress());
			
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
			bw.write("接收到来自:" + socket.getInetAddress().toString() + "的请求.");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
