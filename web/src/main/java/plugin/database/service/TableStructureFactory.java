/**
 * TableStructureFactory.java.java
 * @author FengMy
 * @since 2015年3月30日
 */
package plugin.database.service;

import plugin.database.enums.DbType;
import plugin.database.service.impl.OracleTableStructureServiceImpl;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年3月30日
 */
public class TableStructureFactory {
	private TableStructureFactory(){
	}
	
	/**
	 * 创建方言
	 * @param dbType
	 * @return
	 */
	public static TableStructureService createTableStructureService(DbType dbType){
		if(dbType == DbType.ORACLE){
			return new OracleTableStructureServiceImpl();
		}else if(dbType == DbType.MYSQL){
			return null;
		}
		return null;
	}
}
