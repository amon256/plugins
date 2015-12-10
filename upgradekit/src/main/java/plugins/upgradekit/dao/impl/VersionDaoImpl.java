/**
 * VersionDaoImpl.java.java
 * @author FengMy
 * @since 2015年12月10日
 */
package plugins.upgradekit.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import plugins.upgradekit.dao.VersionDao;
import plugins.upgradekit.entitys.Version;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月10日
 */
@Component
public class VersionDaoImpl extends DaoSupport implements VersionDao {

	@Override
	public List<Version> findPagination(Pagination<Version> pagination, Version param) {
		return pagination(pagination, Version.class.getName() + ".select", param);
	}

}
