/**
 * ApplicationService.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.service;

import java.util.List;

import javax.transaction.Transactional;

import plugins.upgradekit.entitys.Application;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
@Transactional
public interface ApplicationService extends BaseService<Application> {
	/**
	 * 分页查找
	 * @param pagination
	 * @param param
	 * @return
	 */
	public List<Application> findPagination(Pagination<Application> pagination,Application param);
}
