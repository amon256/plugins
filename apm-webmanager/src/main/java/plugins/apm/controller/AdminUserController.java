/**
 * AdminUserController.java.java
 * @author FengMy
 * @since 2015年8月18日
 */
package plugins.apm.controller;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import plugins.apm.context.WebContext;
import plugins.apm.entitys.AdminUser;
import plugins.apm.enums.AdminUserStatusEnum;
import plugins.apm.permission.PermissionManager;
import plugins.apm.permission.SystemMenu;
import plugins.apm.service.AdminUserService;
import plugins.upgradekit.entitys.AdminUser_;
import plugins.utils.CollectionUtils;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;
import plugins.utils.SecurityUtil;
import plugins.utils.persistence.SimplePrepareQueryhandler;
import plugins.validation.LengRangeValidationRule;
import plugins.validation.RegexpValidationRule;
import plugins.validation.RequiredValidationRule;
import plugins.validation.Validation;
import plugins.validation.ValidationResult;
import plugins.validation.ValidationRule;
import plugins.validation.ValidationUtil;

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
		adminUserService.findPagination(pagination, new SimplePrepareQueryhandler<AdminUser>(adminUser){
			@Override
			protected Predicate[] getWhereCondition(CriteriaBuilder cb,AdminUser entity, Root<AdminUser> root) {
				if(StringUtils.isNotEmpty(entity.getKeyword())){
					return new Predicate[]{
							cb.or(
									cb.like(root.get(AdminUser_.name)   , "%"+entity.getKeyword()+"%"),
									cb.like(root.get(AdminUser_.account), "%"+entity.getKeyword()+"%"),
									cb.like(root.get(AdminUser_.mobile) , "%"+entity.getKeyword()+"%"))
					};
				}
				return null;
			}
		});
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
		if(validateAdd(adminUser, responseObject)){
			adminUserService.insert(adminUser);
			responseObject.setMsg("新增成功");
			responseObject.success();
		}
		return responseObject;
	}
	
	protected boolean validateAdd(AdminUser entity, ResponseObject rb) {
		//必填和格式校验
		List<ValidationResult> results = ValidationUtil.validate(entity,
				new Validation("name", "昵称", 
						new RequiredValidationRule(),
						new LengRangeValidationRule(2, 10)
				),
				new Validation("account", "账号", 
						new RequiredValidationRule(),
						new LengRangeValidationRule(4, 20),
						new RegexpValidationRule("[_@$.0-9a-zA-Z]+"),
						new ExistsUserValidationRule(entity)
				),
				new Validation("password", "密码", 
						new RequiredValidationRule(),
						new LengRangeValidationRule(4, 20)
				),
				new Validation("mobile", "手机", 
						new RegexpValidationRule("1[0-9]{10}"),
						new ExistsUserValidationRule(entity)
				),
				new Validation("email", "邮箱", 
						new RegexpValidationRule("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"),
						new ExistsUserValidationRule(entity)
				)
		);
		if(results != null && !results.isEmpty()){
			rb.put("validateErrors", results);
			rb.put("msg", "数据校验不通过");
			return false;
		}
		return true;
	}
	
	@RequestMapping(value="toUpdate")
	public String toUpdate(@RequestParam(value="id",required=true)String id,ModelMap model){
		AdminUser adminUser = adminUserService.findEntity(id);
		model.put("user", adminUser);
		List<SystemMenu> menus = permissionManager.getMenus();
		model.put("menus", menus);
		return "adminuser/update";
	}
	
	@RequestMapping(value="updateData")
	@ResponseBody
	public ResponseObject updateData(@RequestParam(value="id",required=true)String id){
		ResponseObject responseObject = ResponseObject.newInstance().success();
		AdminUser adminUser = adminUserService.findEntity(id);
		responseObject.put("user", adminUser);
		return responseObject;
	}
	
	@RequestMapping(value="update")
	@ResponseBody
	public ResponseObject update(AdminUser adminUser){
		ResponseObject responseObject = ResponseObject.newInstance().fail();
		if(StringUtils.isEmpty(adminUser.getId())){
			responseObject.setMsg("关键字段空缺");
		}else if(validateEdit(adminUser, responseObject)){
			adminUserService.update(adminUser, CollectionUtils.createSet(String.class, "name","mobile","roles"));
			responseObject.setMsg("更新成功");
			responseObject.success();
		}
		return responseObject;
	}
	
	protected boolean validateEdit(AdminUser entity, ResponseObject rb) {
		List<ValidationResult> results = ValidationUtil.validate(entity,
				new Validation("name", "昵称", 
						new RequiredValidationRule(),
						new LengRangeValidationRule(2, 10)
				),
				new Validation("mobile", "手机", 
						new RegexpValidationRule("1[0-9]{10}"),
						new ExistsUserValidationRule(entity)
				),
				new Validation("email", "邮箱", 
						new RegexpValidationRule("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"),
						new ExistsUserValidationRule(entity)
				)
		);
		if(results != null && !results.isEmpty()){
			rb.put("validateErrors", results);
			rb.put("msg", "数据校验不通过");
			return false;
		}
		return true;
	}
	
	@RequestMapping(value="toEffect")
	public String toEffect(@RequestParam(value="id",required=true)String id,ModelMap model){
		AdminUser adminUser = adminUserService.findEntity(id);
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
			AdminUser old = adminUserService.findEntity(adminUser.getId());
			if(old == null){
				responseObject.setMsg("用户不存在");
			}else{
				old.setPassword(SecurityUtil.encryptSHA(adminUser.getPassword()));
				old.setStatus(AdminUserStatusEnum.EFFECT);
				adminUserService.update(old, CollectionUtils.createSet(String.class, "password","status"));
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
		AdminUser adminUser = adminUserService.findEntity(id);
		if(adminUser == null){
			responseObject.setMsg("该用户己不存在");
		}else{
			adminUser.setStatus(AdminUserStatusEnum.DISABLED);
			adminUserService.update(adminUser, CollectionUtils.createSet(String.class, "status"));
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
		adminUserService.update(user, CollectionUtils.createSet(String.class, "mobile","name"));
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
			adminUserService.update(user, CollectionUtils.createSet(String.class, "password"));
			rb.setMsg("修改密码成功");
			rb.success();
		}
		return rb;
	}
	
	private class ExistsUserValidationRule implements ValidationRule{
		private AdminUser self;
		ExistsUserValidationRule(AdminUser self){
			this.self = self;
		}
		@Override
		public String type() {
			return "exists";
		}
		@Override
		public boolean validate(Object bean, final Object value) {
			if(value == null){
				return true;
			}
			if(value instanceof String){
				if(StringUtils.isEmpty(value.toString().trim())){
					return true;
				}
			}
			List<AdminUser> existsList = adminUserService.find(new SimplePrepareQueryhandler<AdminUser>((AdminUser) bean){
				@Override
				protected Predicate[] getWhereCondition(CriteriaBuilder cb,
						AdminUser entity, Root<AdminUser> root) {
					return new Predicate[]{cb.or(
								cb.equal(root.get(AdminUser_.account), value),
								cb.equal(root.get(AdminUser_.mobile), value),
								cb.equal(root.get(AdminUser_.email), value)
							)};
				}
			});
			if(self != null && StringUtils.isNotEmpty(self.getId())){
				if(existsList != null && !existsList.isEmpty()){
					for(AdminUser au : existsList){
						if(StringUtils.equals(self.getId(), au.getId())){
							return true;
						}
					}
				}
			}
			return existsList == null || existsList.isEmpty();
		}
		@Override
		public String getErrorMessage() {
			return "己被注册";
		}
	}
}
