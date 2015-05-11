/**
 * DbTableColumn.java.java
 * @author FengMy
 * @since 2015年3月26日
 */
package plugin.database.domain;

import java.io.Serializable;

/**  
 * 功能描述：数据列
 * 
 * @author FengMy
 * @since 2015年3月26日
 */
public class DbTableColumn implements Serializable {
	private static final long serialVersionUID = -2556782345750006657L;

	private DbTable table;
	
	/**
	 * 列名
	 */
	private String name;
	
	/**
	 * 注释
	 */
	private String comments;
	
	/**
	 * 数据类型
	 */
	private String dataType;
	
	/**
	 * 长度
	 */
	private String dataLength;
	
	/**
	 * 精度
	 */
	private String dataPrecision;
	
	/**
	 * 小数位数
	 */
	private String dataScale;
	
	/**
	 * 是否可以为空
	 */
	private String nullAble;
	
	/**
	 * 主键
	 */
	private String primaryKey;

	public DbTable getTable() {
		return table;
	}

	public void setTable(DbTable table) {
		this.table = table;
	}

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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataLength() {
		return dataLength;
	}

	public void setDataLength(String dataLength) {
		this.dataLength = dataLength;
	}

	public String getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(String dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getDataScale() {
		return dataScale;
	}

	public void setDataScale(String dataScale) {
		this.dataScale = dataScale;
	}

	public String getNullAble() {
		return nullAble;
	}

	public void setNullAble(String nullAble) {
		this.nullAble = nullAble;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
}
