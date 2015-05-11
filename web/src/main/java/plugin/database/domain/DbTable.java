/**
 * DbTable.java.java
 * @author FengMy
 * @since 2015年3月26日
 */
package plugin.database.domain;

import java.io.Serializable;
import java.util.List;

/**  
 * 功能描述：数据表
 * 
 * @author FengMy
 * @since 2015年3月26日
 */
public class DbTable implements Serializable {
	private static final long serialVersionUID = 1563216211884258469L;

	/**
	 * 表名
	 */
	private String name;
	
	/**
	 * 注释
	 */
	private String comments;
	
	/**
	 * 建表语句
	 */
	private String sql;
	
	/**
	 * 列信息
	 */
	private List<DbTableColumn> columns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<DbTableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DbTableColumn> columns) {
		this.columns = columns;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
