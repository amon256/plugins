/**
 * ApplicationController.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.controller;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import plugins.upgradekit.entitys.Application;
import plugins.upgradekit.service.ApplicationService;
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

	@Autowired
	private ApplicationService applicationService;
	
	@RequestMapping(value="list")
	public String list(){
		return "application/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<Application> listData(Pagination<Application> pagination,Application application){
		applicationService.findPagination(pagination, application);
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String toAdd(ModelMap model){
		return "application/add";
	}
	
	@RequestMapping(value="addSave")
	@ResponseBody
	public ResponseObject addSave(final Application application){
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
}
