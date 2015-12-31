/**
 * 
 */
package plugins.installation.execute;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.file.FileUtils;

/**
 * war合并
 * @author fengmengyue
 *
 */
public class WarMergeExecution implements Execution {

	private static final Logger logger = LoggerFactory.getLogger(WarMergeExecution.class);
	
	private String from;
	
	private String to;
	
	@Override
	public void load(Element e) {
		this.from = e.attributeValue("from");
		this.to = e.attributeValue("to");
	}
	
	@Override
	public void execute(InstallConfig config) throws Exception {
		File fromWar = new File(FileUtils.pathFormat(from, config.getContext()));
		File toWar = new File(FileUtils.pathFormat(to, config.getContext()));
		ArchiveOutputStream out = null;
		ArchiveInputStream in = null;
        try{
        	logger.info("开始合并{} 到 {}",fromWar.getAbsolutePath(),toWar.getAbsolutePath());
        	BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(toWar));
            out = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.JAR,bufferedOutputStream);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fromWar));
            in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.JAR,bufferedInputStream);
            JarArchiveEntry entry = null;
	        while ((entry = (JarArchiveEntry) in.getNextEntry()) != null) {
	        	logger.info("合并文件{}",entry.getName());
	        	ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(entry.getName());
                out.putArchiveEntry(zipArchiveEntry);
                IOUtils.copy(in, out);
                out.closeArchiveEntry();
	        }
        }finally{
        	if(out != null){
        		out.close();
        	}
        	if(in != null){
        		in.close();
        	}
        	logger.info("合并完成");
        }
	}

	@Override
	public boolean validate(InstallConfig config) {
		File fromWar = new File(FileUtils.pathFormat(from, config.getContext()));
		File toWar = new File(FileUtils.pathFormat(to, config.getContext()));
		return fromWar.exists() && toWar.exists();
	}

	@Override
	public String info(InstallConfig config) {
		return null;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
