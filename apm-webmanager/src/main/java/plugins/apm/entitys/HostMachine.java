/**
 * 
 */
package plugins.apm.entitys;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 主机信息
 * @author fengmengyue
 *
 */
@Entity
@Table(name="hostmachine")
public class HostMachine extends DataEntity {
	private static final long serialVersionUID = -2444504635588319421L;
	
	/**
	 * 主机IP或域名
	 */
	private String host;
	
	/**
	 * 主机接口开放端口
	 */
	private String port;
	
	/**
	 * 主机名称
	 */
	private String name;
	
	/**
	 * 备注
	 */
	private String description;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
