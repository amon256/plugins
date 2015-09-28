/**
 * UserService.java.java
 * @author FengMy
 * @since 2015年9月25日
 */
package plugin.portal.service;

import org.springframework.transaction.annotation.Transactional;

import plugin.portal.entity.User;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年9月25日
 */
@Transactional
public interface UserService extends CoreService<User>{

}
