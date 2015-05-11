/**
 * ConnectionInfo.java.java
 * @author FengMy
 * @since 2015年3月30日
 */
package plugin.database.domain;

import java.io.Serializable;

import plugin.database.enums.DbType;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年3月30日
 */
public class ConnectionInfo implements Serializable {
	private static final long serialVersionUID = 7338750586401601448L;
	
	private DbType dbType;
	
	private String username;
	
	private String password;
	
	private String url;
	
	private String filter;

	public DbType getDbType() {
		return dbType;
	}

	public void setDbType(DbType dbType) {
		this.dbType = dbType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	

}
