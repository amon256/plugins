/**
 * InstallAuto.java.java
 * @author FengMy
 * @since 2015年12月10日
 */
package plugins.installation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.execute.Execution;

/**  
 * 功能描述：自动安装入口，免输入，所有参数从args中获取
 * 
 * @author FengMy
 * @since 2015年12月10日
 */
public class InstallAuto {
	private static Logger logger = LoggerFactory.getLogger(Installer.class);
	private static final String ARG_HOME = "install_home";//安装源文件夹
	private static final String ARG_TARGET = "install_target";//安装源文件夹
	private static final String ARG_CONFIG = "install_config";//安装过程配置文件
	
	/**
	 * @param args key=value的形式
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Map<String,String> argMap = analysisArgs(args);
		if(!validateArgs(argMap)){
			logger.info("校验不通过,终止安装.");
			return;
		}
		InstallConfig config = InstallConfig.loadFrom(new File(argMap.get(ARG_CONFIG)));
		config.setTarget(argMap.get(ARG_TARGET));
		config.getContext().putAll(argMap);
		List<Execution> executions = config.getExecutions();
		logger.info("\n开始安装.");
		for(Execution exec : executions){
			exec.execute(config);
		}
		logger.info("安装完成.");
		
	}
	
	public static void initContext(InstallConfig config){
		Date date = new Date();
		Map<String,Object> ctx = config.getContext();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		ctx.put("DATE", dateFormat.format(date));
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		ctx.put("TIME", timeFormat.format(date));
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		ctx.put("DATETIME", dateTimeFormat.format(date));
	}
	
	/**
	 * 初步校验安装参数
	 * @param argMap
	 * @return
	 */
	private static boolean validateArgs(Map<String,String> argMap){
		boolean flag = true;
		logger.info("\n***********校验安装参数************");
		String[] validateExists = new String[]{ARG_HOME,ARG_TARGET,ARG_CONFIG};
		for(String arg : validateExists){
			if(!argMap.containsKey(arg)){
				logger.info("参数:{},  不存在.",arg);
				flag = false;
			}
		}
		String[] validateExistsDirectory = new String[]{ARG_HOME,ARG_TARGET};
		for(String arg : validateExistsDirectory){
			if(!argMap.containsKey(arg)){
				continue;
			}
			File file = new File(argMap.get(arg));
			if(!file.exists()){
				logger.info("文件夹:{}, 不存在.",argMap.get(arg));
				flag = false;
			}else if(!file.isDirectory()){
				logger.info("{}不是一个文件夹.",argMap.get(arg));
				flag = false;
			}
		}
		String[] validateExistsFile = new String[]{ARG_CONFIG};
		for(String arg : validateExistsFile){
			if(!argMap.containsKey(arg)){
				continue;
			}
			File file = new File(argMap.get(arg));
			if(!file.exists()){
				logger.info("文件夹:{}, 不存在.",argMap.get(arg));
				flag = false;
			}else if(!file.isFile()){
				logger.info("{}不是一个文件.",argMap.get(arg));
				flag = false;
			}
		}
		logger.info("*************校验结束***************");
		return flag;
	}
	
	/**
	 * 解析参数
	 * @param args
	 * @return
	 */
	private static Map<String,String> analysisArgs(String[] args){
		Map<String,String> argMap = new LinkedHashMap<String, String>();
		logger.info("\n***********解析安装参数************");
		if(args != null && args.length > 0){
			for(String arg : args){
				if(arg == null || "".equals(arg.trim())){
					continue;
				}
				String[] argAttr = arg.split("=");
				if(argAttr != null && argAttr.length == 2 && argAttr[0] != null && !"".equals(argAttr[0].trim())){
					argMap.put(argAttr[0].trim(), argAttr[1].trim());
				}
			}
		}
		Set<String> keys = argMap.keySet();
		for(String key : keys){
			logger.info("{} = {}", key,argMap.get(key));
		}
		logger.info("*************解析结束***************");
		return argMap;
	}
}
