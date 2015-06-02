/**
 * FileUtils.java
 */
package plugins.installation.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * 功能描述：文件工具类
 * 
 * @author FengMy
 * @since 2015年6月1日
 */
public class FileUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	private static final int IO_BUFF_SIZE = 4 * 1024;
	
	/**
	 * 将文件夹from中的内容复制到文件to下
	 * @param from 源文件夹
	 * @param to 目标文件夹
	 */
	public static void dirCopy(File from,File to){
		if(from.exists() && from.isDirectory()){
			if(to.exists() && to.isFile()){
				logger.debug("目标文件己存在且并非文件夹。");
			}else{
				if(!to.exists()){
					if(!to.mkdirs()){
						logger.error("创建文件夹[{}]失败",to.getAbsolutePath());
						throw new RuntimeException("创建文件夹失败,");
					}
				}
				File[] fileList = from.listFiles();
				for(File file : fileList){
					String toPath = to.getAbsolutePath() + File.separator + file.getName();
					if(file.isFile()){
						fileCopy(file, new File(toPath));
					}else{
						dirCopy(file, new File(toPath));
					}
				}
			}
		}else{
			logger.debug("源文件[{}]不存在或不是文件夹",from.getAbsolutePath());
		}
	}
	
	/**
	 * 文件复制，将文件from复制到文件to
	 * @param from
	 * @param to
	 */
	public static void fileCopy(File from,File to){
		if(from.exists() && from.isFile()){
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try{
				fis = new FileInputStream(from);
				fos = new FileOutputStream(to);
				byte[] buff = new byte[IO_BUFF_SIZE];//4kbuff
				int len = 0;
				while((len = fis.read(buff)) != -1 ){
					fos.write(buff, 0, len);
				}
			}catch(Exception e){
				throw new RuntimeException(e);
			}finally{
				try {
					if(fis != null){
						fis.close();
					}
					if(fos != null){
						fos.close();
					}
				} catch (IOException e) {
					logger.error("关闭IO失败",e);
				}
			}
		}else{
			logger.debug("源文件[{}]不存在或不是文件类型",from.getAbsolutePath());
		}
	}
}
