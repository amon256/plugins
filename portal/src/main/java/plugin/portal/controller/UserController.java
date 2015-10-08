/**
 * UserController.java.java
 * @author FengMy
 * @since 2015年7月6日
 */
package plugin.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import plugin.portal.annotations.TokenCreate;
import plugin.portal.annotations.TokenValidate;
import plugin.portal.context.WebContext;
import plugin.portal.entity.User;
import plugin.portal.service.UserService;
import plugin.portal.utils.CollectionUtils;
import plugin.portal.utils.Pagination;
import plugin.portal.utils.SecurityUtil;

/**  
 * 功能描述：用户controller
 * 
 * @author FengMy
 * @since 2015年7月6日
 */
@Controller
@RequestMapping(value="user/*")
public class UserController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="list")
	public String list(Pagination pagination,final User user,ModelMap model){
		Criteria criteria = Criteria.where("_id").exists(true);
		if(!StringUtils.isEmpty(user.getName())){
			criteria.and("name").is(user.getName());
		}else if(!StringUtils.isEmpty(user.getAccount())){
			criteria.and("account").is(user.getAccount());
		}else if(!StringUtils.isEmpty(user.getMobile())){
			criteria.and("mobile").is(user.getMobile());
		}
		Query query = Query.query(criteria);
		query.with(new Sort(Direction.DESC, "createTime"));
		pagination.setRecordCount((int) userService.count(query));
		query.skip((pagination.getCurrentPage() - 1) * pagination.getPageSize()).limit(pagination.getPageSize());
		List<User> historys = userService.findList(query);
		model.put("datas", historys);
		model.put("user", user);
		model.put("pagination", pagination);
		return "user/list";
	}
	
	@RequestMapping(value="userinfo")
	public String userInfo(@RequestParam(value="id",required=true)String id,ModelMap model){
		User user = userService.findById(id);
		model.put("user", user);
		return "user/userInfo";
	}
	
	@RequestMapping(value="useredit")
	public String userEdit(ModelMap model){
		User user = WebContext.getLoginUser();
		model.put("user", user);
		return "user/userEdit";
	}
	
	@RequestMapping(value="userSave")
	public String userSave(User user,ModelMap model){
		User existsUser = WebContext.getLoginUser();
		if(existsUser != null){
			existsUser.setMobile(user.getMobile());
			existsUser.setNickName(user.getNickName());
			existsUser.setHeadPhoto(user.getHeadPhoto());
			userService.update(existsUser, CollectionUtils.createSet(String.class, "mobile","nickName","headPhoto"));
			model.put("user", existsUser);
			model.put("succ", "信息修改成功.");
		}
		return "user/userEdit";
	}
	
	@RequestMapping(value="password")
	public String password(ModelMap model){
		User user = WebContext.getLoginUser();
		model.put("user", user);
		return "user/password";
	}
	
	@RequestMapping(value="changepwd")
	public String changepwd(String pwd,String npwd,String npwd1,ModelMap model){
		if(StringUtils.isEmpty(pwd) || StringUtils.isEmpty(npwd) || StringUtils.isEmpty(npwd1)){
			model.put("msg", "密码不能为空");
		}else if(!npwd.equals(npwd1)){
			model.put("msg", "新密码两次输入不一致");
		}else{
			User existsUser = WebContext.getLoginUser();
			existsUser = userService.findById(existsUser.getId());
			if(existsUser != null){
				if(existsUser.getPassword().equals(SecurityUtil.encryptSHA(pwd))){
					existsUser.setPassword(SecurityUtil.encryptSHA(npwd));
					userService.update(existsUser, CollectionUtils.createSet(String.class, "password"));
					model.put("succ", "修改密码成功,下次请用新密码登录.");
				}else{
					model.put("msg", "旧密码错误");
				}
			}
		}
		User user = WebContext.getLoginUser();
		model.put("user", user);
		return "user/password";
	}
	
	@RequestMapping(value="toregister")
	@TokenCreate
	public String toRegister(String parentAccount,String dir,ModelMap model){
		return "user/register";
	}
	
	@RequestMapping(value="register")
	@TokenValidate
	@TokenCreate
	public String register(User user,ModelMap model){
		User existsUser = WebContext.getLoginUser();
		existsUser = userService.findById(existsUser.getId());
		boolean validateFlag = true;
		String validateMsg = null;
		if(StringUtils.isEmpty(user.getAccount())){
			validateMsg = "请填写账号";
			validateFlag = false;
		}else{
			boolean exists = userService.checkExists(Criteria.where("account").is(user.getAccount()));
			if(exists){
				validateMsg = "该账号己存在";
				validateFlag = false;
			}
		}		
		if(StringUtils.isEmpty(user.getPassword())){
			validateMsg = "请填写密码";
			validateFlag = false;
		}
		if(StringUtils.isEmpty(user.getName())){
			validateMsg = "请填写姓名";
			validateFlag = false;
		}
		if(StringUtils.isEmpty(user.getNickName())){
			validateMsg = "请填写昵称";
			validateFlag = false;
		}
		if(validateFlag){
			user.setPassword(SecurityUtil.encryptSHA(user.getPassword()));
			user.setRoles("M0002");
			userService.add(user);
			model.put("succ", "注册成功,新用户账号:" + user.getAccount());
		}else{
			model.put("user", user);
			model.put("msg", validateMsg);
			return "user/register";
		}
		return "user/userInfo";
	}
}
