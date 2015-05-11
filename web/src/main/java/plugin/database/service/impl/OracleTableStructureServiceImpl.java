/**
 * OracleTableStructureServiceImpl.java.java
 * @author FengMy
 * @since 2015年3月30日
 */
package plugin.database.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import plugin.database.domain.ConnectionInfo;
import plugin.database.domain.DbTable;
import plugin.database.domain.DbTableColumn;
import plugin.database.service.TableStructureService;
import plugin.database.util.DatabaseUtil;
import plugin.database.util.ZipUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年3月30日
 */
public class OracleTableStructureServiceImpl implements TableStructureService {
	private static final String SQL_TABLE_QUERY = " SELECT T.TABLE_NAME AS \"name\",TC.comments AS \"comments\" FROM USER_TABLES T LEFT JOIN USER_TAB_COMMENTS TC ON T.TABLE_NAME = TC.table_name ";
	private static final String SQL_TABLE_QUERY_FILTER = " WHERE ";
	private static final String SQL_OR = " OR ";
	private static final String SQL_TABLE_NAME_LIKE = " T.TABLE_NAME LIKE CONCAT(CONCAT('%',?),'%') ";
	private static final String SQL_TABLE_NAME_EQ = " T.TABLE_NAME = ? ";
	
	private static final String SQL_QUERY_DDL = "SELECT DBMS_METADATA.GET_DDL('TABLE',?) AS \"sql\" FROM DUAL";
	
	private static final String SQL_COLUMN_QUERY = "SELECT TC.COLUMN_NAME AS \"name\","
							+" TC.DATA_TYPE AS \"dataType\","
							+" TC.DATA_LENGTH AS \"dataLength\","
							+" TC.DATA_PRECISION AS \"precision\","
							+" TC.DATA_SCALE AS \"dataScale\","
							+" UCC.comments AS \"comments\","
							+" TC.NULLABLE AS \"nullAble\","
							+" CONS.PRIMARY AS \"primaryKey\" "
							+" FROM USER_TAB_COLS TC LEFT JOIN USER_COL_COMMENTS UCC "
							+" ON TC.TABLE_NAME = UCC.table_name AND TC.COLUMN_NAME = UCC.column_name "
							+" LEFT JOIN (SELECT CC.table_name,CC.column_name,'P' AS \"PRIMARY\" FROM user_cons_columns CC"
							+" LEFT JOIN USER_CONSTRAINTS UC "
							+" ON CC.constraint_name = UC.constraint_name"
							+" WHERE UC.constraint_type = 'P'"
							+" ) CONS"
							+" ON CONS.TABLE_NAME = TC.TABLE_NAME AND CONS.COLUMN_NAME = TC.COLUMN_NAME"
							+" WHERE TC.TABLE_NAME = ? ORDER BY CONS.PRIMARY DESC NULLS LAST,TC.COLUMN_NAME";

