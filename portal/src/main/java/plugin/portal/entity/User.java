/**
 * User.java.java
 * @author FengMy
 * @since 2015年7月1日
 */
package plugin.portal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;


/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年7月1日
 */
@Entity(name="USER")
public class User extends DataEntity implements Roleable {
	private static final long serialVersionUID = -9147696470362067547L;

	/**
	 * 账号
	 */
	@Column(name="ACCOUNT",nullable=false,length=40,unique=true)
	private String account;
	
	/**
	 * 密码
	 */
	@Column(name="PASSWORD",length=40)
	private String password;
	
	/**
	 * 姓名
	 */
	@Column(name="NAME",length=40)
	private String name;
	
	/**
	 * 昵称
	 */
	@Column(name="NICKNAME",length=40)
	private String nickName;
	
	/**
	 * 手机
	 */
	@Column(name="MOBILE",length=20)
	private String mobile;
	
	/**
	 * 头像
	 */
	@Column(name="HEADPHOTO",length=40)
	private String headPhoto;
	
	/**
	 * 最后一次登录时间
	 */
	@Column(name="LASTLOGINTIME")
	private Date lastLoginTime;
	
	/**
	 * 角色集合
	 */
	private String roles;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getHeadPhoto() {
		return headPhoto;
	}

	public void setHeadPhoto(String headPhoto) {
		this.headPhoto = headPhoto;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
	
	@Override
	public String getRoles() {
		return roles;
	}
}
