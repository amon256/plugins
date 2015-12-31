/**
 * AdminUserServiceImpl.java.java
 * @author FengMy
 * @since 2015年8月18日
 */
package plugins.apm.service.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import plugins.apm.entitys.AdminUser;
import plugins.apm.service.AdminUserService;
import plugins.apm.service.ServiceSupport;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年8月18日
 */
@Component
public class AdminUserServiceImpl extends ServiceSupport<AdminUser> implements AdminUserService {

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
}
