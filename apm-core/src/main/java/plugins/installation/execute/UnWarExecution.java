/**
 * 
 */
package plugins.installation.execute;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;

/**
 * war包解压
 * @author fengmengyue
 *
 */
public class UnWarExecution implements Execution {
	
	private static final Logger logger = LoggerFactory.getLogger(UnWarExecution.class);
	
	/**
	 * 描述
	 */
	private String desc;
	
	/**
	 * 文件
	 */
	private String file;
	
	/**
	 * 解压为
	 */
	private String to;
	
	/**
	 * 编码
	 */
	private String encoding;
	
	@Override
	public void load(Element e) {
		this.desc = e.attributeValue("desc");
		this.file = e.attributeValue("file");
		this.to = e.attributeValue("to");
		this.encoding = e.attributeValue("encoding");
	}

	@Override
	public void execute(InstallConfig config) throws Exception {
		File warFile = new File(plugins.installation.file.FileUtils.pathFormat(file, config.getContext()));
		String toPath = plugins.installation.file.FileUtils.pathFormat(to, config.getContext());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(warFile));
        ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.JAR,bufferedInputStream);
        try{
	        JarArchiveEntry entry = null;
	        logger.info("解压文件:{} 到 {}",warFile.getAbsolutePath(),toPath);
	        while ((entry = (JarArchiveEntry) in.getNextEntry()) != null) {
	            if (entry.isDirectory()) {
	            	File dir = new File(toPath, entry.getName());
	                if(!dir.exists()){
	                	logger.info("文件夹{}不存在,创建.",dir.getAbsolutePath());
	                	dir.mkdir();
	                }
	                if(dir.exists() && !dir.isDirectory()){
	                	logger.info("不能解压文件{}到{},此文件己存在",entry.getName(),dir.getAbsolutePath());
	                }
	            } else {
	            	File outFile = new File(toPath, entry.getName());
	            	if(!outFile.getParentFile().exists()){
						logger.info("文件夹{}不存在,创建.",outFile.getParentFile().getAbsolutePath());
						outFile.getParentFile().mkdirs();
					}
	            	logger.info("解压{} 至 {}",entry.getName(),outFile.getAbsolutePath());
	                OutputStream out = FileUtils.openOutputStream(outFile);
	                IOUtils.copy(in, out);
	                out.close();
	            }
	        }
        }finally{
        	in.close();
        	logger.info("解压结束.");
        }
	}

	@Override
	public boolean validate(InstallConfig config) {
		File warFile = new File(file); 
		return warFile .exists() && warFile.isFile();
	}

	@Override
	public String info(InstallConfig config) {
		return MessageFormat.format("解压war包[{0}] 到 [{1}]", file,to);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	
}
