/**
 * AdminUserService.java.java
 * @author FengMy
 * @since 2015年8月18日
 */
package plugins.apm.service;

import javax.transaction.Transactional;

import plugins.apm.entitys.AdminUser;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年8月18日
 */
@Transactional
public interface AdminUserService extends IService<AdminUser> {
	
	/**
	 * 根据账号查找
	 * @param account
	 * @return
	 */
	public AdminUser findByAccount(String account);
	
}
