/**
 * FileCopyExecution.java.java
 * @author FengMy
 * @since 2015年6月2日
 */
package plugins.installation.execute;

import java.io.File;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.file.FileCopyInfo;
import plugins.installation.file.FileUtils;

/**  
 * 功能描述：文件复制执行
 * 
 * @author FengMy
 * @since 2015年6月2日
 */
public class FileCopyExecution implements Execution{
	
	private static Logger logger = LoggerFactory.getLogger(FileCopyExecution.class);
	
	private FileCopyInfo fileCopyInfo;
	
	public FileCopyExecution(FileCopyInfo fileCopyInfo){
		this.fileCopyInfo = fileCopyInfo;
	}

	@Override
	public void execute(InstallConfig config) throws Exception{
		File src = new File(fileCopyInfo.getFrom().replace("${SOURCE}", config.getSource()));
		File obj = new File(fileCopyInfo.getTo().replace("${TARGET}", config.getTarget()));
		logger.debug(fileCopyInfo.getDesc());
		if(src.isDirectory()){
			logger.debug("拷贝文件夹[{}] 至 [{}]。",src.getAbsolutePath(),obj.getAbsolutePath());
			FileUtils.dirCopy(src, obj,true);
		}else{
			logger.debug("拷贝文件[{}] 至 [{}]。",src.getAbsolutePath(),obj.getAbsolutePath());
			FileUtils.fileCopy(src, obj);
		}
	}
	
	@Override
	public String info(InstallConfig config) {
		File src = new File(fileCopyInfo.getFrom().replace("${SOURCE}", config.getSource()));
		File obj = new File(fileCopyInfo.getTo().replace("${TARGET}", config.getTarget()));
		String pattern = "拷贝文件夹[{0}] 至 [{1}]。";
		if(src.isFile()){
			pattern = "拷贝文件[{0}] 至 [{1}]。";
		}
		return MessageFormat.format(pattern, src.getAbsolutePath(),obj.getAbsolutePath());
	}

}
