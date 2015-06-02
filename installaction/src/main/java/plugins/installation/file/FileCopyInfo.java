/**
 * FileCopyInfo.java.java
 * @author FengMy
 * @since 2015年6月1日
 */
package plugins.installation.file;


/**  
 * 功能描述：文件复制信息
 * 
 * @author FengMy
 * @since 2015年6月1日
 */
public class FileCopyInfo {

	/**
	 * 从文件
	 */
	private String from;
	
	/**
	 * 复制到
	 */
	private String to;
	
	/**
	 * 节点描述
	 */
	private String desc;
	
	/**
	 * 节点字符串 
	 */
	private String nodeText;
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
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

	public String getNodeText() {
		return nodeText;
	}

	public void setNodeText(String nodeText) {
		this.nodeText = nodeText;
	}
}
