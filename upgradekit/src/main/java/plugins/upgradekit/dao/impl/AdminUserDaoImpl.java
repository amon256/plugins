/**
 * AdminUserDao.java.java
 * @author FengMy
 * @since 2015年11月24日
 */
package plugins.upgradekit.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import plugins.upgradekit.dao.AdminUserDao;
import plugins.upgradekit.entitys.AdminUser;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月24日
 */
@Component
public class AdminUserDaoImpl extends DaoSupport implements AdminUserDao {
	
	private static final String NAME_SPACE  = "plugins.upgradekit.entitys.AdminUser";

	@Override
	public List<AdminUser> findPagination(Pagination<AdminUser> pagination, AdminUser param) {
		return pagination(pagination, NAME_SPACE + ".select", param);
	}

}
