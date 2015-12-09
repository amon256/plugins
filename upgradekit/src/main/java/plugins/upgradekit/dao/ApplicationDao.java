/**
 * ApplicationDao.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.dao;

import java.util.List;

import plugins.upgradekit.entitys.Application;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
public interface ApplicationDao {
	/**
	 * 分页查找
	 * @param pagination
	 * @param param
	 * @return
	 */
	public List<Application> findPagination(Pagination<Application> pagination,Application param);
}
