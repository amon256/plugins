/**
 * 
 */
package plugins.installation.logs;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.ErrorHandler;

/**
 * @author fengmengyue
 *
 */
public class QuenceWriter extends QuietWriter {

	public QuenceWriter(Writer writer, ErrorHandler errorHandler) {
		super(writer, errorHandler);
	}
	
	@Override
	public void write(char[] cbuf) {
		write(cbuf, 0, cbuf.length);
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) {
		MessageWriter writer = ThreadLocalMessageWriter.get();
		if(writer != null && writer.isAvailable()){
			writer.write(new String(cbuf,off,len));
		}
	}
	
	@Override
	public void write(int c) throws IOException {
		write(new char[]{(char) c}, 0, 1);
	}
	
	@Override
	public void write(String str, int off, int len){
		char[] buff = new char[len];
		str.getChars(0, off+len, buff, 0);
		write(buff, 0, len);
	}
	
	@Override
	public void write(String str) {
		write(str, 0, str.length());
	}

}
