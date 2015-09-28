/**
 * UserController.java.java
 * @author FengMy
 * @since 2015年7月6日
 */
package plugin.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
			existsUser = userService.findById(existsUser.getId());
			if(existsUser != null){
				existsUser.setMobile(user.getMobile());
				existsUser.setNickName(user.getNickName());
				existsUser.setHeadPhoto(user.getHeadPhoto());
				userService.update(existsUser);
			}
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
					userService.update(existsUser);
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
	public String register(User user,String parentAccount,String dir,ModelMap model){
		User existsUser = WebContext.getLoginUser();
		existsUser = userService.findById(existsUser.getId());
		boolean validateFlag = true;
		String validateMsg = null;
		if(!("left".equals(dir) || "right".equals(dir))){
			validateMsg = "非法数据";
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
		return "user/userInfo";
	}
}
