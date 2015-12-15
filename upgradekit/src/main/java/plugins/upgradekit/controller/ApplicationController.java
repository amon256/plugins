/**
 * ApplicationController.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.controller;

import java.io.IOException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import plugins.upgradekit.entitys.Application;
import plugins.upgradekit.service.ApplicationService;
import plugins.upgradekit.tools.ApplicationUpgradeConfig;
import plugins.upgradekit.tools.MessageWriter;
import plugins.upgradekit.tools.NativeCommandExecutor;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.App;
import plugins.utils.CreateQueryHandler;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
@Controller
@RequestMapping(value="application/*")
public class ApplicationController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationService applicationService;
	
	@RequestMapping(value="list")
	public String list(){
		return "application/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<Application> listData(Pagination<Application> pagination,Application application) throws JsonGenerationException, JsonMappingException, IOException{
		logger.debug("查询应用数据:{}",new ObjectMapper().writeValueAsString(application));
		applicationService.findPagination(pagination, application);
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String toAdd(ModelMap model){
		return "application/add";
	}
	
	@RequestMapping(value="addSave")
	@ResponseBody
	public ResponseObject addSave(final Application application) throws JsonGenerationException, JsonMappingException, IOException{
		logger.debug("新增应用数据:{}",new ObjectMapper().writeValueAsString(application));
		ResponseObject rb = ResponseObject.newInstance().fail();
		if(StringUtils.isEmpty(application.getName())){
			rb.setMsg("应用名不能为空");
		}else if(StringUtils.isEmpty(application.getNumber())){
			rb.setMsg("应用编码不能为空");
		}else{
			CreateQueryHandler<Long> handler = new CreateQueryHandler<Long>() {
				@Override
				public CriteriaQuery<Long> create(CriteriaBuilder cb) {
					CriteriaQuery<Long> query = cb.createQuery(Long.class);
					Root<Application> root = query.from(Application.class);
					query.where(cb.equal(root.get("number"), application.getNumber()));
					return query;
				}
			};
			if(applicationService.countByQuery(handler) > 0){
				rb.setMsg("应用编码己存在");
			}else{
				applicationService.addEntity(application);
				rb.success();
			}
		}
		return rb;
	}
	
	@RequestMapping(value="checkStatus")
	@ResponseBody
	public ResponseObject checkStatus(@RequestParam(value="id",required=true)String id){
		ResponseObject rb = ResponseObject.newInstance().success();
		Application application = applicationService.getEntityById(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && app.getStatusCmd() != null){
				final StringBuilder result = new StringBuilder();
				NativeCommandExecutor.executeNativeCommand(new MessageWriter() {
					@Override
					public void write(String message) {
						result.append(message).append("\n");
					}
				},app.getStatusCmd().getCmd(), config.getCharset(), new String[]{},null,10000);
				if(result.indexOf(app.getStatusCmd().getIncludeValue()) >= 0){
					rb.put("appStatus", "running");
				}
			}else{
				rb.fail();
				rb.setMsg("应用状态检查命令未配置");
			}
		}else{
			rb.fail();
			rb.setMsg("应用不存在");
		}
		return rb;
	}
}
