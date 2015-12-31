/**
 * SystemInitContext.java.java
 * @author FengMy
 * @since 2015年11月23日
 */
package plugins.apm.context;

import java.util.List;

import plugins.apm.entitys.AdminUser;
import plugins.apm.enums.AdminUserStatusEnum;
import plugins.apm.permission.PermissionManager;
import plugins.apm.permission.SystemMenu;
import plugins.apm.service.AdminUserService;
import plugins.spring.ApplicationContextAware;
import plugins.utils.SecurityUtil;

/**  
 * 功能描述：检查是否有默认用户admin,如果不存在，则生成admin/admin，并且授予所有权限
 * 
 * @author FengMy
 * @since 2015年11月23日
 */
public class SystemInitContext {
	
	public static final String DEFAULT_HEAD_PHOTO = "/adminLTE/img/avatar5.png";
	
	private static final String DEFAULT_ADMINUSER_ID = "000000000000000000000000001";

	public void systemInit(){
		createDefaultAdminUser();
	}
	
	
	private void createDefaultAdminUser(){
		AdminUserService service = ApplicationContextAware.getApplicationContext().getBean(AdminUserService.class);
		AdminUser user = service.findEntity(DEFAULT_ADMINUSER_ID);
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
			service.insert(user);
		}
	}
}
