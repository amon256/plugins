/**
 * ApplicationServiceImpl.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import plugins.upgradekit.dao.ApplicationDao;
import plugins.upgradekit.entitys.Application;
import plugins.upgradekit.service.ApplicationService;
import plugins.upgradekit.service.DataService;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
@Component
public class ApplicationServiceImpl extends DataService<Application> implements ApplicationService{

	@Autowired
	private ApplicationDao applicationDao;
	
	@Override
	public List<Application> findPagination(Pagination<Application> pagination, Application param) {
		return applicationDao.findPagination(pagination, param);
	}
}
