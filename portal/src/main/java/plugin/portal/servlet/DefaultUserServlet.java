/**
 * DefaultUserServlet.java.java
 * @author FengMy
 * @since 2015年9月25日
 */
package plugin.portal.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import plugin.portal.entity.User;
import plugin.portal.service.UserService;
import plugin.portal.spring.ApplicationContextAware;
import plugin.portal.utils.SecurityUtil;

/**  
 * 功能描述：检验是否存在系统管理员,如果不存在则新增一个默认的
 * 
 * @author FengMy
 * @since 2015年9月25日
 */
public class DefaultUserServlet extends HttpServlet {
	private static final long serialVersionUID = -2511685005680360579L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		UserService userService = ApplicationContextAware.getApplicationContext().getBean(UserService.class);
		User user = userService.findById("0");
		if(user == null){
			user = new User();
			user.setAccount("admin");
			user.setPassword(SecurityUtil.encryptSHA("admin"));
			user.setName("超级管理员");
			user.setNickName("超级管理员");
			user.setId("0");
			user.setRoles("M0000,M0001,M0002");
			user.setHeadPhoto("/adminLTE/img/avatar.png");
			userService.add(user);
		}
	}
}
