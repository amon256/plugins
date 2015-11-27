/**
 * SystemInfo.java.java
 * @author FengMy
 * @since 2015年11月27日
 */
package plugins.monitortools.beans;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月27日
 */
public class SystemInfo implements MonitorInterface{
	
	/**
	 * 操作系统名称
	 */
	private String name;
	
	/**
	 * 操作系统的架构
	 */
	private String arch;
	
	/**
	 * 可用CPU核数
	 */
	private int availableProcessors;
	
	/**
	 * 系统版本
	 */
	private String version;
	
	/**
	 * Java 虚拟机实现名称。 
	 */
	private String vmName;
	
	/**
	 * Java 虚拟机实现供应商。 
	 */
	private String vmVendor;
	
	/**
	 *  Java 虚拟机实现版本。 
	 */
	private String vmVersion;
	
	/**
	 * Java虚拟机启动时间
	 */
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date vmStartTime;
	
	private Map<String,String> systemProperties;
	
	
	@Override
	public MonitorInterface instance() {
		OperatingSystemMXBean mbean =  ManagementFactory.getOperatingSystemMXBean();
		this.setName(mbean.getName());
		this.setAvailableProcessors(mbean.getAvailableProcessors());
		this.setArch(mbean.getArch());
		this.setVersion(this.getVersion());
		
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		this.setVmName(System.getProperty("java.runtime.name"));//        返回 Java 虚拟机实现名称。 
        this.setVmVendor(runtime.getSpecVendor());//        返回 Java 虚拟机实现供应商。 
        this.setVmVersion(System.getProperty("java.runtime.version"));//        返回 Java 虚拟机实现版本。 
        this.setVmStartTime(new Date(runtime.getStartTime()));
        this.setSystemProperties(runtime.getSystemProperties());
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public int getAvailableProcessors() {
		return availableProcessors;
	}

	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getVmVendor() {
		return vmVendor;
	}

	public void setVmVendor(String vmVendor) {
		this.vmVendor = vmVendor;
	}

	public String getVmVersion() {
		return vmVersion;
	}

	public void setVmVersion(String vmVersion) {
		this.vmVersion = vmVersion;
	}

	public Date getVmStartTime() {
		return vmStartTime;
	}

	public void setVmStartTime(Date vmStartTime) {
		this.vmStartTime = vmStartTime;
	}

	public Map<String,String> getSystemProperties() {
		return systemProperties;
	}

	public void setSystemProperties(Map<String,String> systemProperties) {
		this.systemProperties = systemProperties;
	}
	
	
}
