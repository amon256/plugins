/**
 * SpringContextPrepareListner.java.java
 * @author FengMy
 * @since 2015年12月10日
 */
package plugins.spring;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import plugins.upgradekit.context.SystemInitContext;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月10日
 */
@Component
@Lazy(value=false)
public class SpringContextStartedListener implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null){
			new SystemInitContext().systemInit();
		}
	}

}
