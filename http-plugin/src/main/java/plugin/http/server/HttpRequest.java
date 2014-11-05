/**
 * HttpRequest.java.java
 * @author FengMy
 * @since 2014年10月30日
 */
package plugin.http.server;

import java.io.InputStream;
import java.util.Map;

/**  
 * 功能描述：http request
 * 
 * @author FengMy
 * @since 2014年10月30日
 */
public interface HttpRequest {

	/**
	 * HTTP请求方式
	 * @return
	 */
	public HttpMethodEnum getMethod();
	
	/**
	 * 请求路径
	 * @return
	 */
	public String getRequestURI();
	
	/**
	 * 客户地址
	 * @return
	 */
	public String getClientAddress();
	
	/**
	 * 获取http头
	 * @param name
	 * @return
	 */
	public String getHead(String name);
	
	/**
	 * 获取http头集合
	 * @return
	 */
	public Map<String,String> getHeads();
	
	/**
	 * 获取参数
	 * @param name
	 * @return
	 */
	public String getParameter(String name);
	
	/**
	 * 获取所有参数
	 * @return
	 */
	public Map<String,String> getParameters();
	
	/**
	 * 请求io流
	 * @return
	 */
	public InputStream getInputStream();
	
	public static enum HttpMethodEnum{
		GET,POST
	}
}
