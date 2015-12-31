/**
 * UpgradeContext.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.apm.tools;

import java.io.File;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
public class UpgradeContext {

	public static String getFileRoot(){
		String path = System.getProperty("user.dir") + File.separator + "versionfiles";
		File dir = new File(path);
		if(dir.exists() && dir.isFile()){
			throw new RuntimeException("path:" + path + " is not a directory.");
		}
		if(!dir.exists()){
			dir.mkdirs();
		}
		return path;
	}
}
