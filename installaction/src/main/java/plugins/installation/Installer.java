/**
 * Installer.java.java
 * @author FengMy
 * @since 2015年6月1日
 */
package plugins.installation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.file.FileCopyInfo;
import plugins.installation.file.FileEditInfo;
import plugins.installation.file.FileEditInfo.FileEditItem;
import plugins.installation.file.FileUtils;

/**  
 * 功能描述：文件安装程序
 * 
 * @author FengMy
 * @since 2015年6月1日
 */
public class Installer {
	
	private static Logger logger = LoggerFactory.getLogger(Installer.class);

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String configPath = null;
		if(args != null && args.length > 0){
			configPath = args[0];
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(configPath == null){
			configPath = readConfigPath(br);
		}
		InstallConfig config = InstallConfig.loadFrom(new File(configPath));
		logger.debug("*************{}****************",config.getName());
		String source = null;
		while(source == null){
			source = readSourcePath(br);
		}
		String target = null;
		while(target == null){
			target = readTargetPath(br);
		}
		logger.debug("安装资源文件夹:{}",source);
		logger.debug("程序安装路径:{}",target);
		config.setSource(source);
		config.setTarget(target);
		if(config.validateFileCopy()){
			List<FileCopyInfo> fileCopyInfos = config.getFileCopyInfos();
			if(fileCopyInfos != null){
				processFileCopy(fileCopyInfos, config);
			}
			
			if(config.getFileEditInfos() != null && !config.getFileEditInfos().isEmpty()){
				List<FileEditInfo> fileEditInfos = config.getFileEditInfos();
				processFileEdit(fileEditInfos, config, br);
			}
		}
		logger.debug("安装过程结束");
	}
	
	private static void processFileEdit(List<FileEditInfo> fileEditInfos,InstallConfig config,BufferedReader br) throws FileNotFoundException, IOException{
		for(FileEditInfo fileEdit : fileEditInfos){
			File file = new File(fileEdit.getFile().replace("${TARGET}", config.getTarget()));
			if(file.exists() && file.isFile()){
				if(file.getName().toLowerCase().endsWith(".properties")){
					if(fileEdit.getItems() != null && !fileEdit.getItems().isEmpty()){
						logger.debug("修改文件{}的参数",fileEdit.getFile());
						editProperties(file, fileEdit.getItems(), br);
					}
				}else if(file.getName().toLowerCase().endsWith(".xml")){
					editXml(file, fileEdit.getItems(), br);
				}else{
					logger.error("{}文件类型暂时不支持修改,请自行修改",fileEdit.getFile());
				}
			}else{
				logger.error("{}不存在或者不是文件类型。",fileEdit.getFile());
			}
		}
		logger.debug("文件编辑完成");
	}
	
	private static void processFileCopy(List<FileCopyInfo> fileCopyInfos,InstallConfig config){
		logger.debug("开始拷贝文件");
		for(FileCopyInfo fi : fileCopyInfos){
			File src = new File(fi.getFrom().replace("${SOURCE}", config.getSource()));
			File obj = new File(fi.getTo().replace("${TARGET}", config.getTarget()));
			logger.debug(fi.getDesc());
			if(src.isDirectory()){
				logger.debug("拷贝文件夹[{}] 至 [{}]。",src.getAbsolutePath(),obj.getAbsolutePath());
				FileUtils.dirCopy(src, obj);
			}else{
				logger.debug("拷贝文件[{}] 至 [{}]。",src.getAbsolutePath(),obj.getAbsolutePath());
				FileUtils.fileCopy(src, obj);
			}
		}
		logger.debug("拷贝文件过程完成");
	}
	
	private static String readTargetPath(BufferedReader br) throws IOException{
		System.out.print("\n*请输入程序安装路径 : ");
		String target = br.readLine();
		if(target == null || "".equals(target.trim())){
			logger.debug("程序安装路径不能为空,请重新输入");
			target = null;
		}
		File s = new File(target);
		if(!s.exists()){
			logger.debug("{}不存在",target);
			target = null;
		}
		if(s.isFile()){
			logger.debug("{}不是文件夹",target);
			target = null;
		}
		return target;
	}
	
	private static String readSourcePath(BufferedReader br) throws IOException{
		String userDir = System.getProperty("user.dir");
		System.out.print("\n*请输入安装资源文件夹完整路径(直接回车为当前文件夹) : ");
		String source = br.readLine();
		if(source == null || "".equals(source.trim())){
			source = userDir;
			logger.debug(source);
		}
		File s = new File(source);
		if(!s.exists()){
			logger.debug("{}不存在",source);
			source = null;
		}
		if(s.isFile()){
			logger.debug("{}不是文件夹",source);
			source = null;
		}
		return source;
	}
	
	private static String readConfigPath(BufferedReader br) throws IOException{
		System.out.print("\n*请输入安装配置文件完整路径 : ");
		String line = br.readLine();
		if(line != null){
			File file = new File(line.trim());
			if(file.exists() && file.isFile()){
				return file.getAbsolutePath();
			}else{
				System.out.println("文件不存在或者并非文件类型");
			}
		}
		return null;
	}
	
	private static void editXml(File file,List<FileEditItem> items,BufferedReader br) throws IOException{
		try{
			Document document = new SAXReader().read(new FileInputStream(file));
			boolean hasEdit = false;
			for(FileEditItem item : items){
				Node node = document.selectSingleNode(item.getItemName());
				if(node != null){
					System.out.print("\n*" + item.getItemDesc() + "(" + item.getItemName() + ") : ");
					String line = br.readLine();
					if(line != null){
						node.setText(line);
						hasEdit = true;
						logger.debug("设置参数 {}={}",item.getItemName(),line);
					}else{
						logger.warn("{}的属性{}未修改",file.getAbsolutePath(),item.getItemName());
					}
				}else{
					logger.error("{}不存在",item.getItemName());
				}
			}
			if(hasEdit){
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding(document.getXMLEncoding());
				XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
				writer.write(document);
				writer.close();
			}
		}catch(DocumentException e){
			logger.error("XML文档{}格式错误",file.getAbsolutePath());
		}
	}
	
	private static void editProperties(File file,List<FileEditItem> items,BufferedReader br) throws FileNotFoundException, IOException{
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		boolean hasEdit = false;
		for(FileEditItem item : items){
			System.out.print("\n*" + item.getItemDesc() + "(" + item.getItemName() + ") : ");
			String line = br.readLine();
			if(line != null){
				prop.put(item.getItemName(), line);
				logger.debug("设置参数 {}={}",item.getItemName(),prop.getProperty(item.getItemName()));
				hasEdit = true;
			}else{
				logger.warn("{}的属性{}未修改",file.getAbsolutePath(),item.getItemName());
			}
		}
		if(hasEdit){
			FileOutputStream fos = new FileOutputStream(file);
			prop.store(fos, null);
			fos.flush();
			fos.close();
		}
	}

}
