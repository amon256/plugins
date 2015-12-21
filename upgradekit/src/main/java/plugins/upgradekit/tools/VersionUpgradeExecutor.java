/**
 * VersionUpgradeExecutor.java.java
 * @author FengMy
 * @since 2015年12月11日
 */
package plugins.upgradekit.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.VariableMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.InstallAuto;
import plugins.installation.logs.MessageWriter;
import plugins.installation.logs.ThreadLocalMessageWriter;
import plugins.upgradekit.entitys.Version;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.App;
import plugins.upgradekit.tools.ApplicationUpgradeConfig.Cmd;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月11日
 */
public class VersionUpgradeExecutor {
	private static Logger logger = LoggerFactory.getLogger(VersionUpgradeExecutor.class);

	public static void execute(final Version version,final MessageWriter writer){
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
		if(app != null){
			if(app.getInstallCmds() != null){
				writer.write("应用配置了安装命令集，执行安装命令集");
				logger.info("应用配置了安装命令集，执行安装命令集");
				ExpressionFactory elFactory = new ExpressionFactoryImpl();
				ELContext elCtx = new SimpleContext();
				VariableMapper variableMapper = elCtx.getVariableMapper();
				variableMapper.setVariable("version", elFactory.createValueExpression(version, Version.class));
				for(Cmd cmd : app.getInstallCmds()){
					String[] params = new String[0];
					if(cmd.getParams() != null && !cmd.getParams().isEmpty()){
						params = new String[cmd.getParams().size()];
						for(int i = 0; i < cmd.getParams().size(); i++){
							params[i] = cmd.getParams().get(i).getName() + "=" + (String) elFactory.createValueExpression(elCtx, cmd.getParams().get(i).getValue(), String.class).getValue(elCtx);
						}
					}
					//默认5分钟超时
					NativeCommandExecutor.executeNativeCommand(writer, config.getCharset()	, cmd.getCmd(), params,new File(cmd.getPath()),1000*300);
				}
			}else{
				writer.write("应用未配置安装命令集，执行默认安装过程");
				logger.info("应用未配置安装命令集，执行默认安装过程");
				ThreadLocalMessageWriter.register(writer);
				try {
					if(app.getStopCmd() != null){
						writer.write("停止应用");
						NativeCommandExecutor.executeNativeCommand(writer, config.getCharset(), app.getStopCmd().getCmd(), new String[]{}, new File(app.getStopCmd().getPath()), 1000*60*5);
					}
					InstallAuto.install(version.getFile(), app.getAppRoot(), version.getConfigFile(), new HashMap<String, String>());
					if(app.getStartCmd() != null){
						writer.write("停止应用");
						NativeCommandExecutor.executeNativeCommand(writer, config.getCharset(), app.getStartCmd().getCmd(), new String[]{}, new File(app.getStartCmd().getPath()), 1000*60*5);
					}
				} catch (Exception e) {
					e.printStackTrace(new PrintWriter(new Writer() {
						@Override
						public void write(char[] cbuf, int off, int len) throws IOException {
							writer.write(new String(cbuf, off, len));
						}
						@Override
						public void flush() throws IOException {
						}
						@Override
						public void close() throws IOException {
						}
					}));
					logger.error("安装异常",e);
				}finally{
					ThreadLocalMessageWriter.remove();
				}
			}
		}else{
			logger.debug("应用未配置:{}",version.getApplication().getNumber());
			writer.write("应用编号:" + version.getApplication().getNumber() + "没有找到相应的升级配置");
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
