/**
 * Installer.java.java
 * @author FengMy
 * @since 2015年6月1日
 */
package plugins.installation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.execute.Execution;
import plugins.installation.execute.FileEditInfo;
import plugins.installation.execute.FileEditInfo.FileEditItem;

/**  
 * 功能描述：文件安装程序
 * 
 * @author FengMy
 * @since 2015年6月1日
 */
public class Installer {
	
	private static Logger logger = LoggerFactory.getLogger(Installer.class);

	/**
	 * 安装程序入口
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		try{
			String configPath = null;
			if(args != null && args.length > 0){
				configPath = args[0];
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while(configPath == null){
				configPath = readConfigPath(br);
			}
			InstallConfig config = InstallConfig.loadFrom(new File(configPath));
			initContext(config);
			logger.info("*************{}****************",config.getName());
			logger.info("当前路径:{}",System.getProperty("user.dir"));
			String target = null;
			while(target == null){
				target = readTargetPath(config,br);
			}
			config.setTarget(target);
			logger.info("程序安装路径:{}",target);
			
			if(config.validate()){
				List<FileEditInfo> fileEditInfos = config.getEditInfoList();
				if(fileEditInfos != null && !fileEditInfos.isEmpty()){
					processFileEdit(fileEditInfos, config, br);
				}
				List<Execution> executions = config.getExecutions();
				if(confirm(executions, config, br)){
					logger.info("\n开始安装.");
					for(Execution exec : executions){
						exec.execute(config);
					}
					logger.info("安装完成。");
				}else{
					logger.info("取消安装。");
				}
			}else{
				logger.info("校验失败,详情参看日志。");
			}
		}catch(Exception e){
			logger.error("安装过程异常",e);
		}
	}
	
	public static void initContext(InstallConfig config){
		Date date = new Date();
		Map<String,Object> ctx = config.getContext();
		ctx.put("home", System.getProperty("user.dir"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		ctx.put("DATE", dateFormat.format(date));
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		ctx.put("TIME", timeFormat.format(date));
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		ctx.put("DATETIME", dateTimeFormat.format(date));
	}
	
	private static boolean confirm(List<Execution> executions,InstallConfig config,BufferedReader br) throws IOException{
		logger.info("\n***********本次安装将会执行以下内容***********");
		for(Execution exec : executions){
			logger.info(exec.info(config));
		}
		String line = null;
		while(!"Y".equalsIgnoreCase(line) && !"N".equalsIgnoreCase(line)){
			System.out.print("确认安装?(Y/N) : ");
			line = br.readLine();
		}
		return "Y".equalsIgnoreCase(line);
	}
	
	private static void processFileEdit(List<FileEditInfo> fileEditInfos,InstallConfig config,BufferedReader br) throws FileNotFoundException, IOException{
		String line = null;
		while(line == null){
			logger.info("选择文件参数输入方式");
			System.out.print("1、从控制台输入编辑项\n2、从参数表中读取编辑项\n[1 / 2]:");
			line = br.readLine();
			if(line == null){
				continue;
			}
			if("1".equals(line.trim()) || "2".equals(line.trim())){
				break;
			}else{
				line = null;
			}
		}
		if("2".equals(line)){
			readFileEditItemValueFromFile(fileEditInfos, config, br);
		}else{
			readFileEditItemValueFromConsole(fileEditInfos, config, br);
		}
	}
	
	/**
	 * 从文件键值表中读取数据
	 * @param fileEditInfos
	 * @param config
	 * @param br
	 * @throws IOException 
	 */
	private static void readFileEditItemValueFromFile(List<FileEditInfo> fileEditInfos,InstallConfig config,BufferedReader br) throws IOException{
		Map<String,String> valueMap = readValueMap(br);
		for(FileEditInfo fileEdit : fileEditInfos){
			if(fileEdit.getItems() != null && !fileEdit.getItems().isEmpty()){
				List<FileEditItem> items = fileEdit.getItems();
				logger.info("\n配置文件:{}",fileEdit.getFile());
				for(FileEditItem item : items){
					if(valueMap.containsKey(item.getItemName())){
						item.setItemValue(valueMap.get(item.getItemName()));
					}else{
						logger.info("参数{}({})未配置",item.getItemName(),item.getItemDesc());
						throw new RuntimeException("参数文件中缺少参数配置");
					}
					logger.info("配置[{}]值  {} = {}",item.getItemDesc(),item.getItemName(),item.getItemValue());
				}
			}
		}
	}
	
