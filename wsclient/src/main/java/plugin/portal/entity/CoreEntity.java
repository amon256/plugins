/**
 * CoreEntity.java.java
 * @author FengMy
 * @since 2015年7月1日
 */
package plugin.portal.entity;

import java.io.Serializable;

/**  
 * 功能描述：实体基类
 * 
 * @author FengMy
 * @since 2015年7月1日
 */
public abstract class CoreEntity implements Serializable{
	private static final long serialVersionUID = 925904316481738175L;
	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
