/**
 * 
 */
package plugins.datasource.readwrite;

import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**  
 * <b>System：</b>NDMP<br/>
 * <b>Title：</b>ReadWriteDataSource.java<br/>
 * <b>Description：</b> 对功能点的描述<br/>
 * <b>@author： </b>fengmengyue<br/>
 * <b>@date：</b>2014年6月23日 下午4:24:27<br/>  
 * <b>@version：</b> 1.0.0.0<br/>
 * <b>Copyright (c) 2014 ASPire Tech.</b>   
 *   
 */
public class ReadWriteDataSource extends AbstractRoutingDataSource {
	
	private String writeDataSource;
	
	private LinkedList<String> readDataSources;
	

	@Override
	protected Object determineCurrentLookupKey() {
		if(ReadWriteHolder.isReadOnly()){
			return getReadDataSources();
		}
		return writeDataSource;
	}
	
	private synchronized String getReadDataSources(){
		String dataSource = readDataSources.pollLast();
		readDataSources.push(dataSource);
		return dataSource;
	}

	public void setWriteDataSource(String writeDataSource) {
		this.writeDataSource = writeDataSource;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setReadDataSources(List<String> readDataSources) {
		this.readDataSources = new LinkedList(readDataSources);
	}
}
