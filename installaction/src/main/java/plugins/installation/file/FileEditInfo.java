/**
 * FileEditInfo.java.java
 * @author FengMy
 * @since 2015年6月1日
 */
package plugins.installation.file;

import java.util.List;

/**  
 * 功能描述：文件内容编辑
 * 
 * @author FengMy
 * @since 2015年6月1日
 */
public class FileEditInfo {
	
	/**
	 * 需要编辑的文件
	 */
	private String file;
	
	/**
	 * 需要修改的数据项
	 */
	private List<FileEditItem> items;
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public List<FileEditItem> getItems() {
		return items;
	}

	public void setItems(List<FileEditItem> items) {
		this.items = items;
	}

	/**  
	 * 功能描述：文件修改项
	 * 
	 * @author FengMy
	 * @since 2015年6月1日
	 */
	public static final class FileEditItem{
		
		/**
		 * 修改项名称
		 */
		private String itemName;
		
		/**
		 * 修改项说明
		 */
		private String itemDesc;

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public String getItemDesc() {
			return itemDesc;
		}

		public void setItemDesc(String itemDesc) {
			this.itemDesc = itemDesc;
		}
	}
}
