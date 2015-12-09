/**
 * SystemInitContext.java.java
 * @author FengMy
 * @since 2015年11月23日
 */
package plugins.upgradekit.context;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.ibatis.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import plugins.permission.PermissionManager;
import plugins.permission.SystemMenu;
import plugins.spring.ApplicationContextAware;
import plugins.upgradekit.entitys.AdminUser;
import plugins.upgradekit.enums.AdminUserStatusEnum;
import plugins.upgradekit.service.AdminUserService;
import plugins.utils.SecurityUtil;

/**  
 * 功能描述：检查是否有默认用户admin,如果不存在，则生成admin/admin，并且授予所有权限
 * 
 * @author FengMy
 * @since 2015年11月23日
 */
@Component
@Lazy(value=false)
public class SystemInitContext {
	
	public static final String DEFAULT_HEAD_PHOTO = "/adminLTE/img/avatar5.png";
	
	private static final String DEFAULT_ADMINUSER_ID = "000000000000000000000000001";

	@PostConstruct
	public void systemInit(){
		myBatisLogUsing();
		createDefaultAdminUser();
	}
	
	
	private void createDefaultAdminUser(){
		AdminUserService service = ApplicationContextAware.getApplicationContext().getBean(AdminUserService.class);
		AdminUser user = service.getEntityById(DEFAULT_ADMINUSER_ID);
		if(user == null){
			user = new AdminUser();
			user.setId(DEFAULT_ADMINUSER_ID);
			user.setAccount("admin");
			user.setName("管理员");
			user.setStatus(AdminUserStatusEnum.EFFECT);
			user.setPassword(SecurityUtil.encryptSHA("admin"));
			PermissionManager pm = ApplicationContextAware.getApplicationContext().getBean(PermissionManager.class);
			List<SystemMenu> menus = pm.getMenus();
			StringBuilder roles = new StringBuilder();
			if(menus != null){
				for(SystemMenu menu : menus){
					roles.append(menu.getId()).append(",");
				}
			}
			user.setRoles(roles.toString());
			service.addEntity(user);
		}
	}
	
	private void myBatisLogUsing(){
		//mybatis 使用slf4j日志控件
		LogFactory.useSlf4jLogging();
	}
}
