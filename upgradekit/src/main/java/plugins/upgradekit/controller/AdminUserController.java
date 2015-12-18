/**
 * AdminUserController.java.java
 * @author FengMy
 * @since 2015年8月18日
 */
package plugins.upgradekit.controller;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import plugins.permission.PermissionManager;
import plugins.permission.SystemMenu;
import plugins.upgradekit.context.WebContext;
import plugins.upgradekit.entitys.AdminUser;
import plugins.upgradekit.enums.AdminUserStatusEnum;
import plugins.upgradekit.service.AdminUserService;
import plugins.utils.CollectionUtils;
import plugins.utils.CreateQueryHandler;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;
import plugins.utils.SecurityUtil;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年8月18日
 */
@Controller
@RequestMapping(value="adminuser/*")
public class AdminUserController extends BaseController{
	
	@Autowired
	private AdminUserService adminUserService;
	
	@Autowired
	private PermissionManager permissionManager;

	@RequestMapping(value="list")
	public String list(ModelMap model){
		return "adminuser/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<AdminUser> listData(Pagination<AdminUser> pagination,AdminUser adminUser,ModelMap model){
		adminUserService.findPagination(pagination, adminUser);
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String toAdd(){
		return "adminuser/add";
	}
	
	@RequestMapping(value="add")
	@ResponseBody
	public ResponseObject add(final AdminUser adminUser){
		ResponseObject responseObject = ResponseObject.newInstance().fail();
		if(validate(adminUser, responseObject)){
			CreateQueryHandler<Long> handler = new CreateQueryHandler<Long>() {
				@Override
				public CriteriaQuery<Long> create(CriteriaBuilder cb) {
					CriteriaQuery<Long> query = cb.createQuery(Long.class);
					Root<AdminUser> root = query.from(AdminUser.class);
					query.where(cb.equal(root.get("account"), adminUser.getAccount()));
					return query;
				}
			};
			if(adminUserService.countByQuery(handler) > 0){
				responseObject.setMsg("该账号己存在");
			}else{
				adminUserService.addEntity(adminUser);
				responseObject.setMsg("新增成功");
				responseObject.success();
			}
		}
		return responseObject;
	}
	
	private boolean validate(AdminUser adminUser,ResponseObject responseObject){
		if(StringUtils.isEmpty(adminUser.getName())){
			responseObject.setMsg("姓名不能为空");
			return false;
		}else if(StringUtils.isEmpty(adminUser.getAccount())){
			responseObject.setMsg("账号不能为空");
			return false;
		}
		return true;
	}
	
	@RequestMapping(value="toUpdate")
	public String toUpdate(@RequestParam(value="id",required=true)String id,ModelMap model){
		AdminUser adminUser = adminUserService.getEntityById(id);
		model.put("user", adminUser);
		List<SystemMenu> menus = permissionManager.getMenus();
		model.put("menus", menus);
		return "adminuser/update";
	}
	
	@RequestMapping(value="updateData")
	@ResponseBody
	public ResponseObject updateData(@RequestParam(value="id",required=true)String id){
		ResponseObject responseObject = ResponseObject.newInstance().success();
		AdminUser adminUser = adminUserService.getEntityById(id);
		responseObject.put("user", adminUser);
		return responseObject;
	}
	
	@RequestMapping(value="update")
	@ResponseBody
	public ResponseObject update(AdminUser adminUser){
		ResponseObject responseObject = ResponseObject.newInstance().fail();
		if(StringUtils.isEmpty(adminUser.getId())){
			responseObject.setMsg("关键字段空缺");
		}else if(validate(adminUser, responseObject)){
			adminUserService.updateEntity(adminUser, CollectionUtils.createSet(String.class, "name","mobile","roles"));
			responseObject.setMsg("更新成功");
			responseObject.success();
		}
		return responseObject;
	}
	
	@RequestMapping(value="toEffect")
	public String toEffect(@RequestParam(value="id",required=true)String id,ModelMap model){
		AdminUser adminUser = adminUserService.getEntityById(id);
		model.put("user", adminUser);
		return "adminuser/effect";
	}
	
	@RequestMapping(value="effect")
	@ResponseBody
	public ResponseObject effect(AdminUser adminUser,String repassword){
		ResponseObject responseObject = ResponseObject.newInstance().fail();
		if(StringUtils.isEmpty(adminUser.getId())){
			responseObject.setMsg("关键字段空缺");
		}else if(StringUtils.isEmpty(adminUser.getPassword())){
			responseObject.setMsg("激活账号必须重置密码,密码不能为空");
		}else if(StringUtils.isEmpty(repassword) || !repassword.equals(adminUser.getPassword())){
			responseObject.setMsg("两个密码不一致");
		}else{
			AdminUser old = adminUserService.getEntityById(adminUser.getId());
			if(old == null){
				responseObject.setMsg("用户不存在");
			}else{
				old.setPassword(SecurityUtil.encryptSHA(adminUser.getPassword()));
				old.setStatus(AdminUserStatusEnum.EFFECT);
				adminUserService.updateEntity(old, CollectionUtils.createSet(String.class, "password","status"));
				responseObject.setMsg("激活账号成功");
				responseObject.success();
			}
		}
		return responseObject;
	}
	
	@RequestMapping(value="disabled")
	@ResponseBody
	public ResponseObject disabled(@RequestParam(value="id",required=true)String id){
		ResponseObject responseObject = ResponseObject.newInstance().fail();
		AdminUser adminUser = adminUserService.getEntityById(id);
		if(adminUser == null){
			responseObject.setMsg("该用户己不存在");
		}else{
			adminUser.setStatus(AdminUserStatusEnum.DISABLED);
			adminUserService.updateEntity(adminUser, CollectionUtils.createSet(String.class, "status"));
			responseObject.setMsg("己成功禁用");
			responseObject.success();
		}
		return responseObject;
	}
	
	@RequestMapping(value="toProfile")
	public String toProfile(ModelMap model){
		AdminUser user = WebContext.getLoginUser();
		model.put("user", user);
		return "adminuser/profile";
	}
	
	@RequestMapping(value="profile")
	@ResponseBody
	public ResponseObject profile(AdminUser adminUser){
		ResponseObject rb = ResponseObject.newInstance().fail();
		AdminUser user = WebContext.getLoginUser();
		user.setMobile(adminUser.getMobile());
		user.setName(adminUser.getName());
		adminUserService.updateEntity(user, CollectionUtils.createSet(String.class, "mobile","name"));
		return rb.success();
	}
	
	@RequestMapping(value="toPassword")
	public String toPassword(ModelMap model){
		AdminUser user = WebContext.getLoginUser();
		model.put("user", user);
		return "adminuser/password";
	}
	
	@RequestMapping(value="password")
	@ResponseBody
	public ResponseObject password(String oldPassword,String password,String repassword){
		ResponseObject rb = ResponseObject.newInstance().fail();
		AdminUser user = WebContext.getLoginUser();
		if(!user.getPassword().equals(SecurityUtil.encryptSHA(oldPassword))){
			rb.setMsg("旧密码不正确");
		}else if(password == null || "".equals(password.trim())){
			rb.setMsg("密码不能为空");
		}else if(!password.trim().equals(repassword)){
			rb.setMsg("前后密码不一致");
		}else{
			user.setPassword(SecurityUtil.encryptSHA(password.trim()));
			adminUserService.updateEntity(user, CollectionUtils.createSet(String.class, "password"));
			rb.setMsg("修改密码成功");
			rb.success();
		}
		return rb;
	}
}
