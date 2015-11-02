/**
 * UserFacadeImpl.java.java
 * @author FengMy
 * @since 2015年10月22日
 */
package plugin.portal.webservice;

import java.util.List;

import javax.jws.WebService;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import plugin.portal.entity.User;
import plugin.portal.service.UserService;
import plugin.portal.spring.ApplicationContextAware;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年10月22日
 */
@WebService
public class UserFacadeImpl implements UserFacade {

	@Override
	public List<User> findUsers() {
		UserService userService = ApplicationContextAware.getApplicationContext().getBean(UserService.class);
		return userService.findAll();
	}

	@Override
	public User findUserByAccount(String account) {
		UserService userService = ApplicationContextAware.getApplicationContext().getBean(UserService.class);
		return userService.findOne(Query.query(Criteria.where("account").is(account)));
	}

}
