/**
 * ApplicationUser.java.java
 * @author FengMy
 * @since 2015年9月28日
 */
package plugin.portal.entity;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年9月28日
 */
@Document(collection="applicationusers")
public class ApplicationUser extends DataEntity {
	
	private static final long serialVersionUID = 8690457806211765808L;

	@DBRef
	private Application application;
	
	@DBRef
	private User user;
	
	/**
	 * 子账号
	 */
	private String subAccount;
	
	@Transient
	private boolean linked = true;

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSubAccount() {
		return subAccount;
	}

	public void setSubAccount(String subAccount) {
		this.subAccount = subAccount;
	}

	public boolean isLinked() {
		return linked;
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}
}
