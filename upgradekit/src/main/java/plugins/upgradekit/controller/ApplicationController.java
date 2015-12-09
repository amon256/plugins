/**
 * ApplicationController.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.upgradekit.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.VariableMapper;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import plugins.upgradekit.entitys.Application;
import plugins.upgradekit.entitys.Version;
import plugins.upgradekit.service.ApplicationService;
import plugins.upgradekit.service.VersionService;
import plugins.upgradekit.tools.ApplicationUpgradeConfig;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.App;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.Cmd;
import plugins.upgradekit.tools.MessageWriter;
import plugins.upgradekit.tools.NativeCommandExecutor;
import plugins.upgradekit.tools.UpgradeContext;
import plugins.utils.CreateQueryHandler;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
@Controller
@RequestMapping(value="application/*")
public class ApplicationController extends BaseController {

	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private VersionService versionService;
	
	@RequestMapping(value="list")
	public String list(){
		return "application/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<Application> listData(Pagination<Application> pagination,Application application){
		applicationService.findPagination(pagination, application);
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String toAdd(ModelMap model){
		return "application/add";
	}
	
	@RequestMapping(value="addSave")
	@ResponseBody
	public ResponseObject addSave(final Application application){
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
	
	@RequestMapping(value="toUpgrade")
	public String toUpgrade(@RequestParam(value="id",required=true)String id,ModelMap model){
		Application application = applicationService.getEntityById(id);
		model.put("app", application);
		return "application/upgrade";
	}
	
	@RequestMapping(value="upgrade")
	@ResponseBody
	public ResponseObject upgrade(
						final Version version,
						@RequestParam(value = "versionFileUpload", required = true) MultipartFile versionFile,
						@RequestParam(value = "parameterFileUpload", required = false) MultipartFile parameterFile,
						HttpServletResponse response) throws IllegalStateException, IOException{
		Application application = applicationService.getEntityById(version.getApplication().getId());
		version.setApplication(application);
		CreateQueryHandler<Long> handler = new CreateQueryHandler<Long>() {
			@Override
			public CriteriaQuery<Long> create(CriteriaBuilder cb) {
				CriteriaQuery<Long> query = cb.createQuery(Long.class);
				Root<Version> root = query.from(Version.class);
				query.where(cb.equal(root.get("number"), version.getNumber()),cb.equal(root.get("application").get("id"), version.getApplication().getId()));
				return query;
			}
		};
		ResponseObject rb = ResponseObject.newInstance().fail();
		if(versionService.countByQuery(handler) > 0){
			rb.setMsg("应用编码己存在");
		}else{
			String rootPath = UpgradeContext.getFileRoot();
			SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			File file = new File(rootPath + File.separator + "V" + sdf.format(new Date()) + versionFile.getOriginalFilename().substring(versionFile.getOriginalFilename().lastIndexOf(".")));
			versionFile.transferTo(file);
			version.setFile(file.getName());
			version.setFileName(versionFile.getOriginalFilename());
			if(!parameterFile.isEmpty()){
				file = new File(rootPath + File.separator + "P" + sdf.format(new Date()) + parameterFile.getOriginalFilename().substring(parameterFile.getOriginalFilename().lastIndexOf(".")));
				parameterFile.transferTo(file);
				version.setParameterFile(file.getName());
				version.setParameterFileName(parameterFile.getOriginalFilename());
			}
			versionService.addEntity(version);
			rb.put("version", version);
			rb.success();
		}
		return rb;
	}
	
	@RequestMapping(value="executeVersionUpdate")
	public void executeVersionUpdate(@RequestParam(value="id")String id,
			@RequestParam(value="msgFunctionName",required=true)final String msgFunctionName,
			@RequestParam(value="completeFunctionName",required=true)final String completeFunctionName,
			HttpServletResponse response) throws IOException, InterruptedException{
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-type", "text/html;charset=UTF-8");  
		response.setCharacterEncoding("UTF-8");
		Version version = versionService.getEntityById(id);
		final PrintWriter pw = response.getWriter();
		MessageWriter writer = new MessageWriter() {
			@Override
			public void write(String message) {
				String script = msgScript(message, msgFunctionName);
				pw.write(script);
				pw.flush();
			}
		};
		if(version != null && version.getApplication() != null){
			ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
			App app = config.getApp(version.getApplication().getNumber());
			if(app != null && app.getCmds() != null){
				ExpressionFactory elFactory = new ExpressionFactoryImpl();
				ELContext elCtx = new SimpleContext();
				VariableMapper variableMapper = elCtx.getVariableMapper();
				variableMapper.setVariable("version", elFactory.createValueExpression(version, Version.class));
				for(Cmd cmd : app.getCmds()){
					String c = cmd.getCmd();
					String[] params = new String[0];
					if(cmd.getParams() != null && !cmd.getParams().isEmpty()){
						params = new String[cmd.getParams().size()];
						for(int i = 0; i < cmd.getParams().size(); i++){
							params[i] = cmd.getParams().get(i).getName() + "=" + (String) elFactory.createValueExpression(elCtx, cmd.getParams().get(i).getValue(), String.class).getValue(elCtx);
						}
					}
					NativeCommandExecutor.executeNativeCommand(writer, config.getCharset()	, c, params);
				}
			}
		}
		String msg = msgScript("success",completeFunctionName);
		pw.write(msg);
		pw.flush();
	}
	
	private String msgScript(String msg,String functionName){
		StringBuilder script = new StringBuilder("<script type=\"text/javascript\">");
		script.append("window.parent."+functionName+"('"+msg.trim()+"')");
		script.append("</script>");
		System.out.println(script.toString());
		return script.toString();
	}
}
