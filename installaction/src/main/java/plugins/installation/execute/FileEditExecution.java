/**
 * FileEditExecution.java.java
 * @author FengMy
 * @since 2015年6月2日
 */
package plugins.installation.execute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.file.FileEditInfo;
import plugins.installation.file.FileEditInfo.FileEditItem;

/**  
 * 功能描述：文件编辑执行
 * 
 * @author FengMy
 * @since 2015年6月2日
 */
public class FileEditExecution implements Execution {
	
	private static Logger logger = LoggerFactory.getLogger(FileEditExecution.class);
	
	private FileEditInfo fileEditInfo;
	
	public FileEditExecution(FileEditInfo fileEditInfo){
		this.fileEditInfo = fileEditInfo;
	}

	@Override
	public void execute(InstallConfig config) throws Exception{
		File file = new File(fileEditInfo.getFile().replace("${TARGET}", config.getTarget()));
		if(file.exists() && file.isFile()){
			if(file.getName().toLowerCase().endsWith(".properties")){
				if(fileEditInfo.getItems() != null && !fileEditInfo.getItems().isEmpty()){
					logger.debug("修改文件{}的参数",fileEditInfo.getFile());
					editProperties(file, fileEditInfo.getItems());
				}
			}else if(file.getName().toLowerCase().endsWith(".xml")){
				if(fileEditInfo.getItems() != null && !fileEditInfo.getItems().isEmpty()){
					logger.debug("修改文件{}的参数",fileEditInfo.getFile());
					editXml(file, fileEditInfo.getItems());
				}
			}else{
				logger.error("{}文件类型暂时不支持修改,请自行修改",fileEditInfo.getFile());
			}
		}else{
			logger.error("{}不存在或者不是文件类型。",fileEditInfo.getFile());
		}
	}
	
	private static void editXml(File file,List<FileEditItem> items) throws IOException{
		try{
			Document document = new SAXReader().read(new FileInputStream(file));
			boolean hasEdit = false;
			for(FileEditItem item : items){
				Node node = document.selectSingleNode(item.getItemName());
				if(node != null){
					node.setText(item.getItemValue());
					hasEdit = true;
					logger.debug("设置参数 {}={}",item.getItemName(),item.getItemValue());
				}else{
					logger.error("{}不存在",item.getItemName());
				}
			}
			if(hasEdit){
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding(document.getXMLEncoding());
				XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
				writer.write(document);
				writer.close();
			}
		}catch(DocumentException e){
			logger.error("XML文档{}格式错误",file.getAbsolutePath());
		}
	}
	
	private static void editProperties(File file,List<FileEditItem> items) throws FileNotFoundException, IOException{
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		boolean hasEdit = false;
		for(FileEditItem item : items){
			prop.put(item.getItemName(), item.getItemValue());
			logger.debug("设置参数 {}={}",item.getItemName(), item.getItemValue());
			hasEdit = true;
		}
		if(hasEdit){
			FileOutputStream fos = new FileOutputStream(file);
			prop.store(fos, null);
			fos.flush();
			fos.close();
		}
	}
	
	@Override
	public String info(InstallConfig config) {
		File file = new File(fileEditInfo.getFile().replace("${TARGET}", config.getTarget()));
		String pattern = "修改配置文件:{0}";
		String itemPattern = "\n【{0}】:   {1} = {2}";
		StringBuilder info = new StringBuilder(MessageFormat.format(pattern, file.getAbsolutePath()));
		List<FileEditItem> items = fileEditInfo.getItems();
		for(FileEditItem fi : items){
			info.append(MessageFormat.format(itemPattern, fi.getItemDesc(),fi.getItemName(),fi.getItemValue()));
		}
		return info.toString();
	}

}