	@Override
	public List<DbTable> getDbTables(ConnectionInfo connectionInfo) {
		List<DbTable> dbTables = new LinkedList<DbTable>();
		Connection conn = DatabaseUtil.getConnection(connectionInfo);
		try {
			StringBuilder sql = new StringBuilder(SQL_TABLE_QUERY);
			String[] filters = null;
			if(StringUtils.isNotEmpty(connectionInfo.getFilter())){
				filters = connectionInfo.getFilter().split("\\s");
				List<String> filterList = new LinkedList<String>();
				for(String s : filters){
					if(s != null && StringUtils.isNotEmpty(s.trim())){
						filterList.add(s);
					}
				}
				if(filterList.size() > 0){
					filters = new String[filterList.size()];
					sql.append(SQL_TABLE_QUERY_FILTER);
					for(int i = 0;i < filters.length; i++){
						filters[i] = filterList.get(i);
						if(i > 0){
							sql.append(SQL_OR);
						}
						sql.append(SQL_TABLE_NAME_LIKE);
					}
				}else{
					filters = null;
				}
			}
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			if(filters != null && filters.length > 0){
				for(int i = 0;i < filters.length; i++){
					pstmt.setString(i + 1, filters[i]);
				}
			}
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				DbTable dbTable = new DbTable();
				dbTable.setName(rs.getString("name"));
				dbTable.setComments(rs.getString("comments"));
				dbTables.add(dbTable);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return dbTables;
	}
	
	@Override
	public DbTable getDbTableInfo(ConnectionInfo connectionInfo, String tableName) {
		Connection conn = DatabaseUtil.getConnection(connectionInfo);
		DbTable dbTable = null;
		try {
			dbTable = getDbTableInfo(conn, tableName);
		} finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dbTable;
	}
	
	private DbTable getDbTableInfo(Connection conn, String tableName) {
		
		DbTable dbTable = new DbTable();
		dbTable.setName(tableName);
		try {
			//查询表描述
			StringBuilder sql = new StringBuilder(SQL_TABLE_QUERY);
			sql.append(SQL_TABLE_QUERY_FILTER);
			sql.append(SQL_TABLE_NAME_EQ);
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, tableName);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				dbTable.setComments(rs.getString("comments"));
			}
			//查询建表语句
			pstmt = conn.prepareStatement(SQL_QUERY_DDL);
			pstmt.setString(1, tableName);
			rs = pstmt.executeQuery();
			while(rs.next()){
				dbTable.setSql(rs.getString("sql"));
			}
			//查询列信息
			sql = new StringBuilder(SQL_COLUMN_QUERY);
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, tableName);
			rs = pstmt.executeQuery();
			LinkedList<DbTableColumn> columns = new LinkedList<DbTableColumn>();
			while(rs.next()){
				DbTableColumn column = new DbTableColumn();
				column.setName(rs.getString("name"));
				column.setDataType(rs.getString("dataType"));
				column.setDataLength(rs.getString("dataLength"));
				column.setDataPrecision(rs.getString("precision"));
				column.setDataScale(rs.getString("dataScale"));
				column.setComments(rs.getString("comments"));
				column.setNullAble(rs.getString("nullAble"));
				column.setPrimaryKey(rs.getString("primaryKey"));
				columns.add(column);
			}
			dbTable.setColumns(columns);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return dbTable;
	}
	
	@Override
	public void exportDbTables(final ConnectionInfo connectionInfo, OutputStream outputstream) {
		try {
			final List<DbTable> dbTables = getDbTables(connectionInfo);
			ExecutorService executorService = Executors.newCachedThreadPool();
			final CountDownLatch latch = new CountDownLatch(dbTables.size());
			final BasicDataSource dataSource = DatabaseUtil.getDataSource(connectionInfo, 20);
			for(final DbTable dbTable : dbTables){
				executorService.execute(new Thread(){
					@Override
					public void run() {
						try {
							DbTable temp = OracleTableStructureServiceImpl.this.getDbTableInfo(dataSource.getConnection(), dbTable.getName());
							dbTable.setColumns(temp.getColumns());
							dbTable.setSql(temp.getSql());
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
						latch.countDown();
					}
				});
			}
			latch.await();
			dataSource.close();
			executorService.shutdown();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String rootDir = System.getProperty("user.dir") + "/tmp/" + sdf.format(new Date()) + "/table";
			File root = new File(rootDir);
			if(!root.exists()){
				root.mkdirs();
			}
		
			//拷贝样式文件
			FileUtils.copyDirectory(new File(System.getProperty("user.dir") + "/runtime_config/template/bootstrap"), new File(rootDir + "/bootstrap"));
			//生成列表
			generateListFile(dbTables, root);
			//生成单表页面
			for(DbTable dbTable : dbTables){
				generateDbTableFile(dbTable, root);
			}
			//打包输出
			ZipUtil.compress(rootDir, outputstream);
			root.delete();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 生成列表页面
	 * @param dbTables
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws TemplateException 
	 */
	private void generateListFile(List<DbTable> dbTables,File root) throws FileNotFoundException, IOException, TemplateException{
		Configuration config = new Configuration();
		String templateFile =  System.getProperty("user.dir") + "/runtime_config/template/index.ftl";
		Template template = new Template("index", new FileReader(templateFile),config);
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("dbTables", dbTables);
		String outputFile = root.getPath() + "/index.html";
		template.process(param, new FileWriter(outputFile));
	}
	
	/**
	 * 生成单个表格明细页面
	 * @param dbTable
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws TemplateException 
	 */
	private void generateDbTableFile(DbTable dbTable,File root) throws FileNotFoundException, IOException, TemplateException{
		Configuration config = new Configuration();
		String templateFile =  System.getProperty("user.dir") + "/runtime_config/template/table.ftl";
		Template template = new Template("tableInfo", new FileReader(templateFile),config);
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("dbTable", dbTable);
		File outputFile = new File(root.getPath() + "/tables/" + dbTable.getName() + ".html");
		if(!outputFile.getParentFile().exists()){
			outputFile.getParentFile().mkdirs();
		}
		template.process(param, new FileWriter(outputFile));
	}
	
}
