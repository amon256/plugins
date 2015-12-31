/**
 * 
 */
package plugins.installation.logs;

/**
 * @author fengmengyue
 *
 */
public class ThreadLocalMessageWriter {
	private static final ThreadLocal<MessageWriter> writers = new ThreadLocal<MessageWriter>();
	
	/**
	 * 获取接口
	 * @return
	 */
	public static MessageWriter get(){
		return writers.get();
	}
	
	/**
	 * 注册输出接口
	 * @param writer
	 */
	public static void register(MessageWriter writer){
		writers.set(writer);
	}
	
	/**
	 * 删除当前线程输出接口
	 */
	public static void remove(){
		writers.remove();
	}
	
}