	private static Map<String,String> readValueMap(BufferedReader br) throws IOException{
		Map<String,String> valueMap = new LinkedHashMap<String, String>();
		String line = null;
		File file = null;
		while(line == null){
			System.out.print("请输入参数文件位置 : ");
			line = br.readLine();
			if(line != null && !"".equals(line)){
				file = new File(line);
				if(file.exists() && file.isFile()){
					break;
				}else{
					logger.info("文件{}不存在或者不是文件类型",line);
					line = null;
				}
			}else{
				line = null;
			}
		}
		logger.info("开始从参数文件{}中读取",line);
		br = new BufferedReader(new FileReader(file));
		while((line = br.readLine()) != null){
			line = line.trim();
			if("".equals(line) || line.startsWith("#")){
				//忽略空行和注释行
				continue;
			}
			int idx = line.indexOf("=");
			String key = null;
			String value = null;
			if(idx > 0){
				key = line.substring(0, idx).trim();
				value = line.substring(idx + 1).trim();
				valueMap.put(key, value);
			}
		}
		br.close();
		logger.info("从参数配置文件中读取到的值:");
		Set<Entry<String, String>> entrys = valueMap.entrySet();
		for(Entry<String,String> entry : entrys){
			logger.info(" {} = {}",entry.getKey(),entry.getValue());
		}
		return valueMap;
	}
	
	/**
	 * 从控制台录入文件编辑项
	 * @param fileEditInfos
	 * @param config
	 * @param br
	 * @throws IOException
	 */
	private static void readFileEditItemValueFromConsole(List<FileEditInfo> fileEditInfos,InstallConfig config,BufferedReader br) throws IOException{
		for(FileEditInfo fileEdit : fileEditInfos){
			if(fileEdit.getItems() != null && !fileEdit.getItems().isEmpty()){
				List<FileEditItem> items = fileEdit.getItems();
				logger.info("\n配置文件:{}",fileEdit.getFile());
				for(FileEditItem item : items){
					System.out.print("*" + item.getItemDesc() + "(" + item.getItemName() + ") : ");
					String line = br.readLine();
					item.setItemValue(line);
					logger.info("配置[{}]值  {} = {}",item.getItemDesc(),item.getItemName(),item.getItemValue());
				}
			}
		}
		logger.info("文件编辑完成");
	}
	
	private static String readTargetPath(InstallConfig config,BufferedReader br) throws IOException{
		String targetname = "程序安装路径";
		if(config.getTargetname() != null){
			targetname = config.getTargetname();
		}
		System.out.print("\n*请输入"+targetname+" : ");
		String target = br.readLine();
		if(target == null || "".equals(target.trim())){
			logger.info("{}不能为空,请重新输入",targetname);
			target = null;
		}else{
			File s = new File(target);
			if(!s.exists()){
				logger.info("{}不存在",target);
				target = null;
			}
			if(s.isFile()){
				logger.info("{}不是文件夹",target);
				target = null;
			}
		}
		return target;
	}
	
//	private static String readSourcePath(InstallConfig config, BufferedReader br) throws IOException{
//		String sourcename = "安装资源文件夹完整路径";
//		if(config.getSourcename() != null){
//			sourcename = config.getSourcename();
//		}
//		System.out.print("\n*请输入"+sourcename+" : ");
//		String source = br.readLine();
//		if(source == null || "".equals(source.trim())){
//			source = null;
//		}else{
//			File s = new File(source);
//			if(!s.exists()){
//				logger.info("{}不存在",source);
//				source = null;
//			}
//			if(s.isFile()){
//				logger.info("{}不是文件夹",source);
//				source = null;
//			}
//		}
//		return source;
//	}
	
	private static String readConfigPath(BufferedReader br) throws IOException{
		System.out.print("\n*请输入安装配置文件完整路径 : ");
		String line = br.readLine();
		if(line != null){
			File file = new File(line.trim());
			if(file.exists() && file.isFile()){
				return file.getAbsolutePath();
			}else{
				logger.info("文件{}不存在或者并非文件类型",line);
			}
		}
		return null;
	}	
}
