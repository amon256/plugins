/**
 * FileCopyExecution.java.java
 * @author FengMy
 * @since 2015年6月2日
 */
package plugins.installation.execute;

import java.io.File;
import java.text.MessageFormat;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
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
	
	@Override
	public void load(Element e) {
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
		this.fileCopyInfo = info;
	}
	
	@Override
	public void execute(InstallConfig config) throws Exception{
		File src = new File(FileUtils.pathFormat(fileCopyInfo.getFrom(), config.getContext()));
		File obj = new File(FileUtils.pathFormat(fileCopyInfo.getTo(), config.getContext()));
		logger.info(fileCopyInfo.getDesc());
		if(obj.exists()){
			logger.info("目标{}己存在，先删除",obj.getAbsolutePath());
			FileUtils.delete(obj);
		}
		if(src.isDirectory()){
			logger.info("拷贝文件夹[{}] 至 [{}]。",src.getAbsolutePath(),obj.getAbsolutePath());
			FileUtils.dirCopy(src, obj,true);
		}else{
			logger.info("拷贝文件[{}] 至 [{}]。",src.getAbsolutePath(),obj.getAbsolutePath());
			FileUtils.fileCopy(src, obj,true);
		}
	}
	
	@Override
	public boolean validate(InstallConfig config) {
		File src = null;
		boolean flag = true;
		if(this.fileCopyInfo.getFrom() == null || "".equals(this.fileCopyInfo.getFrom().trim())){
			logger.info("源位置未配置");
			flag = false;
		}else{
			String path = FileUtils.pathFormat(this.fileCopyInfo.getFrom(), config.getContext());
			src = new File(path);
			if(!src.exists()){
				logger.info("文件[{}]不存在",this.fileCopyInfo.getFrom());
				flag = false;
			}
		}
		if(this.fileCopyInfo.getTo() == null || "".equals(this.fileCopyInfo.getTo().trim())){
			logger.info("目标位置未配置");
			flag = false;
		}else{
			File obj = new File(FileUtils.pathFormat(this.fileCopyInfo.getTo(), config.getContext()));
			if(src != null && src.exists() && obj.exists() && obj.isDirectory() != src.isDirectory()){
				logger.info("文件类型不匹配:[{}] | [{}]",src.getAbsolutePath(),obj.getAbsolutePath());
				flag = false;
			}
		}
		return flag;
	}
	
	@Override
	public String info(InstallConfig config) {
		File src = new File(FileUtils.pathFormat(fileCopyInfo.getFrom(), config.getContext()));
		File obj = new File(FileUtils.pathFormat(fileCopyInfo.getTo(), config.getContext()));
		String pattern = "拷贝文件夹[{0}] 至 [{1}]。";
		if(src.isFile()){
			pattern = "拷贝文件[{0}] 至 [{1}]。";
		}
		return MessageFormat.format(pattern, src.getAbsolutePath(),obj.getAbsolutePath());
	}

}
