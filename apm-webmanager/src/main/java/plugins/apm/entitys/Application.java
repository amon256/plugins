/**
 * 
 */
package plugins.apm.entitys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import plugins.apm.tools.UpgradeApplication;


/**  
 * 功能描述：应用
 * 
 * @author FengMy
 * @since 2015年12月3日
 */
@Entity
@Table(name="application")
public class Application extends DataEntity implements UpgradeApplication{
	private static final long serialVersionUID = -4385672841148539032L;

	/**
	 * 应用名称
	 */
	@Column(length=40)
	private String name;
	
	/**
	 * 应用编码
	 */
	@Column(length=40)
	private String number;
	
	/**
	 * 应用描述
	 */
	@Column(length=40)
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
