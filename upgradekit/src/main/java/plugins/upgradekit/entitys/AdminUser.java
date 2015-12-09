/**
 * 
 */
package plugins.upgradekit.entitys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import plugins.upgradekit.enums.AdminUserStatusEnum;


/**  
 * 功能描述：管理员用户
 * 
 * @author FengMy
 * @since 2015年8月18日
 */
@Entity
@Table(name="adminuser")
public class AdminUser extends DataEntity implements Roleable{
	private static final long serialVersionUID = -3038906912633866697L;

	/**
	 * 姓名
	 */
	@Column(length=40)
	private String name;
	
	/**
	 * 账号
	 */
	@Column(length=40)
	private String account;
	
	/**
	 * 密码
	 */
	@Column(length=100)
	private String password;
	
	/**
	 * 电话
	 */
	@Column(length=40)
	private String mobile;
	
	/**
	 * 描述
	 */
	@Column(length=400)
	private String description;
	
	/**
	 * 管理员状态
	 */
	@Column(length=10,columnDefinition="VARCHAR")
	@Enumerated(EnumType.STRING)
	private AdminUserStatusEnum status;
	
	/**
	 * 上次登录时间
	 */
	@Column
	private Date lastLoginTime;
	
	/**
	 * 角色
	 */
	@Column(length=800)
	private String roles;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public AdminUserStatusEnum getStatus() {
		return status;
	}

	public void setStatus(AdminUserStatusEnum status) {
		this.status = status;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
}
