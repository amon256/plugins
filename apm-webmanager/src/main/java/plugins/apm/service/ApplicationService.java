/**
 * ApplicationService.java.java
 * @author FengMy
 * @since 2015年12月8日
 */
package plugins.apm.service;

import javax.transaction.Transactional;

import plugins.apm.entitys.Application;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月8日
 */
@Transactional
public interface ApplicationService extends IService<Application> {
}
