/**
 * DaoSupport.java.java
 * @author FengMy
 * @since 2015年11月24日
 */
package plugins.upgradekit.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import plugins.mybatis.CountParameter;
import plugins.utils.Pagination;

/**  
 * 功能描述：Dao基类
 * 
 * @author FengMy
 * @since 2015年11月24日
 */
public abstract class DaoSupport extends SqlSessionDaoSupport {
	
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}
	
	protected <T> List<T> pagination(Pagination<T> pagination,String mapper,Object param){
		if(StringUtils.isEmpty(mapper)){
			throw new IllegalArgumentException("mapper could not to be empty");
		}
		int count = queryCount(mapper, param);
		pagination.setRecordCount(count);
		List<T> list = null;
		if(count > 0){
			list = getSqlSession().selectList(mapper, param, new RowBounds((pagination.getCurrentPage() - 1) * pagination.getPageSize(), pagination.getPageSize()));
		}else{
			list = new ArrayList<T>(0);
		}
		pagination.setDatas(list);
		return list;
	}
	
	/**
	 * 查询结果集数量
	 * @param mapper
	 * @param param
	 * @return
	 */
	protected Integer queryCount(String mapper,Object param){
		if(StringUtils.isEmpty(mapper)){
			throw new IllegalArgumentException("mapper could not to be empty");
		}
		int count = (Integer) getSqlSession().selectOne(mapper, new CountParameter(param));
		return count;
	}
}
