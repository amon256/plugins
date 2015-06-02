/**
 * Installer.java.java
 * @author FengMy
 * @since 2015年6月1日
 */
package plugins.installation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.execute.Execution;
import plugins.installation.execute.FileCopyExecution;
import plugins.installation.execute.FileEditExecution;
import plugins.installation.file.FileCopyInfo;
import plugins.installation.file.FileEditInfo;
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
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
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
			List<Execution> executions = new LinkedList<Execution>();
			List<FileCopyInfo> fileCopyInfos = config.getFileCopyInfos();
			if(fileCopyInfos != null){
				for(FileCopyInfo fi : fileCopyInfos){
					executions.add(new FileCopyExecution(fi));
				}
			}
			
			if(config.getFileEditInfos() != null && !config.getFileEditInfos().isEmpty()){
				List<FileEditInfo> fileEditInfos = config.getFileEditInfos();
				processFileEdit(fileEditInfos, config, br);
				for(FileEditInfo fe : fileEditInfos){
					executions.add(new FileEditExecution(fe));
				}
			}
			
			if(confirm(executions, config, br)){
				logger.debug("\n开始安装.");
				for(Execution exec : executions){
					exec.execute(config);
				}
				logger.debug("安装完成。");
			}else{
				logger.debug("取消安装。");
			}
		}else{
			logger.debug("校验失败,详情参看日志。");
		}
	}
	
	private static boolean confirm(List<Execution> executions,InstallConfig config,BufferedReader br) throws IOException{
		logger.debug("\n***********本次安装包含以下内容***********");
		for(Execution exec : executions){
			logger.debug(exec.info(config));
		}
		String line = null;
		while(!"Y".equalsIgnoreCase(line) && !"N".equalsIgnoreCase(line)){
			System.out.print("确认安装?(Y/N) : ");
			line = br.readLine();
		}
		return "Y".equalsIgnoreCase(line);
	}
	
	private static void processFileEdit(List<FileEditInfo> fileEditInfos,InstallConfig config,BufferedReader br) throws FileNotFoundException, IOException{
		for(FileEditInfo fileEdit : fileEditInfos){
			if(fileEdit.getItems() != null && !fileEdit.getItems().isEmpty()){
				List<FileEditItem> items = fileEdit.getItems();
				logger.debug("\n配置文件:{}",fileEdit.getFile());
				for(FileEditItem item : items){
					System.out.print("*" + item.getItemDesc() + "(" + item.getItemName() + ") : ");
					String line = br.readLine();
					item.setItemValue(line);
					logger.debug("配置[{}]值  {} = {}",item.getItemDesc(),item.getItemName(),item.getItemValue());
				}
			}
		}
		logger.debug("文件编辑完成");
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
				logger.debug("文件{}不存在或者并非文件类型",line);
			}
		}
		return null;
	}	
}
