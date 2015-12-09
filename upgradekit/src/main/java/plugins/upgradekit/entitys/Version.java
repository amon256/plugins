/**
 * Version.java.java
 * @author FengMy
 * @since 2015年12月3日
 */
package plugins.upgradekit.entitys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import plugins.upgradekit.enums.UpgradeStatusEnum;

/**  
 * 功能描述：应用版本
 * 
 * @author FengMy
 * @since 2015年12月3日
 */
@Entity
@Table(name="version")
public class Version extends DataEntity {
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
	 * 升级参数文件
	 */
	@Column(length=80)
	private String parameterFile;
	
	/**
	 * 升级参数文件原文件名
	 */
	@Column(length=80)
	private String parameterFileName;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="applicationId")
	private Application application;
	
	/**
	 * 升级状态
	 */
	@Column(length=20)
	@Enumerated(EnumType.STRING)
	private UpgradeStatusEnum status;

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

	public String getParameterFile() {
		return parameterFile;
	}

	public void setParameterFile(String parameterFile) {
		this.parameterFile = parameterFile;
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

	public String getParameterFileName() {
		return parameterFileName;
	}

	public void setParameterFileName(String parameterFileName) {
		this.parameterFileName = parameterFileName;
	}
}
