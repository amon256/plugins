/**
 * Installer.java.java
 * @author FengMy
 * @since 2015年6月1日
 */
package plugins.installation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.file.FileCopyInfo;
import plugins.installation.file.FileEditInfo;
import plugins.installation.file.FileUtils;
import plugins.installation.file.InstallConfig;
import plugins.installation.file.FileEditInfo.FileEditItem;

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
			System.out.print("请输入安装配置文件完整路径:");
			String line = br.readLine();
			if(line != null){
				File file = new File(line.trim());
				if(file.exists() && file.isFile()){
					configPath = file.getAbsolutePath();
				}else{
					System.out.println("文件不存在或者并非文件类型");
				}
			}
		}
		InstallConfig config = InstallConfig.loadFrom(new File(configPath));
		if(config.validateFileCopy()){
			List<FileCopyInfo> fileCopyInfos = config.getFileCopyInfos();
			if(fileCopyInfos != null){
				for(FileCopyInfo fi : fileCopyInfos){
					File src = new File(fi.getFrom().replace("${SOURCE}", config.getSource()));
					File obj = new File(fi.getTo().replace("${TARGET}", config.getTarget()));
					logger.debug("=========="+fi.getDesc() + "开始============>");
					if(src.isDirectory()){
						FileUtils.dirCopy(src, obj);
					}else{
						FileUtils.fileCopy(src, obj);
					}
					logger.debug("<=========="+fi.getDesc() + "结束============");
				}
				logger.debug("拷贝文件过程完成");
			}
			
			if(config.getFileEditInfos() != null && !config.getFileEditInfos().isEmpty()){
				List<FileEditInfo> fileEditInfos = config.getFileEditInfos();
				for(FileEditInfo fileEdit : fileEditInfos){
					File file = new File(fileEdit.getFile().replace("${TARGET}", config.getTarget()));
					if(file.exists() && file.isFile()){
						if(file.getName().toLowerCase().endsWith(".properties")){
							if(fileEdit.getItems() != null && !fileEdit.getItems().isEmpty()){
								logger.debug("修改文件{}的参数",fileEdit.getFile());
								Properties prop = new Properties();
								prop.load(new FileInputStream(file));
								List<FileEditItem> items = fileEdit.getItems();
								for(FileEditItem item : items){
									System.out.print(item.getItemDesc() + "(" + item.getItemName() + "):");
									String line = br.readLine();
									prop.put(item.getItemName(), line);
									logger.debug("set {}={}",item.getItemName(),prop.getProperty(item.getItemName()));
								}
								FileOutputStream fos = new FileOutputStream(file);
								prop.store(fos, null);
								fos.flush();
								fos.close();
							}
						}else if(file.getName().toLowerCase().endsWith(".xml")){
							
						}else{
							logger.error("{}文件类型暂时不支持修改,请自行修改",fileEdit.getFile());
						}
					}else{
						logger.error("{}不存在或者不是文件类型。",fileEdit.getFile());
					}
				}
				logger.debug("文件编辑完成");
			}
		}
	}

}
