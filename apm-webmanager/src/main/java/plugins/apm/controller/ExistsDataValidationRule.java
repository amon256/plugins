/**
 * 
 */
package plugins.apm.controller;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import plugins.apm.entitys.CoreEntity;
import plugins.apm.service.IService;
import plugins.utils.persistence.PrepareQueryHandler;
import plugins.validation.ValidationRule;

/**
 * 数据是否存在校验工具
 * @author fengmengyue
 *
 */
public class ExistsDataValidationRule<T extends CoreEntity> implements ValidationRule {
	
	private IService<T> service;
	private String errorMessage;
	
	public ExistsDataValidationRule(IService<T> service,String errorMessage){
		this.service = service;
		this.errorMessage = errorMessage;
	}

	@Override
	public String type() {
		return null;
	}

	@Override
	public boolean validate(Object bean, Object value) {
		if(value == null){
			return true;
		}
		if(value instanceof String){
			if(StringUtils.isEmpty(value.toString().trim())){
				return true;
			}
		}
		@SuppressWarnings("unchecked")
		final T entity = (T)bean;
		Long count = service.count(new PrepareQueryHandler<Long>() {
			@Override
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public CriteriaQuery create(CriteriaBuilder cb) {
				CriteriaQuery<T> query = (CriteriaQuery<T>) cb.createQuery(entity.getClass());
				Root<T> root = (Root<T>) query.from(entity.getClass());
				Predicate[] conditions = getWhereCondition(cb, entity,root);
				if(conditions != null && conditions.length > 0){
					query.where(conditions);
				}
				return query;
			}
		});
		return count == 0;
	}
	
	protected Predicate[] getWhereCondition(CriteriaBuilder cb,T entity,Root<T> root){
		return null;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage == null ? "己存在" : errorMessage;
	}

}
