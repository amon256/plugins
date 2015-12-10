/**
 * NativeCommandExecutor.java.java
 * @author FengMy
 * @since 2015年12月9日
 */
package plugins.upgradekit.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月9日
 */
public class NativeCommandExecutor {
	private static final Logger logger = LoggerFactory.getLogger(NativeCommandExecutor.class);

	public static void main(String[] args) {
		MessageWriter writer = new MessageWriter() {
			PrintWriter pw = new PrintWriter(System.out);
			@Override
			public void write(String message) {
				pw.write(message + "\n");
				pw.flush();
			}
		};
		executeNativeCommand(writer, "gbk", "java -version", new String[]{});
	}
	
	public static void executeNativeCommand(MessageWriter writer,String charset,String command,String[] params){
		try{
			logger.debug("cmd:{},params:{}",command,params);
			Process process = Runtime.getRuntime().exec(command,params);
			BufferedReader ebr = new BufferedReader(new InputStreamReader(process.getErrorStream(),charset));
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(),charset));
			String line = null;
			while((line = ebr.readLine()) != null){
				logger.debug(line);
				writer.write(line);
			}
			while((line = br.readLine()) != null){
				logger.debug(line);
				writer.write(line);
			}
		}catch(Exception e){
			logger.error("本地命令执行异常",e);
			if(writer != null){
				writer.write(e.getMessage());
			}
		}
	}
}
