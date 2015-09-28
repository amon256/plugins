/**
 * DataEntity.java.java
 * @author FengMy
 * @since 2015年7月1日
 */
package plugin.portal.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年7月1日
 */
@MappedSuperclass
public abstract class DataEntity extends CoreEntity {
	private static final long serialVersionUID = 3377057353374426809L;

	@Column(name="CREATETIME",updatable=false)
	private Date createTime;
	
	@Column(name="LASTUPDATETIME")
	private Date lastUpdateTime;

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
