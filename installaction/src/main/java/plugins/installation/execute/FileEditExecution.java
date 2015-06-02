/**
 * FileEditExecution.java.java
 * @author FengMy
 * @since 2015年6月2日
 */
package plugins.installation.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			if(file.getName().toLowerCase().endsWith(".properties")){
				if(fileEditInfo.getItems() != null && !fileEditInfo.getItems().isEmpty()){
					logger.debug("修改文件{}的参数",fileEditInfo.getFile());
					editProperties(file, fileEditInfo.getItems(), br);
				}
			}else if(file.getName().toLowerCase().endsWith(".xml")){
				editXml(file, fileEditInfo.getItems(), br);
			}else{
				logger.error("{}文件类型暂时不支持修改,请自行修改",fileEditInfo.getFile());
			}
		}else{
			logger.error("{}不存在或者不是文件类型。",fileEditInfo.getFile());
		}
	}
	
	private static void editXml(File file,List<FileEditItem> items,BufferedReader br) throws IOException{
		try{
			Document document = new SAXReader().read(new FileInputStream(file));
			boolean hasEdit = false;
			for(FileEditItem item : items){
				Node node = document.selectSingleNode(item.getItemName());
				if(node != null){
					System.out.print("\n*" + item.getItemDesc() + "(" + item.getItemName() + ") : ");
					String line = br.readLine();
					if(line != null){
						node.setText(line);
						hasEdit = true;
						logger.debug("设置参数 {}={}",item.getItemName(),line);
					}else{
						logger.warn("{}的属性{}未修改",file.getAbsolutePath(),item.getItemName());
					}
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
	
	private static void editProperties(File file,List<FileEditItem> items,BufferedReader br) throws FileNotFoundException, IOException{
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		boolean hasEdit = false;
		for(FileEditItem item : items){
			System.out.print("\n*" + item.getItemDesc() + "(" + item.getItemName() + ") : ");
			String line = br.readLine();
			if(line != null){
				prop.put(item.getItemName(), line);
				logger.debug("设置参数 {}={}",item.getItemName(),prop.getProperty(item.getItemName()));
				hasEdit = true;
			}else{
				logger.warn("{}的属性{}未修改",file.getAbsolutePath(),item.getItemName());
			}
		}
		if(hasEdit){
			FileOutputStream fos = new FileOutputStream(file);
			prop.store(fos, null);
			fos.flush();
			fos.close();
		}
	}

}
