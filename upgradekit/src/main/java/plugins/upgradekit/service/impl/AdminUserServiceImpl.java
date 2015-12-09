/**
 * AdminUserServiceImpl.java.java
 * @author FengMy
 * @since 2015年8月18日
 */
package plugins.upgradekit.service.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import plugins.upgradekit.dao.AdminUserDao;
import plugins.upgradekit.entitys.AdminUser;
import plugins.upgradekit.enums.AdminUserStatusEnum;
import plugins.upgradekit.service.AdminUserService;
import plugins.upgradekit.service.DataService;
import plugins.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年8月18日
 */
@Component
public class AdminUserServiceImpl extends DataService<AdminUser> implements AdminUserService {
	
	@Autowired
	private AdminUserDao adminUserDao;

	@Override
	public AdminUser addEntity(AdminUser entity) {
		if(entity.getStatus() == null){
			entity.setStatus(AdminUserStatusEnum.INIT);
		}
		return super.addEntity(entity);
	}

	@Override
	public AdminUser findByAccount(String account) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminUser> query = cb.createQuery(AdminUser.class);
		Root<AdminUser> root = query.from(AdminUser.class);
		Predicate condition = cb.equal(root.get("account"),account);
		query.where(condition);
		List<AdminUser> users = entityManager.createQuery(query).getResultList();
		return (users == null || users.isEmpty()) ? null : users.get(0);
	}

	@Override
	public List<AdminUser> findPagination(Pagination<AdminUser> pagination, AdminUser param) {
		return adminUserDao.findPagination(pagination, param);
	}
}
