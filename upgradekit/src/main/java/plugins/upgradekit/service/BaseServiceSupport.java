/**
 * BaseServiceSupport.java
 * create by FengMy from 2013
 */
package plugins.upgradekit.service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import plugins.upgradekit.entitys.CoreEntity;
import plugins.utils.CreateQueryHandler;

/**
 * @描述: 业务基类基础实现
 * @author FengMy
 * @since 2013-3-6
 */
public abstract class BaseServiceSupport<T extends CoreEntity> implements BaseService<T> {
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	@Override
	public T addEntity(T entity) {
		if(StringUtils.isEmpty(entity.getId())){
			entity.setId(UUID.randomUUID().toString().toLowerCase().replaceAll("-", ""));
		}
		entityManager.persist(entity);
		return entity;
	}

	@Override
	public int addBatch(List<T> entities) {
		for(T entity : entities){
			addEntity(entity);
		}
		return entities.size();
	}

	@Override
	public T updateEntity(T entity) {
		return entityManager.merge(entity);
	}

	@Override
	public int updateBatch(List<T> entities) {
		for(T entity : entities){
			updateEntity(entity);
		}
		return entities.size();
	}

	@Override
	public int deleteEntity(T entity) {
		entityManager.remove(entity);
		return 1;
	}

	@Override
	public int deleteBatch(List<T> entities) {
		for(T entity : entities){
			deleteEntity(entity);
		}
		return entities.size();
	}

	@Override
	public T getEntityById(Class<T> type, Object id) {
		return entityManager.find(type, id);
	}
	
	@Override
	public T getEntityById(Object id) {
		Type[] types = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments();
		if(types!=null && types.length > 0){
			@SuppressWarnings("unchecked")
			Class<T> c = (Class<T>)(types[0]);
			return getEntityById(c, id);
		}
		return null;
	}
	
	protected Class<T> getCurrentClassType(){
		Type[] types = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments();
		if(types!=null && types.length > 0){
			@SuppressWarnings("unchecked")
			Class<T> c = (Class<T>)(types[0]);
			return c;
		}
		return null;
	}
	
	@Override
	public T updateEntity(T entity, Collection<String> updateFields) {
		if(entity == null || StringUtils.isEmpty(entity.getId()) || updateFields==null || updateFields.isEmpty()){
			return null;
		}
		T old = getEntityById(entity.getId());
		if(old != null){
			PropertyDescriptor[] propertys = BeanUtils.getPropertyDescriptors(old.getClass());
			List<String> ignoreProperties = new LinkedList<String>();
			for(PropertyDescriptor pd : propertys){
				if(!updateFields.contains(pd.getName())){
					ignoreProperties.add(pd.getName());
				}
			}
			String[] attr = new String[ignoreProperties.size()];
			BeanUtils.copyProperties(entity, old, ignoreProperties.toArray(attr));
			entityManager.merge(old);
		}
		return old;
	}
	
	@Override
	public int countByQuery(CreateQueryHandler<Long> handler) {
		CriteriaQuery<Long> query = null;
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		if(handler != null){
			query = handler.create(cb);
		}
		if(query == null){
			query = cb.createQuery(Long.class);
		}
		Class<T> clazz = getCurrentClassType();
		Root<T> root = query.from(clazz);
		query.select(cb.count(root));
		return entityManager.createQuery(query).getSingleResult().intValue();
	}
	
	private CriteriaQuery<T> getQueryByHandler(CreateQueryHandler<T> handler){
		CriteriaQuery<T> query = null;
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		if(handler != null){
			query = handler.create(cb);
		}
		if(query == null){
			Class<T> clazz = getCurrentClassType();
			query = cb.createQuery(clazz);
			query.from(clazz);
		}
		return query;
	}
	
	@Override
	public List<T> findByQuery(CreateQueryHandler<T> handler) {
		return entityManager.createQuery(getQueryByHandler(handler)).getResultList();
	}

}
