/**
 * ApplicationController.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.apm.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
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

import plugins.apm.entitys.Application;
import plugins.apm.service.ApplicationService;
import plugins.apm.tools.ApplicationUpgradeConfig;
import plugins.apm.tools.NativeCommandExecutor;
import plugins.apm.tools.VersionUpgradeExecutor;
import plugins.apm.tools.ApplicationUpgradeConfig.App;
import plugins.apm.tools.ApplicationUpgradeConfig.Cmd;
import plugins.installation.logs.MessageWriter;
import plugins.upgradekit.entitys.Application_;
import plugins.utils.CollectionUtils;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;
import plugins.utils.persistence.SimplePrepareQueryhandler;
import plugins.validation.LengRangeValidationRule;
import plugins.validation.RegexpValidationRule;
import plugins.validation.RequiredValidationRule;
import plugins.validation.Validation;
import plugins.validation.ValidationResult;
import plugins.validation.ValidationRule;
import plugins.validation.ValidationUtil;

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
		applicationService.findPagination(pagination, new SimplePrepareQueryhandler<Application>(application){
			@Override
			protected Predicate[] getWhereCondition(CriteriaBuilder cb,
					Application entity, Root<Application> root) {
				if(StringUtils.isNotEmpty(entity.getKeyword())){
					return new Predicate[]{
							cb.or(
									cb.like(root.get(Application_.name)   , "%"+entity.getKeyword()+"%"),
									cb.like(root.get(Application_.number), "%"+entity.getKeyword()+"%"),
									cb.like(root.get(Application_.description) , "%"+entity.getKeyword()+"%"))
					};
				}
				return null;
			}
		});
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
		//必填和格式校验
		List<ValidationResult> results = ValidationUtil.validate(application,
				new Validation("name", "名称", 
						new RequiredValidationRule(),
						new LengRangeValidationRule(2, 10)
				),
				new Validation("number", "编号", 
						new RequiredValidationRule(),
						new LengRangeValidationRule(4, 20),
						new RegexpValidationRule("[_@$.0-9a-zA-Z]+"),
						new ExistsApplicationValidationRule(application)
				)
		);
		if(results != null && !results.isEmpty()){
			rb.put("validateErrors", results);
			rb.put("msg", "数据校验不通过");
		}else{
			applicationService.insert(application);
			rb.success();
		}
		return rb;
	}
	
	@RequestMapping(value="toEdit")
	public String toEdit(@RequestParam(value="id")String id,ModelMap model){
		Application application = applicationService.findEntity(id);
		model.put("application", application);
		return "application/edit";
	}
	
	@RequestMapping(value="editSave")
	@ResponseBody
	public ResponseObject editSave(Application application){
		ResponseObject rb = ResponseObject.newInstance().fail();
		//必填和格式校验
		List<ValidationResult> results = ValidationUtil.validate(application,
				new Validation("name", "名称", 
						new RequiredValidationRule(),
						new LengRangeValidationRule(2, 10)
				),
				new Validation("number", "编号", 
						new RequiredValidationRule(),
						new LengRangeValidationRule(4, 20),
						new RegexpValidationRule("[_@$.0-9a-zA-Z]+"),
						new ExistsApplicationValidationRule(application)
				)
		);
		if(results != null && !results.isEmpty()){
			rb.put("validateErrors", results);
			rb.put("msg", "数据校验不通过");
		}else{
			applicationService.update(application, CollectionUtils.createSet(String.class, "name","number","description"));
			rb.success();
		}
		return rb;
	}
	
	@RequestMapping(value="checkStatus")
	@ResponseBody
	public ResponseObject checkStatus(@RequestParam(value="id",required=true)String id){
		ResponseObject rb = ResponseObject.newInstance().success();
		Application application = applicationService.findEntity(id);
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

					@Override
					public boolean isAvailable() {
						return result != null;
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
		Application application = applicationService.findEntity(id);
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
					
					@Override
					public boolean isAvailable() {
						return pw != null;
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
		Application application = applicationService.findEntity(id);
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

					@Override
					public boolean isAvailable() {
						return pw != null;
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
		Application application = applicationService.findEntity(id);
		model.put("application", application);
		return "application/configFiles";
	}
	
	@RequestMapping(value="configFiles")
	@ResponseBody
	public ResponseObject configFiles(@RequestParam(value="id",required=true)String id){
		ResponseObject rb = ResponseObject.newInstance().success();
		Application application = applicationService.findEntity(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && StringUtils.isNotEmpty(app.getConfigRoot())){
				File root = new File(app.getConfigRoot());
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
	public ResponseObject fileContent(@RequestParam(value="id",required=true)String id,
			@RequestParam(value="filePath",required=true)String filePath,
			@RequestParam(value="charset",required=false,defaultValue="utf-8")String charset) throws Exception{
		ResponseObject rb = ResponseObject.newInstance().fail();
		rb.put("charset", charset);
		Application application = applicationService.findEntity(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && StringUtils.isNotEmpty(app.getConfigRoot())){
				File root = new File(app.getConfigRoot());
				if(root.exists() && root.isDirectory()){
					File file = new File(filePath);
					if(file.exists()){
						if(file.getAbsolutePath().startsWith(root.getAbsolutePath())){
							if(file.length() <= 1024*1024){
								StringBuilder content = new StringBuilder();
								BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
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
			@RequestParam(value="fileContent",required=true)String fileContent,
			@RequestParam(value="charset",required=false,defaultValue="utf-8")String charset) throws Exception{
		ResponseObject rb = ResponseObject.newInstance().fail();
		Application application = applicationService.findEntity(id);
		if(application != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(application.getNumber());
			if(app != null && StringUtils.isNotEmpty(app.getConfigRoot())){
				File root = new File(app.getConfigRoot());
				if(root.exists() && root.isDirectory()){
					File file = new File(filePath);
					if(file.exists()){
						if(file.getAbsolutePath().startsWith(root.getAbsolutePath())){
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
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
	
	private class ExistsApplicationValidationRule implements ValidationRule{
		private Application self;
		ExistsApplicationValidationRule(Application self){
			this.self = self;
		}
		@Override
		public String type() {
			return "exists";
		}
		@Override
		public boolean validate(Object bean, final Object value) {
			if(value == null){
				return true;
			}
			if(value instanceof String){
				if(StringUtils.isEmpty(value.toString().trim())){
					return true;
				}
			}
			List<Application> existsList = applicationService.find(new SimplePrepareQueryhandler<Application>((Application) bean){
				@Override
				protected Predicate[] getWhereCondition(CriteriaBuilder cb,
						Application entity, Root<Application> root) {
					return new Predicate[]{cb.equal(root.get(Application_.number), entity.getNumber())};
				}
			});
			if(self != null && StringUtils.isNotEmpty(self.getId())){
				if(existsList != null && !existsList.isEmpty()){
					for(Application au : existsList){
						if(StringUtils.equals(self.getId(), au.getId())){
							return true;
						}
					}
				}
			}
			return existsList == null || existsList.isEmpty();
		}
		@Override
		public String getErrorMessage() {
			return "己存在";
		}
	}
}
