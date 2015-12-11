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
	public static void dirCopy(File from,File to,boolean removeIfExists){
		if(from.exists() && from.isDirectory()){
			if(removeIfExists && to.exists()){
				delete(to);
			}
			if(to.exists() && to.isFile() && !removeIfExists){
				logger.info("目标文件己存在且并非文件夹。");
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
						fileCopy(file, new File(toPath),removeIfExists);
					}else{
						dirCopy(file, new File(toPath),removeIfExists);
					}
				}
			}
		}else{
			logger.info("源文件[{}]不存在或不是文件夹",from.getAbsolutePath());
		}
	}
	
	/**
	 * 文件复制，将文件from复制到文件to
	 * @param from
	 * @param to
	 */
	public static void fileCopy(File from,File to,boolean removeIfExists){
		if(from.exists() && from.isFile()){
			logger.info("拷贝文件:[{}] to [{}]",from.getAbsolutePath(),to.getAbsolutePath());
			if(removeIfExists && to.exists()){
				delete(to);
			}
			File parent = to.getParentFile();
			if(!parent.exists()){
				parent.mkdirs();
			}
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
			logger.info("源文件[{}]不存在或不是文件类型",from.getAbsolutePath());
		}
	}
	
	/**
	 * 删除文件(文件夹 )
	 * @param file
	 */
	public static void delete(File file){
		if(file != null && file.exists()){
			if(file.isFile()){
				file.delete();
			}else{
				File[] files = file.listFiles();
				if(files != null){
					for(File f : files){
						delete(f);
					}
				}
				file.delete();
			}
		}
	}
	
	/**
	 * 路径格式化:${xxxx}/abbb/${xxx}ccc/……
	 * @param path
	 * @param param
	 * @return
	 */
	public static String pathFormat(String path,Object param){
		return ELUtils.parseTemplate(path, param);
	}
	
	public static void main(String[] args) {
		dirCopy(new File("G:\\source\\config"), new File("G:\\target\\config"), true);
	}
}
