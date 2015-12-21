/**
 * 
 */
package plugins.installation.execute;

import java.io.File;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.file.FileUtils;

/**
 * 文件删除
 * @author fengmengyue
 *
 */
public class FileRemoveExecution implements Execution {
	private static Logger logger = LoggerFactory.getLogger(FileEditExecution.class);
	private String filePath;

	@Override
	public void execute(InstallConfig config) throws Exception {
		File src = new File(FileUtils.pathFormat(filePath, config.getContext()));
		if(src.exists()){
			logger.info("删除{}:{}",src.isDirectory()?"文件夹":"文件",src.getAbsolutePath());
			FileUtils.delete(src);
		}
	}

	@Override
	public boolean validate(InstallConfig config) {
		return true;
	}

	@Override
	public String info(InstallConfig config) {
		File src = new File(FileUtils.pathFormat(filePath, config.getContext()));
		return "删除文件:" + src.getAbsolutePath() + (src.exists() ? (src.isDirectory()?"[文件夹]":"") : "(不存在)");
	}

	@Override
	public void load(Element element) {
		filePath = element.attributeValue("file");
	}

}
