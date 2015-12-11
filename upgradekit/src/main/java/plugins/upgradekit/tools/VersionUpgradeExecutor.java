/**
 * VersionUpgradeExecutor.java.java
 * @author FengMy
 * @since 2015年12月11日
 */
package plugins.upgradekit.tools;

import java.io.File;
import java.net.URLEncoder;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.VariableMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import plugins.upgradekit.entitys.Version;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.App;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.Cmd;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月11日
 */
public class VersionUpgradeExecutor {
	private static Logger logger = LoggerFactory.getLogger(VersionUpgradeExecutor.class);

	public static void execute(Version version,MessageWriter writer){
		String rootPath = UpgradeContext.getFileRoot();
		if(!version.getFile().startsWith(rootPath)){
			//版本文件加上根目录，变成全路径
			version.setFileName(version.getFile());
			version.setFile(rootPath + File.separator + version.getFile());
		}
		if(!version.getConfigFile().startsWith(rootPath)){
			//配置文件加上根目录，变成全路径
			version.setConfigFileName(version.getConfigFile());
			version.setConfigFile(rootPath + File.separator + version.getConfigFile());
		}
		ApplicationUpgradeConfig config = ApplicationUpgradeConfig.getInstance();
		App app = config.getApp(version.getApplication().getNumber());
		if(app != null && app.getCmds() != null){
			ExpressionFactory elFactory = new ExpressionFactoryImpl();
			ELContext elCtx = new SimpleContext();
			VariableMapper variableMapper = elCtx.getVariableMapper();
			variableMapper.setVariable("version", elFactory.createValueExpression(version, Version.class));
			for(Cmd cmd : app.getCmds()){
				String c = cmd.getCmd();
				String[] params = new String[0];
				if(cmd.getParams() != null && !cmd.getParams().isEmpty()){
					params = new String[cmd.getParams().size()];
					for(int i = 0; i < cmd.getParams().size(); i++){
						params[i] = cmd.getParams().get(i).getName() + "=" + (String) elFactory.createValueExpression(elCtx, cmd.getParams().get(i).getValue(), String.class).getValue(elCtx);
					}
				}
				//默认5分钟超时
				NativeCommandExecutor.executeNativeCommand(writer, config.getCharset()	, c, params,1000*300);
			}
		}
	}
	
	public static String messageScript(String msg,String functionName){
		try{
			msg = URLEncoder.encode(msg.trim(),"utf-8").replaceAll("\\+", "%20");
		}catch(Exception e){
			logger.error("转码异常:"+msg,e);
		}
		StringBuilder script = new StringBuilder("<script type=\"text/javascript\">");
		script.append("window.parent."+functionName+"(decodeURIComponent('"+msg+"'))");
		script.append("</script>");
		System.out.println(script.toString());
		return script.toString();
	}
}
