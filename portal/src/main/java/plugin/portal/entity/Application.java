/**
 * Application.java.java
 * @author FengMy
 * @since 2015年9月28日
 */
package plugin.portal.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**  
 * 功能描述：应用
 * 
 * @author FengMy
 * @since 2015年9月28日
 */
@Document(collection="applications")
public class Application extends DataEntity {
	private static final long serialVersionUID = -8291685424189253735L;

	private String name;
	
	private String description;
	
	private String url;
	
	private String ticketName = "ticket";

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}

}
