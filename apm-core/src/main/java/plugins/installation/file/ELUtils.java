/**
 * ELUtils.java.java
 * @author FengMy
 * @since 2015年12月11日
 */
package plugins.installation.file;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月11日
 */
public class ELUtils {

	public static String parseTemplate(String template,Object param){
		try{
			Configuration config = new Configuration();
			Template freemarker = new Template(template, new StringReader(template), config);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			freemarker.process(param, new OutputStreamWriter(out));
			out.flush();
			out.close();
			template = out.toString();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return template;
	}
}
