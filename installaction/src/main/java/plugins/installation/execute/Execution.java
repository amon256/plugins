/**
 * Execution.java.java
 * @author FengMy
 * @since 2015年6月2日
 */
package plugins.installation.execute;

import plugins.installation.config.InstallConfig;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年6月2日
 */
public interface Execution {

	/**
	 * 执行
	 * @param config
	 */
	public void execute(InstallConfig config) throws Exception;
	
}
