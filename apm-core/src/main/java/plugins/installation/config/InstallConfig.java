/**
 * InstallConfig.java.java
 * @author FengMy
 * @since 2015年6月1日
 */
package plugins.installation.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.execute.Execution;
import plugins.installation.execute.FileEditExecution;
import plugins.installation.execute.FileEditInfo;

/**  
 * 功能描述：安装配置
 * 
 * @author FengMy
 * @since 2015年6月1日
 */
public class InstallConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(InstallConfig.class);
	
	public Map<String,Class<? extends Execution>> executionClasses = new HashMap<String, Class<? extends Execution>>();
	
	/**
	 * install名称
	 */
	private String name;
	
	/**
	 * 目标文件夹
	 */
	private String target;
	
	/**
	 * 目标位置说明
	 */
	private String targetname;
	
	/**
	 * 执行集
	 */
	private List<Execution> executions;
	
	/**
	 * 上下文
	 */
	private Map<String,Object> context = new HashMap<String,Object>();
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		new InstallConfig();
	}
	
	@SuppressWarnings("unchecked")
	private InstallConfig() throws IOException, ClassNotFoundException{
		Enumeration<URL> configFiles = InstallConfig.class.getClassLoader().getResources("META-INF/executionClasses.properties");
		if(configFiles != null){
			while(configFiles.hasMoreElements()){
				URL url = configFiles.nextElement();
				InputStream is = url.openStream();
				Properties prop = new Properties();
				prop.load(is);
				Set<Object> keys = prop.keySet();
				if(keys != null){
					for(Object key : keys){
						if(key != null){
							executionClasses.put(key.toString().trim(), (Class<? extends Execution>) Class.forName(prop.getProperty(key.toString().trim()).trim()));
						}
					}
				}
			}
		}
	}

	/**
	 * 从配置文件加载
	 * @param configFile
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static InstallConfig loadFrom(File configFile) throws FileNotFoundException {
		logger.info("加载安装配置文件[{}]",configFile.getAbsolutePath());
		return loadFrom(new FileInputStream(configFile));
	}
	
	/**
	 * 从inputstream加载配置
	 * @param inputstream
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static InstallConfig loadFrom(InputStream inputstream){
		try {
			InstallConfig config = new InstallConfig();
			Document document = new SAXReader().read(inputstream);
			Element root = document.getRootElement();
			config.setName(root.attributeValue("name"));
			config.setTargetname(root.attributeValue("targetname"));
			List<Element> elements = root.elements();
			config.executions = new LinkedList<Execution>();
			if(elements != null){
				for(Element e : elements){
					String eleName = e.getName();
					if(config.executionClasses.containsKey(eleName)){
						Class<? extends Execution> clazz = config.executionClasses.get(eleName);
						Execution execution = clazz.newInstance();
						execution.load(e);
						config.executions.add(execution);
					}else{
						throw new RuntimeException("unable to analysis element : " + eleName + " to plugins.installation.execute.Execution,not register,see META-INF/executionClasses.properties.");
					}
				}
			}
			return config;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<FileEditInfo> getEditInfoList(){
		List<FileEditInfo> editInfos = new LinkedList<FileEditInfo>();
		for(Execution exec : executions){
			if(exec instanceof FileEditExecution){
				editInfos.add(((FileEditExecution)exec).getFileEditInfo());
			}
		}
		return editInfos;
	}
	
	/**
	 * 校验文件拷贝合法性
	 * @return
	 */
	public boolean validate(){
		logger.info("\n开始校验安装配置");
		boolean flag = true;
		if(target == null || "".equals(target)){
			logger.info("目标文件夹未配置(target)");
			flag = false;
		}else{
			File s = new File(target);
			if(!s.exists() || s.isFile()){
				logger.info("target 不存在或不是文件夹");
				flag = false;
			}else{
				logger.info("目标文件夹{} ok",target);
			}
		}
		logger.info("校验结束，结果为:{}",flag);
		return flag;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetname() {
		return targetname;
	}

	public void setTargetname(String targetname) {
		this.targetname = targetname;
	}
	
	public Map<String, Object> getContext() {
		return context;
	}

	public List<Execution> getExecutions() {
		return executions;
	}

	public void setExecutions(List<Execution> executions) {
		this.executions = executions;
	}
}
