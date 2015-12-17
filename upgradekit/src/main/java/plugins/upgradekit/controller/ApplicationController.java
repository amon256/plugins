/**
 * ApplicationController.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import plugins.upgradekit.entitys.Application;
import plugins.upgradekit.service.ApplicationService;
import plugins.upgradekit.tools.ApplicationUpgradeConfig;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.App;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.Cmd;
import plugins.upgradekit.tools.MessageWriter;
import plugins.upgradekit.tools.NativeCommandExecutor;
import plugins.upgradekit.tools.VersionUpgradeExecutor;
import plugins.utils.CreateQueryHandler;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
@Controller
@RequestMapping(value="application/*")
public class ApplicationController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationService applicationService;
	
	@RequestMapping(value="list")
	public String list(){
		return "application/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<Application> listData(Pagination<Application> pagination,Application application) throws JsonGenerationException, JsonMappingException, IOException{
		logger.debug("查询应用数据:{}",new ObjectMapper().writeValueAsString(application));
		applicationService.findPagination(pagination, application);
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String toAdd(ModelMap model){
		return "application/add";
	}
	
	@RequestMapping(value="addSave")
	@ResponseBody
	public ResponseObject addSave(final Application application) throws JsonGenerationException, JsonMappingException, IOException{
		logger.debug("新增应用数据:{}",new ObjectMapper().writeValueAsString(application));
		ResponseObject rb = ResponseObject.newInstance().fail();
		if(StringUtils.isEmpty(application.getName())){
			rb.setMsg("应用名不能为空");
		}else if(StringUtils.isEmpty(application.getNumber())){
			rb.setMsg("应用编码不能为空");
		}else{
			CreateQueryHandler<Long> handler = new CreateQueryHandler<Long>() {
				@Override
				public CriteriaQuery<Long> create(CriteriaBuilder cb) {
					CriteriaQuery<Long> query = cb.createQuery(Long.class);
					Root<Application> root = query.from(Application.class);
					query.where(cb.equal(root.get("number"), application.getNumber()));
					return query;
				}
			};
			if(applicationService.countByQuery(handler) > 0){
				rb.setMsg("应用编码己存在");
			}else{
				applicationService.addEntity(application);
				rb.success();
			}
		}
		return rb;
	}
	
	@RequestMapping(value="checkStatus")
	@ResponseBody
	public ResponseObject checkStatus(@RequestParam(value="id",required=true)String id){
		ResponseObject rb = ResponseObject.newInstance().success();
		Application application = applicationService.getEntityById(id);
		rb.put("id", id);
		if(application != null){
			rb.put("appStatus", "running");
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && app.getStatusCmd() != null){
				final StringBuilder result = new StringBuilder();
				NativeCommandExecutor.executeNativeCommand(new MessageWriter() {
					@Override
					public void write(String message) {
						result.append(message).append("\n");
					}
				}, config.getCharset(), app.getStatusCmd().getCmd(), new String[]{},null,10000);
				if(result.indexOf(app.getStatusCmd().getIncludeValue()) >= 0){
					rb.put("appStatus", "running");
				}else{
					rb.put("appStatus", "stop");
				}
			}else{
				rb.fail();
				rb.setMsg("应用状态检查命令未配置");
			}
		}else{
			rb.fail();
			rb.setMsg("应用不存在");
		}
		return rb;
	}
	
	@RequestMapping(value="start")
	@ResponseBody
	public void start(@RequestParam(value="id",required=true)String id,
			@RequestParam(value="msgFunctionName",required=true)final String msgFunctionName,
			@RequestParam(value="completeFunctionName",required=true)final String completeFunctionName,
			HttpServletResponse response) throws IOException{
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-type", "text/html;charset=UTF-8");  
		response.setCharacterEncoding("UTF-8");
		final PrintWriter pw = response.getWriter();
		Application application = applicationService.getEntityById(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && app.getStartCmd() != null){
				Cmd cmd = app.getStartCmd();
				String[] params = new String[0];
				if(cmd.getParams() != null && !cmd.getParams().isEmpty()){
					params = new String[cmd.getParams().size()];
					for(int i = 0; i < cmd.getParams().size(); i++){
						params[i] = cmd.getParams().get(i).getName() + "=" + cmd.getParams().get(i).getValue();
					}
				}
				MessageWriter writer = new MessageWriter() {
					@Override
					public void write(String message) {
						String script = VersionUpgradeExecutor.messageScript(message, msgFunctionName);
						pw.write(script);
						pw.flush();
					}
				};
				//默认5分钟超时
				NativeCommandExecutor.executeNativeCommand(writer, config.getCharset()	, cmd.getCmd(), params,new File(cmd.getPath()),1000*300);
				pw.write(VersionUpgradeExecutor.messageScript("启动命令执行完成",completeFunctionName));
			}else{
				pw.write(VersionUpgradeExecutor.messageScript("应用启用命令未配置",completeFunctionName));
			}
		}else{
			pw.write(VersionUpgradeExecutor.messageScript("应用不存在",completeFunctionName));
		}
		pw.flush();
	}
	
	@RequestMapping(value="stop")
	@ResponseBody
	public void stop(@RequestParam(value="id",required=true)String id,
			@RequestParam(value="msgFunctionName",required=true)final String msgFunctionName,
			@RequestParam(value="completeFunctionName",required=true)final String completeFunctionName,
			HttpServletResponse response) throws IOException{
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-type", "text/html;charset=UTF-8");  
		response.setCharacterEncoding("UTF-8");
		final PrintWriter pw = response.getWriter();
		Application application = applicationService.getEntityById(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && app.getStopCmd() != null){
				Cmd cmd = app.getStopCmd();
				String[] params = new String[0];
				if(cmd.getParams() != null && !cmd.getParams().isEmpty()){
					params = new String[cmd.getParams().size()];
					for(int i = 0; i < cmd.getParams().size(); i++){
						params[i] = cmd.getParams().get(i).getName() + "=" + cmd.getParams().get(i).getValue();
					}
				}
				MessageWriter writer = new MessageWriter() {
					@Override
					public void write(String message) {
						String script = VersionUpgradeExecutor.messageScript(message, msgFunctionName);
						pw.write(script);
						pw.flush();
					}
				};
				//默认5分钟超时
				NativeCommandExecutor.executeNativeCommand(writer, config.getCharset()	, cmd.getCmd(), params,new File(cmd.getPath()),1000*300);
				pw.write(VersionUpgradeExecutor.messageScript("关闭命令执行完成",completeFunctionName));
			}else{
				pw.write(VersionUpgradeExecutor.messageScript("应用关闭命令未配置",completeFunctionName));
			}
		}else{
			pw.write(VersionUpgradeExecutor.messageScript("应用不存在",completeFunctionName));
		}
		pw.flush();
	}
	
	@RequestMapping(value="toConfigFiles")
	public String toConfigFiles(@RequestParam(value="id",required=true)String id,ModelMap model){
		Application application = applicationService.getEntityById(id);
		model.put("application", application);
		return "application/configFiles";
	}
	
	@RequestMapping(value="configFiles")
	@ResponseBody
	public ResponseObject configFiles(@RequestParam(value="id",required=true)String id){
		ResponseObject rb = ResponseObject.newInstance().success();
		Application application = applicationService.getEntityById(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && StringUtils.isNotEmpty(app.getRootPath())){
				File root = new File(app.getRootPath());
				if(root.exists() && root.isDirectory()){
					Map<String,Object> map = new HashMap<String, Object>();
					map.put("name", root.getName());
					map.put("path", root.getAbsolutePath());
					map.put("isParent", true);
					map.put("children", listFiles(root));
					rb.put("datas", new Object[]{map});
				}else{
					rb.fail();
					rb.setMsg("应用根路径配置错误");
				}
			}else{
				rb.fail();
				rb.setMsg("应用根路径未配置");
			}
		}else{
			rb.fail();
			rb.setMsg("应用不存在");
		}
		return rb;
	}
	
	private List<Map<String, Object>> listFiles(File root){
		File[] files = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() 
						|| file.getName().endsWith(".xml") 
						|| file.getName().endsWith(".properties")
						|| file.getName().endsWith(".txt")
						|| file.getName().endsWith(".json")
						|| file.getName().endsWith(".svg")
						|| file.getName().endsWith(".vml")
						|| file.getName().endsWith(".html")
						|| file.getName().endsWith(".css")
						|| file.getName().endsWith(".js");
			}
		});
		if(files != null && files.length > 0){
			List<Map<String, Object>> fileList = new ArrayList<Map<String,Object>>(files.length);
			for(File file : files){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name", file.getName());
				map.put("path", file.getAbsolutePath());
				map.put("isParent", false);
				if(file.isDirectory()){
					List<Map<String, Object>> children = listFiles(file);
					if(children != null && !children.isEmpty()){
						map.put("isParent", true);
						map.put("children", children);
					}
				}else{
					map.put("file", true);
				}
				fileList.add(map);
			}
			return fileList;
		}
		return null;
	}
	
	@RequestMapping(value="fileContent")
	@ResponseBody
	public ResponseObject fileContent(@RequestParam(value="id",required=true)String id,@RequestParam(value="filePath",required=true)String filePath) throws Exception{
		ResponseObject rb = ResponseObject.newInstance().fail();
		Application application = applicationService.getEntityById(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && StringUtils.isNotEmpty(app.getRootPath())){
				File root = new File(app.getRootPath());
				if(root.exists() && root.isDirectory()){
					File file = new File(filePath);
					if(file.exists()){
						if(file.getAbsolutePath().startsWith(root.getAbsolutePath())){
							if(file.length() <= 1024*1024){
								StringBuilder content = new StringBuilder();
								BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
								String line = null;
								while((line = br.readLine()) != null){
									content.append(line).append("\n");
								}
								rb.success();
								rb.put("content", content.toString());
								br.close();
							}else{
								rb.setMsg("文件大于1M，请使用ftp下载进行查看");
							}
						}else{
							rb.setMsg("文件在允许查看的范围外");
						}
					}else{
						rb.setMsg("文件不存在");
					}
				}else{
					rb.setMsg("应用根路径配置错误");
				}
			}else{
				rb.setMsg("应用根路径未配置");
			}
		}else{
			rb.setMsg("应用不存在");
		}
		return rb;
	}
	
	@RequestMapping(value="saveFile")
	@ResponseBody
	public ResponseObject saveFile(@RequestParam(value="id",required=true)String id,
			@RequestParam(value="filePath",required=true)String filePath,
			@RequestParam(value="fileContent",required=true)String fileContent) throws Exception{
		ResponseObject rb = ResponseObject.newInstance().fail();
		Application application = applicationService.getEntityById(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && StringUtils.isNotEmpty(app.getRootPath())){
				File root = new File(app.getRootPath());
				if(root.exists() && root.isDirectory()){
					File file = new File(filePath);
					if(file.exists()){
						if(file.getAbsolutePath().startsWith(root.getAbsolutePath())){
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
							bw.write(fileContent);
							bw.flush();
							bw.close();
							rb.success();
							rb.setMsg("保存成功");
						}else{
							rb.setMsg("文件在允许查看的范围外");
						}
					}else{
						rb.setMsg("文件不存在");
					}
				}else{
					rb.setMsg("应用根路径配置错误");
				}
			}else{
				rb.setMsg("应用根路径未配置");
			}
		}else{
			rb.setMsg("应用不存在");
		}
		return rb;
	}
}
