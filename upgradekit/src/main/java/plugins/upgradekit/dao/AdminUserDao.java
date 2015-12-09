/**
 * AdminUserDao.java.java
 * @author FengMy
 * @since 2015年11月24日
 */
package plugins.upgradekit.dao;

import java.util.List;

import plugins.upgradekit.entitys.AdminUser;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月24日
 */
public interface AdminUserDao {
	
	/**
	 * 分页查找
	 * @param pagination
	 * @param param
	 * @return
	 */
	public List<AdminUser> findPagination(Pagination<AdminUser> pagination,AdminUser param);
}
