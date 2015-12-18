/**
 * 
 */
package plugins.installation.logs;

import java.io.OutputStreamWriter;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 自定义日志输出套件,允许接入自定义日志消息输出接口
 * 仅输出来自plugins.installation.包的日志
 * @author fengmengyue
 *
 */
public class MessageWriterAppender extends WriterAppender {
	
	private static final String LOG_CLASS_PACKAGE = "plugins.installation.";
	
	public MessageWriterAppender(){
		this.qw = new QuenceWriter(new OutputStreamWriter(System.out), errorHandler);
	}
	
	@Override
	public void append(LoggingEvent event) {
		if(event.getLocationInformation().getClassName().startsWith(LOG_CLASS_PACKAGE)){
			super.append(event);
		}
	}
}
