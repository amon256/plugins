/**
 * LoginInterceptor.java.java
 * @author FengMy
 * @since 2015年7月6日
 */
package plugins.apm.interceptors;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import plugins.apm.context.WebContext;
import plugins.apm.entitys.Roleable;
import plugins.apm.permission.PermissionManager;
import plugins.spring.ApplicationContextAware;
import plugins.utils.CollectionUtils;
import plugins.utils.ResponseObject;

/**  
 * 功能描述：登录拦截器
 * 
 * @author FengMy
 * @since 2015年7月6日
 */
public class PermissionInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);
	
	private String noPermissionPath = null;

	public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {
		String path = request.getServletPath();
		logger.debug("accept request : {}",path);
		PermissionManager manager = ApplicationContextAware.getApplicationContext().getBean(PermissionManager.class);
		if(!manager.isPermissionControl(path) && !"/index".equals(path)){
			logger.debug("accept request is not in permission control.");
			return true;
		}
		logger.debug("accept request is in permission control.");
		Roleable user = WebContext.getLoginUser();
		boolean isAllow = false;
		if(user != null && user.getRoles() != null){
			List<String> roles = CollectionUtils.createList(String.class, user.getRoles().split(","));
			if(manager.validatePermission(path, roles)){
				isAllow = true;
				logger.debug("accept is allow.");
			}
		}
		if(!isAllow){
			logger.debug("accept is forbidden.");
			boolean isAjax = request.getHeader("X-Requested-With") != null;
			if(isAjax){
				response.setHeader("Content-type", "text/html;charset=UTF-8");  
				response.setCharacterEncoding("UTF-8");
				ResponseObject rb = new ResponseObject();
				rb.setStatus("noPermission");
				rb.setMsg("权限不足");
				JsonGenerator jsonGenerator = new ObjectMapper().getJsonFactory().createJsonGenerator(response.getOutputStream(), JsonEncoding.UTF8);
				jsonGenerator.writeObject(rb);
				jsonGenerator.flush();
				jsonGenerator.close();
			}else{
				response.sendRedirect(getNoPermissionUrl(request,manager));
			}
			return false;
		}
		return true;
	}
	
	public String getNoPermissionUrl(HttpServletRequest request, PermissionManager manager){
		if(noPermissionPath == null){
			String menuId = manager.getDefaultMenu().getId();
			String pageId = manager.getPageUrlMap().get("/noPermission").getId();
			noPermissionPath = request.getContextPath() + "/index?_m=" + menuId + "&_p=" + pageId;
		}
		return noPermissionPath;
	}
	
	public void setNoPermissionPath(String noPermissionPath) {
		this.noPermissionPath = noPermissionPath;
	}
}
