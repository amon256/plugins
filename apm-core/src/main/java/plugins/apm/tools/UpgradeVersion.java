/**
 * 
 */
package plugins.apm.tools;


/**
 * @author fengmengyue
 *
 */
public interface UpgradeVersion {

	public String getNumber();

	public String getFile();
	
	public void setFile(String file);

	public String getFileName();
	public void setFileName(String fileName);

	public String getConfigFile();
	public void setConfigFile(String configFile);
	public String getConfigFileName();
	public void setConfigFileName(String configFileName);
	public UpgradeApplication getApplication();
	

}
