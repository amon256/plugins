/**
 * Version.java.java
 * @author FengMy
 * @since 2015年12月3日
 */
package plugins.apm.entitys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import plugins.apm.enums.UpgradeStatusEnum;
import plugins.apm.tools.UpgradeVersion;

/**  
 * 功能描述：应用版本
 * 
 * @author FengMy
 * @since 2015年12月3日
 */
@Entity
@Table(name="version")
public class Version extends DataEntity implements UpgradeVersion{
	private static final long serialVersionUID = -4713932011978890785L;

	/**
	 * 版本号
	 */
	@Column(length=40)
	private String number;
	
	/**
	 * 版本升级文件
	 */
	@Column(length=80)
	private String file;
	
	/**
	 * 原文件名
	 */
	@Column(length=80)
	private String fileName;
	
	/**
	 * 升级过程配置文件
	 */
	@Column(length=80)
	private String configFile;
	
	/**
	 * 升级参数文件原文件名
	 */
	@Column(length=80)
	private String configFileName;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="applicationId")
	private Application application;
	
	/**
	 * 升级状态
	 */
	@Column(length=20)
	@Enumerated(EnumType.STRING)
	private UpgradeStatusEnum status;
	
	/**
	 * 升级时间
	 */
	@Column
	private Date upgradeTime;

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

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public UpgradeStatusEnum getStatus() {
		return status;
	}

	public void setStatus(UpgradeStatusEnum status) {
		this.status = status;
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

	public Date getUpgradeTime() {
		return upgradeTime;
	}

	public void setUpgradeTime(Date upgradeTime) {
		this.upgradeTime = upgradeTime;
	}
}
