/**
 * PortalController.java.java
 * @author FengMy
 * @since 2015年9月29日
 */
package plugin.portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import plugin.portal.context.WebContext;
import plugin.portal.entity.ApplicationUser;
import plugin.portal.entity.User;
import plugin.portal.service.ApplicationUserService;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年9月29日
 */
@Controller
@RequestMapping(value="portal/*")
public class PortalController extends BaseController {
	
	@Autowired
	private ApplicationUserService applicationUserService;

	@RequestMapping(value="appList")
	public String appList(ModelMap model){
		User user = WebContext.getLoginUser();
		List<ApplicationUser> appUsers = applicationUserService.findList(Query.query(Criteria.where("user.$id").is(user.getId())));
		model.put("datas", appUsers);
		return "portal/appList";
	}
	
	@RequestMapping(value="portalLogin")
	@ResponseBody
	public Map<String,Object> portalLogin(@RequestParam(value="id",required=true)String appUserId,ModelMap model){
		Map<String,Object> result = new HashMap<String, Object>();
		ApplicationUser appUser = applicationUserService.findById(appUserId);
		User user = WebContext.getLoginUser();
		if(appUser == null || !user.getId().equals(appUser.getUser().getId())){
			result.put(STATUS, false);
			result.put(MSG, "未被关联此应用");
		}else{
			result.put(STATUS, true);
			String url = appUser.getApplication().getUrl();
			if(url.indexOf("?") > 0){
				url += "&";
			}else{
				url += "?";
			}
			url += appUser.getApplication().getTicketName() + "=";
			result.put("url", url);
		}
		return result;
	}
}
