/**
 * AdminUserService.java.java
 * @author FengMy
 * @since 2015年8月18日
 */
package plugins.upgradekit.service;

import java.util.List;

import javax.transaction.Transactional;

import plugins.upgradekit.entitys.AdminUser;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年8月18日
 */
@Transactional
public interface AdminUserService extends BaseService<AdminUser> {
	
	/**
	 * 根据账号查找
	 * @param account
	 * @return
	 */
	public AdminUser findByAccount(String account);
	
	/**
	 * 分页查找
	 * @param pagination
	 * @param param
	 * @return
	 */
	public List<AdminUser> findPagination(Pagination<AdminUser> pagination,AdminUser param);
}
