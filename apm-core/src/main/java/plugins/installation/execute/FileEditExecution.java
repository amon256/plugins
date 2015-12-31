/**
 * FileEditExecution.java.java
 * @author FengMy
 * @since 2015年6月2日
 */
package plugins.installation.execute;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.config.InstallConfig;
import plugins.installation.execute.FileEditInfo.FileEditItem;
import plugins.installation.file.ELUtils;
import plugins.installation.file.FileUtils;

/**  
 * 功能描述：文件编辑执行
 * 
 * @author FengMy
 * @since 2015年6月2日
 */
public class FileEditExecution implements Execution {
	
	private static Logger logger = LoggerFactory.getLogger(FileEditExecution.class);
	
	private FileEditInfo fileEditInfo;
	
	@Override
	public void load(Element e) {
		FileEditInfo fi = new FileEditInfo();
		fi.setFile(e.attributeValue("file"));
		fi.setItems(readEditItems(e));
		this.fileEditInfo = fi;
	}
	
	/**
	 * 读取文件编辑项
	 * @param ele
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<FileEditItem> readEditItems(Element ele){
		List<FileEditItem> editItems = new LinkedList<FileEditItem>();
		List<Element> infoElements = ele.elements("item");
		if(infoElements != null){
			for(Element e : infoElements){
				FileEditItem item = new FileEditItem();
				item.setItemName(e.attributeValue("name"));
				item.setItemDesc(e.attributeValue("desc"));
				item.setItemValue(e.attributeValue("value"));
				editItems.add(item);
			}
		}
		return editItems;
	}

	@Override
	public void execute(InstallConfig config) throws Exception{
		File file = new File(FileUtils.pathFormat(fileEditInfo.getFile(), config.getContext()));
		if(file.exists() && file.isFile()){
			if(file.getName().toLowerCase().endsWith(".properties")){
				if(fileEditInfo.getItems() != null && !fileEditInfo.getItems().isEmpty()){
					logger.info("修改文件{}的参数",fileEditInfo.getFile());
					editProperties(file, fileEditInfo.getItems(), config);
				}
			}else if(file.getName().toLowerCase().endsWith(".xml")){
				if(fileEditInfo.getItems() != null && !fileEditInfo.getItems().isEmpty()){
					logger.info("修改文件{}的参数",fileEditInfo.getFile());
					editXml(file, fileEditInfo.getItems(), config);
				}
			}else{
				logger.error("{}文件类型暂时不支持修改,请自行修改",fileEditInfo.getFile());
			}
		}else{
			logger.error("{}不存在或者不是文件类型。",fileEditInfo.getFile());
		}
	}
	
	@Override
	public boolean validate(InstallConfig config) {
		return true;
	}
	
	private static void editXml(File file,List<FileEditItem> items,InstallConfig config) throws IOException{
		try{
			Document document = new SAXReader().read(new FileInputStream(file));
			boolean hasEdit = false;
			for(FileEditItem item : items){
				if(item.getItemName() == null || item.getItemValue() == null){
					logger.info("参数{},{}={}为空,设置为空字符串",item.getItemDesc(),item.getItemName(),item.getItemValue());
					item.setItemValue("");
				}else{
					item.setItemValue(ELUtils.parseTemplate(item.getItemValue(), config.getContext()));
				}
				Node node = document.selectSingleNode(item.getItemName());
				if(node != null){
					node.setText(item.getItemValue());
					hasEdit = true;
					logger.info("设置参数 {}={}",item.getItemName(),item.getItemValue());
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
	
	private static void editProperties(File file,List<FileEditItem> items,InstallConfig config) throws FileNotFoundException, IOException{
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		boolean hasEdit = false;
		for(FileEditItem item : items){
			if(item.getItemName() == null || item.getItemValue() == null){
				logger.info("参数{},{}={}为空,设置为空字符串",item.getItemDesc(),item.getItemName(),item.getItemValue());
				item.setItemValue("");
			}else{
				item.setItemValue(ELUtils.parseTemplate(item.getItemValue(), config.getContext()));
			}
			prop.put(item.getItemName(), item.getItemValue());
			logger.info("设置参数 {}={}",item.getItemName(), item.getItemValue());
			hasEdit = true;
		}
		if(hasEdit){
			//使用生成临时文件+文本处理的方式，避免打乱己有的注释及格式
			File tmp = new File(file.getAbsolutePath() + ".t");
			FileOutputStream fos = new FileOutputStream(tmp);
			prop.store(fos, null);
			fos.flush();
			fos.close();
			//原配置文件各行
			List<String> lines = new LinkedList<String>();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null){
				lines.add(line);
			}
			br.close();
			//修改的配置行
			Map<String,String> kvMap = new HashMap<String, String>();
			br = new BufferedReader(new FileReader(tmp));
			while((line = br.readLine()) != null){
				if(line.trim().startsWith("#") || "".equals(line.trim())){
					continue;
				}
				String[] ls = line.split("=");
				for(FileEditItem item : items){
					if(item.getItemName().equals(ls[0])){
						kvMap.put(ls[0], null);
						if(ls.length >= 2){
							kvMap.put(ls[0], ls[1]);
						}
					}
				}
			}
			br.close();
			tmp.delete();
			//写入配置文件
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for(String li : lines){
				if(li.trim().startsWith("#") || "".equals(li.trim())){
					bw.write(li);
				}else{
					String[] ls = li.split("=");
					if(kvMap.containsKey(ls[0].trim())){
						bw.write(ls[0] + "=" + kvMap.get(ls[0]));
					}else{
						bw.write(li);
					}
				}
				bw.write("\n");
			}
			bw.close();
		}
	}
	
	@Override
	public String info(InstallConfig config) {
		File file = new File(FileUtils.pathFormat(fileEditInfo.getFile(), config.getContext()));
		String pattern = "修改配置文件:{0}";
		String itemPattern = "\n【{0}】:   {1} = {2}";
		StringBuilder info = new StringBuilder(MessageFormat.format(pattern, file.getAbsolutePath()));
		List<FileEditItem> items = fileEditInfo.getItems();
		for(FileEditItem fi : items){
			info.append(MessageFormat.format(itemPattern, fi.getItemDesc(),fi.getItemName(),fi.getItemValue()));
		}
		return info.toString();
	}

	public FileEditInfo getFileEditInfo() {
		return fileEditInfo;
	}

	public void setFileEditInfo(FileEditInfo fileEditInfo) {
		this.fileEditInfo = fileEditInfo;
	}

	
}
