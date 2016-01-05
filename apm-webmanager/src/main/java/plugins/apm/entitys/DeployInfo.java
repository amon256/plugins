/**
 * 
 */
package plugins.apm.entitys;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 应用部署信息
 * @author fengmengyue
 *
 */
@Entity
@Table(name="deployinfo")
public class DeployInfo extends DataEntity {
	private static final long serialVersionUID = -2771813702999356684L;

	/**
	 * 应用
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="applicationId")
	private Application application;
	
	/**
	 * 主机
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="machineId")
	private HostMachine machine;
	
	/**
	 * 部署标识,对主机唯一
	 */
	private String identity;
	
	/**
	 * 描述
	 */
	private String description;
	
	public DeployInfo(){
	}
	
	public DeployInfo(Application application,HostMachine machine){
		this.application = application;
		this.machine = machine;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public HostMachine getMachine() {
		return machine;
	}

	public void setMachine(HostMachine machine) {
		this.machine = machine;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
