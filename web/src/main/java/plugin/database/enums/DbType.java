/**
 * DbType.java.java
 * @author FengMy
 * @since 2015年3月30日
 */
package plugin.database.enums;

/**  
 * 功能描述：数据库类型
 * 
 * @author FengMy
 * @since 2015年3月30日
 */
public enum DbType {
	ORACLE("oracle","oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:@[IP]:[PORT]:[SERVICE_NAME]"),
	MYSQL("mysql","com.mysql.jdbc.Driver","jdbc:mysql://[IP]:[PORT]/[DATABASE_NAME]");
	/**
	 * 驱动class名
	 */
	private String driverClassName;
	/**
	 * 数据库类型
	 */
	private String dbType;
	/**
	 * 连接url模板
	 */
	private String urlPattern;
	private DbType(String dbType,String driverClassName,String urlPattern){
		this.driverClassName = driverClassName;
		this.dbType = dbType;
		this.urlPattern = urlPattern;
	}
	public String getDriverClassName() {
		return driverClassName;
	}
	public String getDbType() {
		return dbType;
	}
	
	public String getUrlPattern() {
		return urlPattern;
	}
}
