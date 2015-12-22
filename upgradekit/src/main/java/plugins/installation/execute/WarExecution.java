/**
 * 
 */
package plugins.installation.execute;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.Iterator;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.file.FileUtils;

/**
 * @author fengmengyue
 *
 */
public class WarExecution implements Execution {
	
	private static final Logger logger = LoggerFactory.getLogger(WarExecution.class);

	private String war;
	
	private String dir;
	
	private String desc;
	
	@Override
	public void load(Element e) {
		this.war = e.attributeValue("war");
		this.dir = e.attributeValue("dir");
		this.desc = e.attributeValue("desc");
	}
	
	@Override
	public void execute(InstallConfig config) throws Exception {
		File warFile = new File(FileUtils.pathFormat(war, config.getContext()));
		File dirPath = new File(FileUtils.pathFormat(dir, config.getContext()));
		logger.info("开始打war包,从{} 到 {}",dirPath.getAbsolutePath(),warFile.getAbsolutePath());
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(warFile));
        ArchiveOutputStream out = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.JAR,bufferedOutputStream);
        try{
        	if(dirPath.isDirectory()){
	        	Iterator<File> files = org.apache.commons.io.FileUtils.iterateFiles(dirPath, null, true);
	        	String filePrefix = dirPath.getAbsolutePath().replace(File.separator, "\\");
	        	if(!filePrefix.endsWith("\\")){
	        		filePrefix += "\\";
	        	}
	            while (files.hasNext()) {
	                File file = files.next();
	                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getPath().replace(filePrefix, ""));
	                logger.info("压缩文件{}至{}",file.getAbsolutePath(),zipArchiveEntry.getName());
	                out.putArchiveEntry(zipArchiveEntry);
	                IOUtils.copy(new FileInputStream(file), out);
	                out.closeArchiveEntry();
	            }
        	}else{
                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(dirPath, dirPath.getName());
                out.putArchiveEntry(zipArchiveEntry);
                IOUtils.copy(new FileInputStream(dirPath), out);
                out.closeArchiveEntry();
        	}
        }finally{
        	out.close();
        	logger.info("解压完成");
        }
	}

	@Override
	public boolean validate(InstallConfig config) {
		File dirPath = new File(FileUtils.pathFormat(dir, config.getContext()));
		return dirPath.exists();
	}

	@Override
	public String info(InstallConfig config) {
		return MessageFormat.format("压缩{0}为war包{1}", dir,war);
	}

	public String getWar() {
		return war;
	}

	public void setWar(String war) {
		this.war = war;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
