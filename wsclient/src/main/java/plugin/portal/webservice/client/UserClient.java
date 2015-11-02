/**
 * 
 */
package plugin.portal.webservice.client;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import plugin.portal.entity.User;
import plugin.portal.webservice.UserFacade;

/**  
 * <b>System：</b>NDMP<br/>
 * <b>Title：</b>UserClient.java<br/>
 * <b>Description：</b> 对功能点的描述<br/>
 * <b>@author： </b>fengmengyue<br/>
 * <b>@date：</b>2015年10月22日 下午3:49:03<br/>  
 * <b>@version：</b> 1.0.0.0<br/>
 * <b>Copyright (c) 2015 ASPire Tech.</b>   
 *   
 */
public class UserClient {

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://localhost:8080/portal/services/User?wsdl");
		Service service = Service.create(url, new QName("http://webservice.portal.plugin/", "UserFacadeImplService"));
		UserFacade facade = service.getPort(UserFacade.class);
		List<User> users =  facade.findUsers();
		if(users != null){
			for(User user : users){
				System.out.println(user.getAccount());
			}
		}
		
		System.out.println("=====");
		
		JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
		Client client = factory.createClient("http://localhost:8080/portal/services/User?wsdl");
		Object[] res = client.invoke("findUserByAccount", "ndmpps");
		if(res != null){
			for(Object obj : res){
				System.out.println(obj);
			}
		}
	}

}
