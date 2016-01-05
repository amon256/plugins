/**
 * 
 */
package plugins.apm.controller;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import plugins.apm.entitys.HostMachine;
import plugins.apm.entitys.HostMachine_;
import plugins.apm.service.HostMachineService;
import plugins.utils.CollectionUtils;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;
import plugins.utils.persistence.SimplePrepareQueryhandler;
import plugins.validation.LengthRangeValidationRule;
import plugins.validation.RangeValidationRule;
import plugins.validation.RegexpValidationRule;
import plugins.validation.RequiredValidationRule;
import plugins.validation.Validation;
import plugins.validation.ValidationResult;
import plugins.validation.ValidationRule;
import plugins.validation.ValidationUtil;

/**
 * @author fengmengyue
 *
 */
@Controller
@RequestMapping(value="hostmachine")
public class HostMachineController extends BaseController {

	@Autowired
	private HostMachineService service;
	
	@RequestMapping(value="list")
	public String list(ModelMap model){
		return "hostmachine/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<HostMachine> listData(Pagination<HostMachine> pagination,HostMachine entity,HttpServletRequest request,HttpServletResponse response){
		service.findPagination(pagination, new SimplePrepareQueryhandler<HostMachine>(entity){
			@Override
			protected Predicate[] getWhereCondition(CriteriaBuilder cb, HostMachine entity, Root<HostMachine> root) {
				if(StringUtils.isNotEmpty(entity.getKeyword())){
					String key = "%" + entity.getKeyword() + "%";
					return new Predicate[]{
							cb.or(
									cb.like(root.get(HostMachine_.host), key),
									cb.like(root.get(HostMachine_.port), key),
									cb.like(root.get(HostMachine_.name), key)
							)
					};
				}
				return super.getWhereCondition(cb, entity, root);
			}
		});
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String add(ModelMap model){
		return "hostmachine/add";
	}
	
	@RequestMapping(value="saveAdd")
	@ResponseBody
	public ResponseObject saveAdd(HostMachine entity,ModelMap model){
		ResponseObject rb = ResponseObject.newInstance().fail();
		List<ValidationResult> result = ValidationUtil.validate(entity, new Validation[]{
				new Validation("name", "主机名", new ValidationRule[]{
						new RequiredValidationRule(),
						new LengthRangeValidationRule(2, 10)
				}),
				new Validation("host", "主机地址", new ValidationRule[]{
						new RequiredValidationRule(),
						new RegexpValidationRule("^(2[5][0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(2[5][0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$"),
						new ExistsDataValidationRule<HostMachine>(service,null){
							@Override
							protected Predicate[] getWhereCondition(CriteriaBuilder cb, HostMachine entity,Root<HostMachine> root) {
								return new Predicate[]{cb.equal(root.get(HostMachine_.host), entity.getHost())};
							}
						}
				}),
				new Validation("port", "端口", new ValidationRule[]{
						new RequiredValidationRule(),
						new RangeValidationRule(1000, 65535)
				}),
		});
		if(result == null || result.isEmpty()){
			service.insert(entity);
			rb.success();
		}else{
			rb.setMsg("数据校验未通过");
			rb.put("validateErrors", result);
		}
		return rb;
	}
	
	@RequestMapping(value="toEdit")
	public String toEdit(@RequestParam(value="id")String id,ModelMap model){
		HostMachine entity = service.findEntity(id);
		model.put("entity", entity);
		return "hostmachine/edit";
	}
	
	@RequestMapping(value="saveEdit")
	@ResponseBody
	public ResponseObject saveEdit(HostMachine entity,ModelMap model){
		ResponseObject rb = ResponseObject.newInstance().fail();
		List<ValidationResult> result = ValidationUtil.validate(entity, new Validation[]{
				new Validation("name", "主机名", new ValidationRule[]{
						new RequiredValidationRule(),
						new LengthRangeValidationRule(2, 10)
				}),
				new Validation("host", "主机地址", new ValidationRule[]{
						new RequiredValidationRule(),
						new RegexpValidationRule("^(2[5][0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(2[5][0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$"),
						new ExistsDataValidationRule<HostMachine>(service,null){
							@Override
							protected Predicate[] getWhereCondition(CriteriaBuilder cb, HostMachine entity,Root<HostMachine> root) {
								return new Predicate[]{cb.and(
										cb.notEqual(root.get(HostMachine_.id), entity.getId()),
										cb.equal(root.get(HostMachine_.host), entity.getHost()))};
							}
						}
				}),
				new Validation("port", "端口", new ValidationRule[]{
						new RequiredValidationRule(),
						new RangeValidationRule(1000, 65535)
				}),
		});
		if(result == null || result.isEmpty()){
			service.update(entity,CollectionUtils.createSet(String.class, "name","host","port","description"));
			rb.success();
		}else{
			rb.setMsg("数据校验未通过");
			rb.put("validateErrors", result);
		}
		return rb;
	}
	
	@RequestMapping(value="delete")
	@ResponseBody
	public ResponseObject delete(@RequestParam(value="id")String id){
		ResponseObject rb = ResponseObject.newInstance().success();
		HostMachine entity = service.findEntity(id);
		if(entity == null){
			rb.fail();
			rb.setMsg("数据不存在");
		}
		return rb;
	}
}
