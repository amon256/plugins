/**
 * CreateQueryHandler.java.java
 * @author FengMy
 * @since 2015年11月23日
 */
package plugins.utils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月23日
 */
public interface CreateQueryHandler<T> {

	public CriteriaQuery<T> create(CriteriaBuilder cb);
	
}
