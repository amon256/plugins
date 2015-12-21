/**
 * Execution.java.java
 * @author FengMy
 * @since 2015年6月2日
 */
package plugins.installation.execute;

import org.dom4j.Element;

import plugins.installation.config.InstallConfig;

/**  
 * 功能描述：可执行过程接口
 * 
 * @author FengMy
 * @since 2015年6月2日
 */
public interface Execution {
	
	/**
	 * 从xml加载
	 * @param element
	 */
	public void load(Element element);

	/**
	 * 执行
	 * @param config
	 */
	public void execute(InstallConfig config) throws Exception;
	
	/**
	 * 执行前校验
	 * @param validate
	 */
	public boolean validate(InstallConfig config);
	
	/**
	 * 执行过程描述
	 * @param config
	 * @return
	 */
	public String info(InstallConfig config);
}
