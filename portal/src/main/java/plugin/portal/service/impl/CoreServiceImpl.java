/**
 * CoreServiceImpl.java.java
 * @author FengMy
 * @since 2015年9月25日
 */
package plugin.portal.service.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import plugin.portal.entity.CoreEntity;
import plugin.portal.service.CoreService;
import plugin.portal.utils.ExecuteCallback;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年9月25日
 */
public abstract class CoreServiceImpl<T extends CoreEntity> implements CoreService<T> {
	
	@PersistenceContext
	private EntityManager entityManager;
	

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	protected Class<T> getCurrentClass(){
		ParameterizedType parameterizedType = (ParameterizedType)this.getClass().getGenericSuperclass(); 
		@SuppressWarnings("unchecked")
		Class<T> entityClass= (Class<T>)(parameterizedType.getActualTypeArguments()[0]); 
		return entityClass;
	}

	@Override
	public void insert(T entity) {
		if(entity.getId() == null){
			entity.setId(UUID.randomUUID().toString());
		}
		entityManager.persist(entity);
	}

	@Override
	public void insert(Collection<T> entityList) {
		if(entityList != null){
			for(T entity : entityList){
				insert(entity);
			}
		}
	}

	@Override
	public void update(T entity) {
		entityManager.merge(entity);
	}

	@Override
	public T findById(String id) {
		return entityManager.find(getCurrentClass(), id);
	}

	@Override
	public void deleteById(String id) {
		try{
			Class<T> clazz = getCurrentClass();
			T entity = clazz.newInstance();
			entity.setId(id);
			delete(entity);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void delete(T entity) {
		entityManager.remove(entity);
	}
	
	@Override
	public List<T> find(String jpql,Object... params) {
		TypedQuery<T> query = entityManager.createQuery(jpql,getCurrentClass());
		if(params != null && params.length > 0){
			for(int i = 0; i < params.length; i++){
				query.setParameter(i+1, params[i]);
			}
		}
		return query.getResultList();
	}
	
	@Override
	public T findOne(String jpql,Object... params) {
		List<T> resultList = find(jpql,params);
		if(resultList != null && !resultList.isEmpty()){
			return resultList.get(0);
		}
		return null;
	}
	
	@Override
	public List<T> find(ExecuteCallback<T> callback) {
		CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(getCurrentClass());
		if(callback != null){
			callback.callback(query);
		}
		return entityManager.createQuery(query).getResultList();
	}

}
