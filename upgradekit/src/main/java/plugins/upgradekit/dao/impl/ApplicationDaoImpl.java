/**
 * ApplicationDaoImpl.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import plugins.upgradekit.dao.ApplicationDao;
import plugins.upgradekit.entitys.Application;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
@Component
public class ApplicationDaoImpl extends DaoSupport implements ApplicationDao {
	private static final String NAME_SPACE  = "plugins.upgradekit.entitys.Application";
	@Override
	public List<Application> findPagination(Pagination<Application> pagination, Application param) {
		return pagination(pagination, NAME_SPACE + ".select", param);
	}

}
