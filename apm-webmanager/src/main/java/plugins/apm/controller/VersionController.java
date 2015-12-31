/**
 * VersionController.java.java
 * @author FengMy
 * @since 2015年12月10日
 */
package plugins.apm.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import org.springframework.web.multipart.MultipartFile;

import plugins.apm.entitys.Application;
import plugins.apm.entitys.Version;
import plugins.apm.enums.UpgradeStatusEnum;
import plugins.apm.service.ApplicationService;
import plugins.apm.service.VersionService;
import plugins.apm.tools.UpgradeContext;
import plugins.apm.tools.VersionUpgradeExecutor;
import plugins.installation.logs.MessageWriter;
import plugins.upgradekit.entitys.Version_;
import plugins.utils.CollectionUtils;
import plugins.utils.Pagination;
import plugins.utils.ResponseObject;
import plugins.utils.persistence.PrepareQueryHandler;
import plugins.utils.persistence.SimplePrepareQueryhandler;

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
		Application app = applicationService.findEntity(appId);
		model.put("app", app);
		return "version/list";
	}
	
	@RequestMapping(value="listData")
	@ResponseBody
	public Pagination<Version> listData(Pagination<Version> pagination,Version version) throws JsonGenerationException, JsonMappingException, IOException{
		logger.debug("查询版本数据:{}",new ObjectMapper().writeValueAsString(version));
		if(StringUtils.isEmpty(version.getSort_())){
			version.setSort_("createTime");
			version.setOrder_("desc");
		}
		versionService.findPagination(pagination, new SimplePrepareQueryhandler<Version>(version){
			@Override
			protected Predicate[] getWhereCondition(CriteriaBuilder cb,Version entity, Root<Version> root) {
				List<Predicate> predicates = new LinkedList<Predicate>();
				predicates.add(cb.equal(root.get(Version_.application), entity.getApplication()));
				return predicates.toArray(new Predicate[]{});
			}
		});
		return pagination;
	}
	
	@RequestMapping(value="toAdd")
	public String toAdd(@RequestParam(value="appId",required=true)String appId,ModelMap model){
		Application application = applicationService.findEntity(appId);
		model.put("app", application);
		return "version/add";
	}
	
	@RequestMapping(value="addSave")
	@ResponseBody
	public ResponseObject addSave(final Version version,
			@RequestParam(value = "versionFileUpload", required = true) MultipartFile versionFile,
			@RequestParam(value = "configFileUpload", required = false) MultipartFile configFile) throws IllegalStateException, IOException{
		ResponseObject rb = ResponseObject.newInstance().fail();
		Application application = applicationService.findEntity(version.getApplication().getId());
		version.setApplication(application);
		PrepareQueryHandler<Long> handler = new PrepareQueryHandler<Long>() {
			@Override
			public CriteriaQuery<Long> create(CriteriaBuilder cb) {
				CriteriaQuery<Long> query = cb.createQuery(Long.class);
				Root<Version> root = query.from(Version.class);
				query.where(cb.equal(root.get(Version_.application), version.getApplication()),cb.equal(root.get(Version_.number), version.getNumber()));
				return query;
			}
			
		};
		if(versionService.count(handler) > 0){
			rb.setMsg("版本号己存在");
		}else{
			String rootPath = UpgradeContext.getFileRoot();
			SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			File file = new File(rootPath + File.separator + "V" + sdf.format(new Date()) + versionFile.getOriginalFilename().substring(versionFile.getOriginalFilename().lastIndexOf(".")));
			versionFile.transferTo(file);
			version.setFile(file.getName());
			version.setFileName(versionFile.getOriginalFilename());
			if(!configFile.isEmpty()){
				file = new File(rootPath + File.separator + "P" + sdf.format(new Date()) + configFile.getOriginalFilename().substring(configFile.getOriginalFilename().lastIndexOf(".")));
				configFile.transferTo(file);
				version.setConfigFile(file.getName());
				version.setConfigFileName(configFile.getOriginalFilename());
			}
			version.setStatus(UpgradeStatusEnum.NOTDO);
			versionService.insert(version);
			rb.success();
		}
		logger.debug("新增版本数据:{}",new ObjectMapper().writeValueAsString(version));
		return rb;
	}
	
	@RequestMapping(value="toUpgrade")
	public String toUpgrade(@RequestParam(value="id",required=true)String id,ModelMap model){
		Version version = versionService.findEntity(id);
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
		Version version = versionService.findEntity(id);
		logger.debug("执行版本升级:{}",new ObjectMapper().writeValueAsString(version));
		final PrintWriter pw = response.getWriter();
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
		if(version != null && version.getApplication() != null){
			version.setUpgradeTime(new Date());
			VersionUpgradeExecutor.execute(version, writer);
			version.setStatus(UpgradeStatusEnum.SUCCESS);
			versionService.update(version, CollectionUtils.createSet(String.class, "status","upgradeTime"));
		}
		String msg = VersionUpgradeExecutor.messageScript("success",completeFunctionName);
		pw.write(msg);
		pw.flush();
	}
	
	@RequestMapping(value="confirmResult")
	@ResponseBody
	public ResponseObject confirmResult(Version version){
		ResponseObject rb = ResponseObject.newInstance().fail();
		if(StringUtils.isNotEmpty(version.getId()) && version.getStatus() != null){
			UpgradeStatusEnum status = version.getStatus();
			version = versionService.findEntity(version.getId());
			if(version != null){
				version.setStatus(status);
				versionService.update(version, CollectionUtils.createSet(String.class, "status"));
				rb.success();
			}else{
				rb.setMsg("版本不存在");
			}
		}else{
			rb.setMsg("参数异常");
		}
		return rb;
	}
}
