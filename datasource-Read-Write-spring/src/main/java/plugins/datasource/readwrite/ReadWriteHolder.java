/**
 * 
 */
package plugins.datasource.readwrite;

/**  
 * <b>System：</b>NDMP<br/>
 * <b>Title：</b>ReadWriteHolder.java<br/>
 * <b>Description：</b> 对功能点的描述<br/>
 * <b>@author： </b>fengmengyue<br/>
 * <b>@date：</b>2014年6月23日 下午4:25:07<br/>  
 * <b>@version：</b> 1.0.0.0<br/>
 * <b>Copyright (c) 2014 ASPire Tech.</b>   
 *   
 */
public class ReadWriteHolder {
	private static final ThreadLocal<String> holder = new ThreadLocal<String>();
	private static final String READ_ONLY = "READ_ONLY";
	
	public static void setReadOnly(){
		holder.set(READ_ONLY);
	}
	
	public static boolean isReadOnly(){
		return READ_ONLY.equals(holder.get());
	}
	
	public static void reset(){
		holder.set(null);
	}
}
