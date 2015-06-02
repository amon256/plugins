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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import plugins.installation.file.FileCopyInfo;
import plugins.installation.file.FileEditInfo;
import plugins.installation.file.FileEditInfo.FileEditItem;

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
	 * 源文件夹
	 */
	private String source;
	
	/**
	 * 目标文件夹
	 */
	private String target;
	
	/**
	 * 文件拷贝项
	 */
	private List<FileCopyInfo> fileCopyInfos;
	
	/**
	 * 文件编辑项
	 */
	private List<FileEditInfo> fileEditInfos;
	
	private InstallConfig(){
	}

	/**
	 * 从配置文件加载
	 * @param configFile
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static InstallConfig loadFrom(File configFile) throws FileNotFoundException {
		logger.debug("加载安装配置文件[{}]",configFile.getAbsolutePath());
		return loadFrom(new FileInputStream(configFile));
	}
	
	@SuppressWarnings("unchecked")
	public static InstallConfig loadFrom(InputStream inputstream){
		InstallConfig config = new InstallConfig();
		try {
			Document document = new SAXReader().read(inputstream);
			Element root = document.getRootElement();
			config.setName(root.attributeValue("name"));
			List<Element> fileCopys = root.elements("fileCopy");
			if(fileCopys != null){
				config.fileCopyInfos = new LinkedList<FileCopyInfo>();
				for(Element ele : fileCopys){
					config.fileCopyInfos.addAll(readCopyInfos(ele));
				}
			}
			
			List<Element> fileEdits = root.elements("fileEdit");
			if(fileEdits != null){
				config.fileEditInfos = new LinkedList<FileEditInfo>();
				for(Element ele : fileEdits){
					config.fileEditInfos.addAll(readEditInfos(ele));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return config;
	}
	
	@SuppressWarnings("unchecked")
	private static Collection<? extends FileEditInfo> readEditInfos(Element ele) {
		List<FileEditInfo> fileEditInfo = new LinkedList<FileEditInfo>();
		ele = ele.element("list");
		if(ele == null){
			return fileEditInfo;
		}
		List<Element> infoElements = ele.elements("editInfo");
		if(infoElements != null){
			for(Element e : infoElements){
				FileEditInfo fi = new FileEditInfo();
				fi.setFile(e.attributeValue("file"));
				fi.setItems(readEditItems(e));
				fileEditInfo.add(fi);
			}
		}
		return fileEditInfo;
	}
	
	@SuppressWarnings("unchecked")
	private static List<FileEditItem> readEditItems(Element ele){
		List<FileEditItem> editItems = new LinkedList<FileEditItem>();
		ele = ele.element("items");
		if(ele == null){
			return editItems;
		}
		List<Element> infoElements = ele.elements("item");
		if(infoElements != null){
			for(Element e : infoElements){
				FileEditItem item = new FileEditItem();
				item.setItemName(e.attributeValue("name"));
				item.setItemDesc(e.attributeValue("desc"));
				editItems.add(item);
			}
		}
		return editItems;
	}

	@SuppressWarnings("unchecked")
	private static List<FileCopyInfo> readCopyInfos(Element ele){
		List<FileCopyInfo> infos = new LinkedList<FileCopyInfo>();
		ele = ele.element("list");
		if(ele == null){
			return infos;
		}
		List<Element> infoElements = ele.elements("copyInfo");
		if(infoElements != null){
			for(Element e : infoElements){
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
				infos.add(info);
			}
		}
		return infos;
	}
	
	public boolean validateFileCopy(){
		logger.debug("\n开始校验安装配置");
		boolean flag = true;
		if(source == null || "".equals(source)){
			logger.debug("源文件夹未配置(source)");
			flag = false;
		}else{
			File s = new File(source);
			if(!s.exists() || s.isFile()){
				logger.debug("source 不存在或不是文件夹");
				flag = false;
			}else{
				logger.debug("源文件夹{} ok",source);
			}
		}
		if(target == null || "".equals(target)){
			logger.debug("目标文件夹未配置(target)");
			flag = false;
		}else{
			File s = new File(target);
			if(!s.exists() || s.isFile()){
				logger.debug("target 不存在或不是文件夹");
				flag = false;
			}else{
				logger.debug("目标文件夹{} ok",target);
			}
		}
		if(fileCopyInfos != null){
			for(FileCopyInfo info : fileCopyInfos){
				File src = null;
				if(info.getFrom() == null || "".equals(info.getFrom().trim())){
					logger.debug("源位置未配置");
					flag = false;
				}else{
					String path = info.getFrom().replace("${SOURCE}", source);
					src = new File(path);
					if(!src.exists()){
						logger.debug("源位置[{}]不存在",info.getFrom());
						flag = false;
					}
				}
				if(info.getTo() == null || "".equals(info.getTo().trim())){
					logger.debug("目标位置未配置");
					flag = false;
				}
			}
		}
		logger.debug("校验结束，结果为:{}",flag);
		return flag;
	}
	
	public List<FileCopyInfo> getFileCopyInfos() {
		return fileCopyInfos;
	}

	public List<FileEditInfo> getFileEditInfos() {
		return fileEditInfos;
	}

	public void setFileEditInfos(List<FileEditInfo> fileEditInfos) {
		this.fileEditInfos = fileEditInfos;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setSource(String source) {
		this.source = source;
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
}
