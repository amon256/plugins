/**
 * DataService.java
 * create by FengMy from 2013
 */
package plugins.upgradekit.service;

import java.util.Date;

import plugins.upgradekit.entitys.DataEntity;

/**
 * @描述: 数据基类Service实现
 * @author FengMy
 * @since 2013-3-25
 */
public abstract class DataService<T extends DataEntity> extends BaseServiceSupport<T> {
	
	/**
	 *覆盖，设置创建时间和最后更新时间
	 */
	@Override
	public T addEntity(T entity) {
		if(entity.getCreateTime()==null){
			entity.setCreateTime(new Date());
		}
		if(entity.getLastUpdateTime()==null){
			entity.setLastUpdateTime(new Date());
		}
		return super.addEntity(entity);
	}
	
	/**
	 *覆盖，设置最后更新时间
	 */
	@Override
	public T updateEntity(T entity) {
		entity.setLastUpdateTime(new Date());
		return super.updateEntity(entity);
	}
}
