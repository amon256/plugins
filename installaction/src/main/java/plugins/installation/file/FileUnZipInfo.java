/**
 * FileDecompressionInfo.java.java
 * @author FengMy
 * @since 2015年6月5日
 */
package plugins.installation.file;


/**  
 * 功能描述：文件解压信息
 * 
 * @author FengMy
 * @since 2015年6月5日
 */
public class FileUnZipInfo {
	
	/**
	 * 描述
	 */
	private String desc;
	
	/**
	 * 文件
	 */
	private String file;
	
	/**
	 * 解压为
	 */
	private String to;
	
	/**
	 * 编码
	 */
	private String encoding;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
