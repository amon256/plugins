/**
 * CoreService.java.java
 * @author FengMy
 * @since 2015年9月25日
 */
package plugin.portal.service;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import plugin.portal.entity.CoreEntity;
import plugin.portal.utils.ExecuteCallback;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年9月25日
 */
@Transactional
public interface CoreService<T extends CoreEntity> {

	/**
	 * 新增
	 * @param entity
	 */
	public void insert(T entity);
	
	/**
	 * 批量新增
	 * @param entityList
	 */
	public void insert(Collection<T> entityList);
	
	/**
	 * 更新
	 * @param entity
	 */
	public void update(T entity);
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public T findById(String id);
	
	/**
	 * 根据id删除
	 * @param id
	 */
	public void deleteById(String id);
	
	/**
	 * 删除实体
	 * @param entity
	 */
	public void delete(T entity);
	
	
	
	/**
	 * 自定义查询
	 * @param query
	 * @return
	 */
	public T findOne(String jpql,Object... params);
	
	/**
	 * 自定义查询
	 * @param query
	 * @return
	 */
	public List<T> find(String jqpl,Object... params);
	
	/**
	 * 查询时设置查询参数
	 * @param callback
	 * @return
	 */
	public List<T> find(ExecuteCallback<T> callback);
	
}
