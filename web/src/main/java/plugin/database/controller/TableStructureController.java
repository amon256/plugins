/**
 * DatabaseController.java.java
 * @author FengMy
 * @since 2015年3月30日
 */
package plugin.database.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import plugin.database.domain.ConnectionInfo;
import plugin.database.domain.DbTable;
import plugin.database.enums.DbType;
import plugin.database.service.TableStructureFactory;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年3月30日
 */
@Controller
@RequestMapping(value="database/tablestructure/*")
public class TableStructureController {
	private static final String PAGE_PATH_ROOT = "database/tablestructure/";
	
	@RequestMapping(value="start")
	public String start(ModelMap model){
		model.put("dbTypes", DbType.values());
		return PAGE_PATH_ROOT + "start";
	}
	
	@RequestMapping(value="tables",method=RequestMethod.POST)
	public String tables(ConnectionInfo connectionInfo,ModelMap model,HttpServletRequest request){
		request.getSession().setAttribute("ConnectionInfo", connectionInfo);
		List<DbTable> dbTables = TableStructureFactory.createTableStructureService(connectionInfo.getDbType()).getDbTables(connectionInfo);
		model.put("dbTables", dbTables);
		return PAGE_PATH_ROOT + "tables";
	}
	
	@RequestMapping(value="table",method=RequestMethod.GET)
	public String table(@RequestParam(required=true,value="tableName")String tableName,ModelMap model,HttpServletRequest request){
		ConnectionInfo connectionInfo = (ConnectionInfo) request.getSession().getAttribute("ConnectionInfo");
		if(connectionInfo != null){
			DbTable dbTable = TableStructureFactory.createTableStructureService(connectionInfo.getDbType()).getDbTableInfo(connectionInfo, tableName);
			model.put("dbTable", dbTable);
		}
		return PAGE_PATH_ROOT + "table";
	}
	
	@RequestMapping(value="export",method=RequestMethod.POST)
	public void export(@RequestParam(value="filter")String filter,HttpServletRequest request,HttpServletResponse response) throws IOException{
		ConnectionInfo connectionInfo = (ConnectionInfo) request.getSession().getAttribute("ConnectionInfo");
		response.setContentType("application/zip");
		response.addHeader("Content-Disposition", "attachment;filename=db.zip");
		if(connectionInfo != null){
			TableStructureFactory.createTableStructureService(connectionInfo.getDbType()).exportDbTables(connectionInfo, response.getOutputStream());
		}
	}
}
