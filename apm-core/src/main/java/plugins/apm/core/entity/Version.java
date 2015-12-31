/**
 * Version.java.java
 * @author FengMy
 * @since 2015年12月3日
 */
package plugins.apm.core.entity;

import plugins.apm.tools.UpgradeVersion;


public class Version implements UpgradeVersion{
	/**
	 * 版本号
	 */
	private String number;
	
	/**
	 * 版本升级文件
	 */
	private String file;
	
	/**
	 * 原文件名
	 */
	private String fileName;
	
	/**
	 * 升级过程配置文件
	 */
	private String configFile;
	
	/**
	 * 升级参数文件原文件名
	 */
	private String configFileName;
	
	/**
	 * 应用
	 */
	private Application application;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}
