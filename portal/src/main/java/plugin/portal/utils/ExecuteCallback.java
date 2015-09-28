/**
 * ExecuteCallback.java.java
 * @author FengMy
 * @since 2015年9月25日
 */
package plugin.portal.utils;

import javax.persistence.criteria.CriteriaQuery;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年9月25日
 */
public interface ExecuteCallback<T> {
	
	public void callback(CriteriaQuery<T> query);
	
}
