/**
 * DataEntity.java.java
 * @author FengMy
 * @since 2015年7月1日
 */
package plugins.upgradekit.entitys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年7月1日
 */
@MappedSuperclass
public class DataEntity extends CoreEntity {
	private static final long serialVersionUID = 3377057353374426809L;

	@Column
	private Date createTime;
	
	@Column
	private Date lastUpdateTime;
	
	@Transient
	private String keyword;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}
