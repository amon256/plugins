/**
 * BaseService.java
 * create by FengMy from 2013
 */
package plugins.upgradekit.service;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import plugins.upgradekit.entitys.CoreEntity;
import plugins.utils.CreateQueryHandler;

/**
 * @描述: 业务层基类接口
 * @author FengMy
 * @since 2013-3-6
 */
@Transactional
public interface BaseService<T extends CoreEntity> {
	/**
	 * 新增实体
	 * @param entity
	 * @return
	 */
	T addEntity(T entity);
	
	/**
	 * 批量持久化新实体
	 * @param entities
	 * @return
	 */
	int addBatch(List<T> entities);
	
	/**
	 * 更新实体
	 * @param entity
	 */
	T updateEntity(T entity);
	
	/**
	 * 更新
	 * @param entity
	 * @param updateFields 需要更新的字段
	 * @return
	 */
	T updateEntity(T entity,Collection<String> updateFields);
	
	/**
	 * 批量更新实体
	 * @param entities
	 */
	int updateBatch(List<T> entities);
	
	/**
	 * 删除实体
	 * @param entity
	 */
	int deleteEntity(T entity);
	
	/**
	 * 删除实体
	 * @param entity
	 */
	int deleteBatch(List<T> entities);
	
	/**
	 * 根据id获取指定类型实体
	 * @param <T>
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	T getEntityById(Class<T> type,Object id);
	
	/**
	 * 根据ID获取实体,实体类型以当前Service的泛型类型为准
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	T getEntityById(Object id);
	
	/**
	 * 根据query统计
	 * @param handler
	 * @return
	 */
	int countByQuery(CreateQueryHandler<Long> handler);
	
	/**
	 * 根据query查询
	 * @param handler
	 * @return
	 */
	List<T> findByQuery(CreateQueryHandler<T> handler);
	
}
