/**
 * FileUnZipExecution.java.java
 * @author FengMy
 * @since 2015年6月5日
 */
package plugins.installation.execute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.file.FileUnZipInfo;
import plugins.installation.file.FileUtils;

/**  
 * 功能描述：文件zip解压执行 
 * 
 * @author FengMy
 * @since 2015年6月5日
 */
public class FileUnZipExecution implements Execution {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUnZipExecution.class);

	private FileUnZipInfo fileUnZipInfo;
	
	public FileUnZipExecution(FileUnZipInfo fileUnZipInfo){
		this.fileUnZipInfo = fileUnZipInfo;
	}
	
	@Override
	public void execute(InstallConfig config) throws Exception {
		File src = new File(FileUtils.pathFormat(fileUnZipInfo.getFile(), config.getContext()));
		File obj = new File(FileUtils.pathFormat(fileUnZipInfo.getTo(), config.getContext()));
		logger.info("解压文件:{} 到 {}",src.getAbsolutePath(),obj.getAbsolutePath());
		if(!obj.exists()){
			obj.mkdirs();
		}else{
			if(obj.exists() && obj.isFile()){
				logger.error("文件{}己存在且不是文件夹",obj.getAbsolutePath());
				return;
			}
		}
		unZipFile(src.getAbsolutePath(), obj.getAbsolutePath());
	}
	
	@Override
	public boolean validate(InstallConfig config) {
		boolean flag = true;
		File src = new File(FileUtils.pathFormat(fileUnZipInfo.getFile(), config.getContext()));
		if(!src.exists()){
			logger.debug("文件{}不存在",fileUnZipInfo.getFile());
			flag = false;
		}else if(src.isDirectory()){
			logger.debug("文件{}是文件夹，不能解压",fileUnZipInfo.getFile());
			flag = false;
		}else{
			File obj = new File(FileUtils.pathFormat(fileUnZipInfo.getTo(), config.getContext()));
			if(obj.exists() && obj.isFile()){
				logger.debug("{}己存在并且不是文件夹",fileUnZipInfo.getTo());
				flag = false;
			}
		}
		return flag;
	}
	
	@SuppressWarnings("unchecked")
	private void unZipFile(String filepath,String topath) throws IOException{
		File file = new File(filepath);
		File to = new File(topath);
		ZipFile zipFile;
		if(fileUnZipInfo.getEncoding() != null){
			zipFile = new ZipFile(file,fileUnZipInfo.getEncoding());
		}else{
			zipFile = new ZipFile(file,"gb2312");
		}
		try{
			for(Enumeration<? extends ZipEntry> zipEntrys = zipFile.getEntries(); zipEntrys.hasMoreElements();){
				ZipEntry entry = zipEntrys.nextElement();
				if(entry.isDirectory()){
					File dir = new File(to, entry.getName());
					if(!dir.exists()){
						dir.mkdirs();
					}
				}else{
					InputStream in = zipFile.getInputStream(entry);
					File outFile = new File(to, entry.getName());
					if(!outFile.getParentFile().exists()){
						outFile.getParentFile().mkdirs();
					}
					OutputStream out = new FileOutputStream(outFile);
					byte[] buff = new byte[1024];
					int len;
					while((len = in.read(buff)) > 0){
						out.write(buff, 0, len);
					}
					in.close();
					out.close();
				}
			}
		}finally{
			zipFile.close();
		}
	}

	@Override
	public String info(InstallConfig config) {
		File src = new File(FileUtils.pathFormat(fileUnZipInfo.getFile(), config.getContext()));
		File obj = new File(FileUtils.pathFormat(fileUnZipInfo.getTo(), config.getContext()));
		String pattern = "解压文件[{0}] 至 [{1}]。";
		return MessageFormat.format(pattern, src.getAbsolutePath(),obj.getAbsolutePath());
	}

}
