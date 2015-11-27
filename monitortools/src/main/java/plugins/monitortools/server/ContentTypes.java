/**
 * ContentTypes.java.java
 * @author FengMy
 * @since 2015年11月27日
 */
package plugins.monitortools.server;

import java.util.HashMap;
import java.util.Map;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月27日
 */
public class ContentTypes {
	
	public static final String CONTENT_TYPE = "Content-Type";
	
	private static Map<String,String> contentTypes = new HashMap<String,String>();
	static{
		contentTypes.put("", "text/html");
		contentTypes.put("html", "text/html");
		contentTypes.put("htm", "text/html");
		contentTypes.put("xhtml", "text/html");
		contentTypes.put("js", "application/x-javascript");
		contentTypes.put("css", "text/css");
	}

	public static String getContentType(String fileSuffix){
		if(contentTypes.containsKey(fileSuffix)){
			return contentTypes.get(fileSuffix);
		}
		return "application/octet-stream";
	}
}
