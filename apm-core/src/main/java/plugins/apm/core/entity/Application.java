/**
 * 
 */
package plugins.apm.core.entity;

import plugins.apm.tools.UpgradeApplication;



/**  
 * 功能描述：应用
 * 
 * @author FengMy
 * @since 2015年12月3日
 */
public class Application implements UpgradeApplication{
	/**
	 * 应用名称
	 */
	private String name;
	
	/**
	 * 应用编码
	 */
	private String number;
	
	/**
	 * 应用描述
	 */
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
