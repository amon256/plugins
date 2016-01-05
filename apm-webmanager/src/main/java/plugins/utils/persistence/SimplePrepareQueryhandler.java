/**
 * 
 */
package plugins.utils.persistence;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import plugins.apm.entitys.CoreEntity;

/**
 * 简单实体查询handler
 * @author fengmengyue
 *
 */
public class SimplePrepareQueryhandler<T extends CoreEntity> implements PrepareQueryHandler<T> {
	
	private T entity;
	
	public SimplePrepareQueryhandler(T entity){
		this.entity = entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CriteriaQuery<T> create(CriteriaBuilder cb) {
		CriteriaQuery<T> query = (CriteriaQuery<T>) cb.createQuery(entity.getClass());
		Root<T> root = (Root<T>) query.from(entity.getClass());
		onCreated(cb, query, root);
		if(StringUtils.isNotEmpty(entity.getSort_())){
			Path<Object> path = null;
			if(entity.getSort_().indexOf(".") > 0){
				String[] paths = entity.getSort_().trim().split("\\.");
				path = root.get(paths[0]);
				for(int i = 1; i < paths.length; i++){
					path = path.get(paths[i]);
				}
			}else{
				path = root.get(entity.getSort_().trim());
			}
			if("asc".equalsIgnoreCase(entity.getOrder_())){
				query.orderBy(cb.asc(path));
			}else if("desc".equalsIgnoreCase(entity.getOrder_())){
				query.orderBy(cb.desc(path));
			}
		}
		Predicate[] conditions = getWhereCondition(cb, entity,root);
		if(conditions != null && conditions.length > 0){
			query.where(conditions);
		}
		return query;
	}
	
	protected void onCreated(CriteriaBuilder cb,CriteriaQuery<T> query,Root<T> root){
		
	}

	protected Predicate[] getWhereCondition(CriteriaBuilder cb,T entity,Root<T> root){
		return null;
	}
}
