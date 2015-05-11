/**
 * DatabaseUtil.java.java
 * @author FengMy
 * @since 2015年3月30日
 */
package plugin.database.util;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import plugin.database.domain.ConnectionInfo;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年3月30日
 */
public class DatabaseUtil {

	private DatabaseUtil(){
	}
	
	public static Connection getConnection(ConnectionInfo connectionInfo){
		Connection conn = null;
		try {
			Class.forName(connectionInfo.getDbType().getDriverClassName());
			conn = DriverManager.getConnection(connectionInfo.getUrl(), connectionInfo.getUsername(), connectionInfo.getPassword());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return conn;
	}
	
	public static BasicDataSource getDataSource(ConnectionInfo connectionInfo,int maxActive){
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(connectionInfo.getUrl());
		dataSource.setUsername(connectionInfo.getUsername());
		dataSource.setPassword(connectionInfo.getPassword());
		dataSource.setDriverClassName(connectionInfo.getDbType().getDriverClassName());
		dataSource.setMaxActive(maxActive<5?5:maxActive);
		return dataSource;
	}
}
