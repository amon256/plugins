/**
 * ApplicationController.java.java
 * @author FengMy
 * @since 2015年9月28日
 */
package plugin.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;

import plugin.portal.entity.Application;
import plugin.portal.entity.ApplicationUser;
import plugin.portal.entity.User;
import plugin.portal.service.ApplicationService;
import plugin.portal.service.ApplicationUserService;
import plugin.portal.service.UserService;
import plugin.portal.utils.CollectionUtils;
import plugin.portal.utils.Pagination;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年9月28日
 */
@Controller
@RequestMapping(value="app/*")
public class ApplicationController extends BaseController {
	
	@Autowired
	private ApplicationService appService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ApplicationUserService applicationUserService;
	
	@RequestMapping(value="list")
	public String list(Pagination pagination,Application app,ModelMap model){
		Query query = new Query();
		long count = appService.count(query);
		pagination.setRecordCount((int) count);
		if(count > 0){
			query.skip(pagination.getStartIndex());
			query.limit(pagination.getPageSize());
			query.with(new Sort(Direction.DESC, "createTime"));
			List<Application> appList = appService.findList(query);
			model.put("datas", appList);
		}
		model.put("app", app);
		model.put("pagination", pagination);
		return "app/list";
	}
	
	@RequestMapping(value="toadd")
	public String toadd(Application app,ModelMap model){
		model.put("app", app);
		return "app/add";
	}
	
	@RequestMapping(value="add")
	public String add(Application app,ModelMap model){
		if(app.getName() == null || "".equals(app.getName())){
			model.put(MSG, "应用名称不能为空");
		}else if(app.getUrl() == null || "".equals(app.getUrl())){
			model.put(MSG, "应用URL不能为空");
		}else  if(app.getTicketName() == null || "".equals(app.getTicketName())){
			model.put(MSG, "应用凭证KEY不能为空");
		}else{
			appService.add(app);
			model.put(SUCC, "保存成功");
			return list(new Pagination(), new Application(), model);
		}
		return toadd(app, model);
		
	}
	
	@RequestMapping(value="toupdate")
	public String toupdate(@RequestParam(value="id",required=true)String id,ModelMap model){
		Application app = appService.findById(id);
		model.put("app", app);
		return "app/update";
	}
	
	@RequestMapping(value="update")
	public String update(Application app,ModelMap model){
		if(app.getName() == null || "".equals(app.getName())){
			model.put(MSG, "应用名称不能为空");
		}else if(app.getUrl() == null || "".equals(app.getUrl())){
			model.put(MSG, "应用URL不能为空");
		}else  if(app.getTicketName() == null || "".equals(app.getTicketName())){
			model.put(MSG, "应用凭证KEY不能为空");
		}else{
			appService.update(app, CollectionUtils.createSet(String.class, "name","url","ticketName","description"));
			model.put(SUCC, "保存成功");
			return list(new Pagination(), new Application(), model);
		}
		return toupdate(app.getId(), model);
	}
	
	@RequestMapping(value="linkuser")
	public String linkuser(Pagination pagination,@RequestParam(value="appid",required=true)String appid,User user,ModelMap model){
		Application app = appService.findById(appid);
		
		Criteria criteria = Criteria.where("_id").exists(true);
		if(!StringUtils.isEmpty(user.getName())){
			criteria.and("name").is(user.getName());
		}else if(!StringUtils.isEmpty(user.getAccount())){
			criteria.and("account").is(user.getAccount());
		}
		Query query = Query.query(criteria);
		query.with(new Sort(Direction.DESC, "createTime"));
		pagination.setRecordCount((int) userService.count(query));
		query.skip((pagination.getCurrentPage() - 1) * pagination.getPageSize()).limit(pagination.getPageSize());
		List<User> users = userService.findList(query);
		
		List<String> ids = new LinkedList<String>();
		for(User u : users){
			ids.add(u.getId());
		}
		query = Query.query(Criteria.where("application.$id").is(app.getId()).and("user.$id").in(ids));
		List<ApplicationUser> appUsers = applicationUserService.findList(query);
		Map<String,ApplicationUser> appUserMap = new HashMap<String, ApplicationUser>();
		for(ApplicationUser appUser : appUsers){
			appUserMap.put(appUser.getUser().getId(), appUser);
		}
		appUsers = new ArrayList<ApplicationUser>(users.size());
		for(User u : users){
			ApplicationUser appUser = null;
			if((appUser  = appUserMap.get(u.getId())) == null){
				appUser = new ApplicationUser();
				appUser.setApplication(app);
				appUser.setUser(u);
				appUser.setLinked(false);
			}
			appUsers.add(appUser);
		}
		model.put("app", app);
		model.put("user", user);
		model.put("datas", appUsers);
		model.put("pagination", pagination);
		return "app/linkeduser";
	}
	
	@RequestMapping(value="linksave")
	@ResponseBody
	public Map<String, Object> linksave(String appid,String userid,ModelMap model){
		Map<String,Object> result = new HashMap<String, Object>();
		result.put(STATUS, true);
		if(applicationUserService.checkExists(Criteria.where("application.$id").is(appid).and("user.$id").is(userid))){
			result.put(STATUS, false);
			result.put(MSG, "该用户己关联此应用，不需要重复关联");
		}else{
			Application app = appService.findById(appid);
			User user = userService.findById(userid);
			ApplicationUser appUser = new ApplicationUser();
			appUser.setApplication(app);
			appUser.setUser(user);
			applicationUserService.add(appUser);
			result.put(MSG,"关联成功");
		}
		return result;
	}
	
	@RequestMapping(value="unlink")
	@ResponseBody
	public Map<String, Object> unlink(String appid,String userid,ModelMap model){
		Map<String,Object> result = new HashMap<String, Object>();
		result.put(STATUS, true);
		applicationUserService.delete(Query.query(Criteria.where("application.$id").is(appid).and("user.$id").is(userid)));
		result.put(MSG, "解除成功");
		return result;
	}

}
