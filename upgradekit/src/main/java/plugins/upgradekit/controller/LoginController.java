/**
 * LoginController.java.java
 * @author FengMy
 * @since 2015年6月30日
 */
package plugins.upgradekit.controller;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.code.kaptcha.Constants;

import plugins.upgradekit.context.WebContext;
import plugins.upgradekit.entitys.AdminUser;
import plugins.upgradekit.service.AdminUserService;
import plugins.utils.CollectionUtils;
import plugins.utils.SecurityUtil;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年6月30日
 */
@Controller
public class LoginController extends BaseController {
	
	private static final String USER_NAME_COOKIE_KEY = "admin_accounts";
	private static final int ACCOUNT_COOKIE_AGE = 60*60*24*180;
	private static final int MAX_ACCOUNTS = 10;
	
	@Autowired
	private AdminUserService userService;

	@RequestMapping(value="login")
	public String login(AdminUser user,String kaptcha,ModelMap model,HttpServletRequest request,HttpServletResponse response){
		if(WebContext.hasLogin()){
			return "redirect:index";
		}
		if(!StringUtils.isEmpty(user.getAccount())){
			if(loginProcess(user,kaptcha,request, model)){
				loginCookie(user, request, response);
				return "redirect:index";
			}
		}
		model.put("user", user);
		loginReadCookie(user,model, request, response);
		return "login";
	}
	
	@RequestMapping(value="logout")
	public String logout(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		WebContext.logout();
		AdminUser user = new AdminUser();
		model.put("user", user);
		loginReadCookie(user, model, request, response);
		return "login";
	}
	
	private void loginReadCookie(AdminUser user,ModelMap model,HttpServletRequest request,HttpServletResponse response){
		Cookie cookie = readUserNameCookie(request);
		if(cookie != null){
			String names = cookie.getValue();
			Set<String> userNames = new LinkedHashSet<String>();
			if(names != null){
				String[] nameAttr = names.split(",");
				for(String name : nameAttr){
					if(name != null && !"".equals(name.trim())){
						userNames.add(name.trim());
					}
				}
				if(userNames.size() == 1){
					user.setAccount(userNames.iterator().next());
				}else if(userNames.size() > 1){
					user.setAccount(userNames.iterator().next());
					model.put("accounts", userNames);
				}
			}
		}
	}
	
	private void loginCookie(AdminUser user,HttpServletRequest request,HttpServletResponse response){
		Cookie cookie = readUserNameCookie(request);
		if(cookie == null){
			cookie = new Cookie(USER_NAME_COOKIE_KEY, user.getAccount());
		}
		Set<String> userNames = new LinkedHashSet<String>();
		userNames.add(user.getAccount());
		String names = cookie.getValue();
		if(names != null){
			String[] nameAttr = names.split(",");
			for(String name : nameAttr){
				if(name != null && !"".equals(name.trim())){
					userNames.add(name.trim());
				}
				if(userNames.size() >= MAX_ACCOUNTS){
					break;
				}
			}
		}
		String nameStr = "";
		for(String name : userNames){
			if(!"".equals(nameStr)){
				nameStr += ",";
			}
			nameStr += name;
		}
		cookie.setValue(nameStr);
		cookie.setMaxAge(ACCOUNT_COOKIE_AGE);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	private Cookie readUserNameCookie(HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies){
			if(USER_NAME_COOKIE_KEY.equals(cookie.getName())){
				return cookie;
			}
		}
		return null;
	}
	
	/**
	 * 登录过程
	 * @param kaptcha 
	 * @param account
	 * @param pwd
	 * @param model 
	 * @return
	 */
	private boolean loginProcess(AdminUser user, String kaptcha,HttpServletRequest request, ModelMap model){
		if(StringUtils.isEmpty(user.getPassword())){
			model.put("msg", "密码不能为空");
			return false;
		}
		if(StringUtils.isEmpty(kaptcha)){
			model.put("msg", "验证码不能为空");
			return false;
		}else{
			String sessionKaptcha = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
			if(!kaptcha.trim().equalsIgnoreCase(sessionKaptcha)){
				model.put("msg", "验证码不正确");
				return false;
			}
		}
		AdminUser validateUser = userService.findByAccount(user.getAccount());
		if(validateUser != null && SecurityUtil.encryptSHA(user.getPassword()).equals(validateUser.getPassword())){
			WebContext.login(validateUser);
			validateUser.setLastLoginTime(new Date());
			userService.updateEntity(validateUser, CollectionUtils.createSet(String.class, "lastLoginTime"));
			return true;
		}else{
			model.put("msg", "用户名或密码错误");
			return false;
		}
	}
}
