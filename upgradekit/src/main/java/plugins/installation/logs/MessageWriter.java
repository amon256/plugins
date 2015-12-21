/**
 * 
 */
package plugins.installation.logs;

/**
 * @author fengmengyue
 *
 */
public interface MessageWriter {

	/**
	 * 输出字符串
	 * @param message
	 */
	public void write(String message);
	
	/**
	 * 是否可用
	 * @return
	 */
	public boolean isAvailable();
}
