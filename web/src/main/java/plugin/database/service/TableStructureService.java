/**
 * TableStructureService.java.java
 * @author FengMy
 * @since 2015年3月30日
 */
package plugin.database.service;

import java.io.OutputStream;
import java.util.List;

import plugin.database.domain.ConnectionInfo;
import plugin.database.domain.DbTable;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年3月30日
 */
public interface TableStructureService {

	/**
	 * 获取数据库列表
	 * @param connectionInfo
	 * @return
	 */
	public List<DbTable> getDbTables(ConnectionInfo connectionInfo);
	
	/**
	 * 获取单个表的信息，包含数据列
	 * @param connectionInfo
	 * @param tableName
	 * @return
	 */
	public DbTable getDbTableInfo(ConnectionInfo connectionInfo,String tableName);
	
	/**
	 * 导出静态资源
	 * @param connectionInfo
	 * @param outputstream
	 */
	public void exportDbTables(ConnectionInfo connectionInfo,OutputStream outputstream);
}
