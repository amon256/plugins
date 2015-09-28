/**
 * DataServiceImpl.java.java
 * @author FengMy
 * @since 2015年9月25日
 */
package plugin.portal.service.impl;

import java.util.Date;

import plugin.portal.entity.DataEntity;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年9月25日
 */
public class DataServiceImpl<T extends DataEntity> extends CoreServiceImpl<T> {
	
	@Override
	public void insert(T entity) {
		if(entity != null && entity.getCreateTime() == null){
			entity.setCreateTime(new Date());
			entity.setLastUpdateTime(entity.getCreateTime());
		}
		super.insert(entity);
	}
	
	@Override
	public void update(T entity) {
		entity.setLastUpdateTime(new Date());
		super.update(entity);
	}
}
