/**
 * VersionController.java.java
 * @author FengMy
 * @since 2015年12月10日
 */
package plugins.upgradekit.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import plugins.upgradekit.entitys.Application;
import plugins.upgradekit.entitys.Version;
import plugins.upgradekit.enums.UpgradeStatusEnum;
import plugins.upgradekit.service.ApplicationService;
import plugins.upgradekit.service.VersionService;
import plugins.upgradekit.tools.ApplicationUpgradeConfig;
import plugins.upgradekit.tools.MessageWriter;
import plugins.upgradekit.tools.NativeCommandExecutor;
import plugins.upgradekit.tools.UpgradeContext;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.App;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.Cmd;
import plugins.utils.CollectionUtils;
import plugins.utils.CreateQueryHandler;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月10日
 */
@Controller
@RequestMapping(value="version/*")
public class VersionController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(VersionController.class);
	
	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private VersionService versionService;
	
	@RequestMapping(value="list")
	public String list(@RequestParam(value="appId",required=true)String appId,ModelMap model){
		Application app = applicationService.getEntityById(appId);
		model.put("app", app);
		return "version/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<Version> listData(Pagination<Version> pagination,Version version){
		if(StringUtils.isEmpty(version.getSort_())){
			version.setSort_("createTime");
			version.setOrder_("desc");
		}
		versionService.findPagination(pagination, version);
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String toAdd(@RequestParam(value="appId",required=true)String appId,ModelMap model){
		Application application = applicationService.getEntityById(appId);
		model.put("app", application);
		return "version/add";
	}
	
	@RequestMapping(value="addSave")
	@ResponseBody
	public ResponseObject addSave(final Version version,
			@RequestParam(value = "versionFileUpload", required = true) MultipartFile versionFile,
			@RequestParam(value = "parameterFileUpload", required = false) MultipartFile parameterFile) throws IllegalStateException, IOException{
		ResponseObject rb = ResponseObject.newInstance().fail();
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
		if(versionService.countByQuery(handler) > 0){
			rb.setMsg("版本号己存在");
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
			version.setStatus(UpgradeStatusEnum.NOTDO);
			versionService.addEntity(version);
			rb.success();
		}
		return rb;
	}
	
	@RequestMapping(value="toUpgrade")
	public String toUpgrade(@RequestParam(value="id",required=true)String id,ModelMap model){
		Version version = versionService.getEntityById(id);
		model.put("version", version);
		return "version/upgrade";
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
			version.setUpgradeTime(new Date());
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
			version.setStatus(UpgradeStatusEnum.SUCCESS);
			versionService.updateEntity(version, CollectionUtils.createSet(String.class, "status","upgradeTime"));
		}
		String msg = msgScript("success",completeFunctionName);
		pw.write(msg);
		pw.flush();
	}
	
	@RequestMapping(value="confirmResult")
	@ResponseBody
	public ResponseObject confirmResult(Version version){
		ResponseObject rb = ResponseObject.newInstance().fail();
		if(StringUtils.isNotEmpty(version.getId()) && version.getStatus() != null){
			UpgradeStatusEnum status = version.getStatus();
			version = versionService.getEntityById(version.getId());
			if(version != null){
				version.setStatus(status);
				versionService.updateEntity(version, CollectionUtils.createSet(String.class, "status"));
				rb.success();
			}else{
				rb.setMsg("版本不存在");
			}
		}else{
			rb.setMsg("参数异常");
		}
		return rb;
	}
	
	private String msgScript(String msg,String functionName){
		try{
			msg = URLEncoder.encode(msg.trim(),"utf-8").replaceAll("\\+", "%20");
		}catch(Exception e){
			logger.error("转码异常:"+msg,e);
		}
		StringBuilder script = new StringBuilder("<script type=\"text/javascript\">");
		script.append("window.parent."+functionName+"(decodeURIComponent('"+msg+"'))");
		script.append("</script>");
		System.out.println(script.toString());
		return script.toString();
	}
}
