/**
 * UserFacade.java.java
 * @author FengMy
 * @since 2015年10月22日
 */
package plugin.portal.webservice;

import java.util.List;

import javax.jws.WebService;

import plugin.portal.entity.User;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年10月22日
 */
@WebService
public interface UserFacade {

	public List<User> findUsers();
	
	public User findUserByAccount(String account);
}
