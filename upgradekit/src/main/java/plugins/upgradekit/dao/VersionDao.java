/**
 * VersionDao.java.java
 * @author FengMy
 * @since 2015年12月10日
 */
package plugins.upgradekit.dao;

import java.util.List;

import plugins.upgradekit.entitys.Version;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月10日
 */
public interface VersionDao {
	/**
	 * 分页查找
	 * @param pagination
	 * @param param
	 * @return
	 */
	public List<Version> findPagination(Pagination<Version> pagination,Version param);
}
