/**
 * ApplicationUpgradeConfig.java.java
 * @author FengMy
 * @since 2015年12月9日
 */
package plugins.apm.tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月9日
 */
public class ApplicationUpgradeConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationUpgradeConfig.class);
	
	private String charset = System.getProperty("file.encoding");
	
	private Map<String,App> appMap = new HashMap<String, ApplicationUpgradeConfig.App>();
	
	private ApplicationUpgradeConfig(File file) throws Exception{
		readConfig(file);
	}
	
	/**
	 * default
	 * System.getProperty("user.dir") + File.separator + "UpgradeConfig.xml"
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
		<applications charset="gbk">
			<application>
				<id>NDMP_WEB</id>
				<name>网络局数据前台WEB</name>
				<commands>
					<command>
						<cmd>G:\source\install.bat</cmd>
						<params>
							<param>${version.file}</param>
							<param>${version.parameterFile}</param>
						</params>
					</command>
				</commands>
			</application>
		</applications>
	 * 
	 * 
	 * @return
	 */
	public static ApplicationUpgradeConfig getInstance(){
		return ApplicationUpgradeConfig.getInstance(System.getProperty("user.dir") + File.separator + "apm.xml");
	}
	
	/**
	 * 指定配置文件位置
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
		<applications charset="gbk">
			<application>
				<id>NDMP_WEB</id>
				<name>网络局数据前台WEB</name>
				<commands>
					<command>
						<cmd>G:\source\install.bat</cmd>
						<params>
							<param>${version.file}</param>
							<param>${version.parameterFile}</param>
						</params>
					</command>
				</commands>
			</application>
		</applications>
	 * 
	 * 
	 * @param configFilePath
	 * @return
	 */
	public static ApplicationUpgradeConfig getInstance(String configFilePath){
		logger.info("读取配置文件:{}",configFilePath);
		File file = new File(configFilePath);
		if(!file.exists() || file.isDirectory()){
			throw new RuntimeException(configFilePath + " 不存在或者是文件夹");
		}
		try{
			ApplicationUpgradeConfig config = new ApplicationUpgradeConfig(file);
			return config;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readConfig(File file) throws Exception{
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new FileInputStream(file));
		Element root = doc.getRootElement();
		if(root.getAttributeValue("charset") != null){
			charset = root.getAttributeValue("charset").trim();
		}
		List<Element> children = root.getChildren();
		if(children != null && !children.isEmpty()){
			for(Element ele : children){
				App app = null;
				if("application".equals(ele.getName())){
					app = readApp(ele);
					appMap.put(app.getId(), app);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private App readApp(Element element){
		App app = new App();
		app.setId(element.getChildTextTrim("id"));
		app.setName(element.getChildTextTrim("name"));
		app.setAppRoot(element.getChildTextTrim("appRoot"));
		app.setConfigRoot(element.getChildTextTrim("configRoot"));
		Element cmdsElement = element.getChild("commands");
		if(cmdsElement != null){
			List<Element> children = cmdsElement.getChildren();
			if(children != null && !children.isEmpty()){
				List<Cmd> cmdList = new LinkedList<ApplicationUpgradeConfig.Cmd>();
				for(Element ele : children){
					Cmd cmd = null;
					if("command".equals(ele.getName())){
						cmd = readCmd(ele);
						cmdList.add(cmd);
					}
				}
				app.setInstallCmds(cmdList);
			}
		}
		//应用状态
		Element statusCmd = element.getChild("statusCmd");
		if(statusCmd != null){
			StatusCmd cmd = readStatusCmd(statusCmd);
			app.setStatusCmd(cmd);
		}
		//启动命令
		Element startCmd = element.getChild("startCmd");
		if(startCmd != null){
			Cmd cmd = readCmd(startCmd);
			app.setStartCmd(cmd);
		}
		//关闭命令
		Element stopCmd = element.getChild("stopCmd");
		if(stopCmd != null){
			Cmd cmd = readCmd(stopCmd);
			app.setStopCmd(cmd);
		}
		logger.info("读取App,id:{},name:{}",app.getId(),app.getName());
		return app;
	}
	
	private StatusCmd readStatusCmd(Element element){
		StatusCmd cmd = new StatusCmd();
		cmd.setCmd(element.getChildTextTrim("cmd"));
		cmd.setIncludeValue(element.getChildTextTrim("include"));
		return cmd;
	}
	
	@SuppressWarnings("unchecked")
	private Cmd readCmd(Element element){
		Cmd cmd = new Cmd();
		cmd.setPath(element.getChildTextTrim("path"));
		cmd.setCmd(element.getChildTextTrim("cmd"));
		element = element.getChild("params");
		if(element != null){
			List<Element> children = element.getChildren();
			if(children != null && !children.isEmpty()){
				List<Param> paramList = new LinkedList<Param>();
				for(Element ele : children){
					if("param".equals(ele.getName())){
						paramList.add(readParam(ele));
					}
				}
				cmd.setParams(paramList);
			}
		}
		logger.info("读取Cmd,cmd:{},params:{}",cmd.getCmd(),cmd.getParams());
		return cmd;
	}
	
	private Param readParam(Element element){
		Param param = new Param();
		param.setName(element.getChildTextTrim("name"));
		param.setValue(element.getChildTextTrim("value"));
		return param;
	}
	
	public App getApp(String id){
		return appMap.get(id);
	}
	
	public String getCharset() {
		return charset;
	}
	
	public static class App{
		private String id;
		private String name;
		private String appRoot;
		private String configRoot;
		private StatusCmd statusCmd;
		private Cmd startCmd;
		private Cmd stopCmd;
		private List<Cmd> installCmds;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<Cmd> getInstallCmds() {
			return installCmds;
		}
		public void setInstallCmds(List<Cmd> cmds) {
			this.installCmds = cmds;
		}
		public StatusCmd getStatusCmd() {
			return statusCmd;
		}
		public void setStatusCmd(StatusCmd statusCmd) {
			this.statusCmd = statusCmd;
		}
		public Cmd getStartCmd() {
			return startCmd;
		}
		public void setStartCmd(Cmd startCmd) {
			this.startCmd = startCmd;
		}
		public Cmd getStopCmd() {
			return stopCmd;
		}
		public void setStopCmd(Cmd stopCmd) {
			this.stopCmd = stopCmd;
		}
		public String getAppRoot() {
			return appRoot;
		}
		public void setAppRoot(String appRoot) {
			this.appRoot = appRoot;
		}
		public String getConfigRoot() {
			return configRoot;
		}
		public void setConfigRoot(String configRoot) {
			this.configRoot = configRoot;
		}
	}
	
	public static class StatusCmd{
		private String cmd;
		private String includeValue;
		public String getCmd() {
			return cmd;
		}
		public void setCmd(String cmd) {
			this.cmd = cmd;
		}
		public String getIncludeValue() {
			return includeValue;
		}
		public void setIncludeValue(String includeValue) {
			this.includeValue = includeValue;
		}
	}
	
	public static class Cmd{
		private String path;
		private String cmd;
		private List<Param> params;
		public String getCmd() {
			return cmd;
		}
		public void setCmd(String cmd) {
			this.cmd = cmd;
		}
		public List<Param> getParams() {
			return params;
		}
		public void setParams(List<Param> params) {
			this.params = params;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
	}
	
	public static class Param{
		private String name;
		private String value;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}

}
