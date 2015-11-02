/**
 * User.java.java
 * @author FengMy
 * @since 2015年7月1日
 */
package plugin.portal.entity;

import java.util.Date;


/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年7月1日
 */
public class User extends DataEntity implements Roleable {
	private static final long serialVersionUID = -9147696470362067547L;

	/**
	 * 账号
	 */
	private String account;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 姓名
	 */
	private String name;
	
	/**
	 * 昵称
	 */
	private String nickName;
	
	/**
	 * 手机
	 */
	private String mobile;
	
	/**
	 * 头像
	 */
	private String headPhoto;
	
	/**
	 * 最后一次登录时间
	 */
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
