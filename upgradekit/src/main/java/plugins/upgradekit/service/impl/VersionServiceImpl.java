/**
 * VersionServiceImpl.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import plugins.upgradekit.dao.VersionDao;
import plugins.upgradekit.entitys.Version;
import plugins.upgradekit.service.DataService;
import plugins.upgradekit.service.VersionService;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
@Component
public class VersionServiceImpl extends DataService<Version> implements VersionService {

	@Autowired
	private VersionDao versionDao;
	
	@Override
	public List<Version> findPagination(Pagination<Version> pagination, Version param) {
		return versionDao.findPagination(pagination, param);
	}

}
