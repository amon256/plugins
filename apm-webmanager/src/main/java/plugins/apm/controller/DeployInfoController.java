/**
 * 
 */
package plugins.apm.controller;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import plugins.apm.entitys.Application_;
import plugins.apm.entitys.DeployInfo;
import plugins.apm.entitys.DeployInfo_;
import plugins.apm.entitys.HostMachine;
import plugins.apm.entitys.HostMachine_;
import plugins.apm.service.ApplicationService;
import plugins.apm.service.DeployInfoService;
import plugins.apm.service.HostMachineService;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;
import plugins.utils.persistence.SimplePrepareQueryhandler;
import plugins.validation.LengthRangeValidationRule;
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
@RequestMapping(value="deployinfo/*")
public class DeployInfoController extends BaseController {
	
	@Autowired
	private DeployInfoService service;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private HostMachineService hostMachineService;
	
	@RequestMapping(value="list")
	public String list(){
		return "deployinfo/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<DeployInfo> listData(Pagination<DeployInfo> pagination,DeployInfo entity){
		service.findPagination(pagination, new SimplePrepareQueryhandler<DeployInfo>(entity){
			
			@Override
			protected void onCreated(CriteriaBuilder cb, CriteriaQuery<DeployInfo> query, Root<DeployInfo> root) {
				super.onCreated(cb, query, root);
			}
			
			@Override
			protected Predicate[] getWhereCondition(CriteriaBuilder cb,DeployInfo entity, Root<DeployInfo> root) {
				List<Predicate> predicates = new LinkedList<Predicate>();
				if(entity.getApplication() != null && StringUtils.isNotEmpty(entity.getApplication().getId())){
					predicates.add(cb.equal(root.get(DeployInfo_.application), entity.getApplication()));
				}
				if(entity.getMachine() != null && StringUtils.isNotEmpty(entity.getMachine().getId())){
					predicates.add(cb.equal(root.get(DeployInfo_.machine), entity.getMachine()));
				}
				Predicate and = null;
				if(predicates != null){
					and = cb.and(predicates.toArray(new Predicate[]{}));
				}
				Predicate ret = null;
				if(StringUtils.isNotEmpty(entity.getKeyword())){
					String key = "%" + entity.getKeyword() + "%";
					Predicate or = cb.or(
						cb.like(root.get(DeployInfo_.application).get(Application_.name), key),
						cb.like(root.get(DeployInfo_.application).get(Application_.number), key),
						cb.like(root.get(DeployInfo_.machine).get(HostMachine_.name), key),
						cb.like(root.get(DeployInfo_.machine).get(HostMachine_.host), key),
						cb.like(root.get(DeployInfo_.identity), key)
					);
					if(and != null){
						ret = cb.and(and,or);
					}else{
						ret = or;
					}
				}
				if(ret != null){
					return new Predicate[]{ret};
				}
				return super.getWhereCondition(cb, entity, root);
			}
		});
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String toAdd(){
		return "deployinfo/add";
	}
	
	@RequestMapping(value="addSave")
	@ResponseBody
	public ResponseObject addSave(DeployInfo entity){
		ResponseObject rb = ResponseObject.newInstance().fail();
		List<ValidationResult> results = ValidationUtil.validate(entity, new Validation[]{
				new Validation("application.id", "应用", new ValidationRule[]{
					new RequiredValidationRule()
				}),
				new Validation("machine.id", "主机", new ValidationRule[]{
					new RequiredValidationRule()
				}),
				new Validation("identity", "部署标识", new ValidationRule[]{
					new RequiredValidationRule(),
					new LengthRangeValidationRule(3, 20),
					new ExistsDataValidationRule<DeployInfo>(service, "己存在"){
						protected Predicate[] getWhereCondition(CriteriaBuilder cb, DeployInfo entity, javax.persistence.criteria.Root<DeployInfo> root) {
							if(entity.getMachine() == null || StringUtils.isEmpty(entity.getMachine().getId())){
								HostMachine machine = new HostMachine();
								machine.setId("nodata");
								entity.setMachine(machine);
							}
							return new Predicate[]{
									cb.and(
											cb.equal(root.get(DeployInfo_.machine), entity.getMachine()),
											cb.equal(root.get(DeployInfo_.identity),entity.getIdentity())
									)
							};
						}
					}
				})
		});
		if(results == null || results.isEmpty()){
			service.insert(entity);
			rb.success();
		}else{
			rb.fail();
			rb.setMsg("校验不通过");
			rb.put("validateErrors", results);
		}
		return rb;
	}
}
