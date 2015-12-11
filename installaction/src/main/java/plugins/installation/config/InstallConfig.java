/**
 * InstallConfig.java.java
 * @author FengMy
 * @since 2015年6月1日
 */
package plugins.installation.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.execute.Execution;
import plugins.installation.execute.FileCopyExecution;
import plugins.installation.execute.FileEditExecution;
import plugins.installation.execute.FileUnZipExecution;
import plugins.installation.file.FileCopyInfo;
import plugins.installation.file.FileEditInfo;
import plugins.installation.file.FileEditInfo.FileEditItem;
import plugins.installation.file.FileUnZipInfo;

/**  
 * 功能描述：安装配置
 * 
 * @author FengMy
 * @since 2015年6月1日
 */
public class InstallConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(InstallConfig.class);
	
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
	
	private InstallConfig(){
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
		InstallConfig config = new InstallConfig();
		try {
			Document document = new SAXReader().read(inputstream);
			Element root = document.getRootElement();
			config.setName(root.attributeValue("name"));
			config.setTargetname(root.attributeValue("targetname"));
			
			List<Element> elements = root.elements();
			config.executions = new LinkedList<Execution>();
			if(elements != null){
				for(Element e : elements){
					if("unzip".equals(e.getName())){
						config.executions.add(new FileUnZipExecution(readFileUnzipInfo(e)));
					}else if("copy".equals(e.getName())){
						config.executions.add(new FileCopyExecution(readCopyInfo(e)));
					}else if("edit".equals(e.getName())){
						config.executions.add(new FileEditExecution(readEditInfo(e)));
					}
				}
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return config;
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
	
	private static FileUnZipInfo readFileUnzipInfo(Element e){
		FileUnZipInfo fi = new FileUnZipInfo();
		fi.setFile(e.attributeValue("file"));
		fi.setDesc(e.attributeValue("desc"));
		fi.setTo(e.attributeValue("to"));
		return fi;
	}
	
	/**
	 * 读取文件编辑列表
	 * @param ele
	 * @return
	 */
	private static FileEditInfo readEditInfo(Element e) {
		FileEditInfo fi = new FileEditInfo();
		fi.setFile(e.attributeValue("file"));
		fi.setItems(readEditItems(e));
		return fi;
	}
	
	/**
	 * 读取文件编辑项
	 * @param ele
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<FileEditItem> readEditItems(Element ele){
		List<FileEditItem> editItems = new LinkedList<FileEditItem>();
		List<Element> infoElements = ele.elements("item");
		if(infoElements != null){
			for(Element e : infoElements){
				FileEditItem item = new FileEditItem();
				item.setItemName(e.attributeValue("name"));
				item.setItemDesc(e.attributeValue("desc"));
				item.setItemValue(e.attributeValue("value"));
				editItems.add(item);
			}
		}
		return editItems;
	}

	/**
	 * 读取文件复制项
	 * @param ele
	 * @return
	 */
	private static FileCopyInfo readCopyInfo(Element e){
		FileCopyInfo info = new FileCopyInfo();
		info.setNodeText(e.asXML());
		if(e.attribute("desc") != null && e.attribute("desc").getText() != null){
			info.setDesc(e.attribute("desc").getText());
		}
		if(e.attribute("from") != null && e.attribute("from").getText() != null){
			info.setFrom(e.attribute("from").getText());
		}
		if(e.attribute("to") != null && e.attribute("to").getText() != null){
			info.setTo(e.attribute("to").getText());
		}
		return info;
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
