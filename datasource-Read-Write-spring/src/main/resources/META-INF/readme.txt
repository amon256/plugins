软件说明
  基于spring实现的数据源读写分离插件
  使用@Transaction(readOnly=true)注明方法使用读库，否则使用写库
使用说明
  spring配置
			  	<aop:aspectj-autoproxy />	
				<bean id="readWriteDataSource" class="org.apache.commons.dbcp.BasicDataSource">
					<property name="driverClassName" value="com.mysql.jdbc.Driver"/>
					<property name="url" value="jdbc:mysql://localhost:3306/rwsplit_w?useUnicode=true&amp;characterEncoding=utf-8"/>
					<property name="username" value="root"/>
					<property name="password" value="root"/>
				</bean>
				<bean id="readOnlyDataSource" class="org.apache.commons.dbcp.BasicDataSource">
					<property name="driverClassName" value="com.mysql.jdbc.Driver"/>
					<property name="url" value="jdbc:mysql://localhost:3306/rwsplit_r?useUnicode=true&amp;characterEncoding=utf-8"/>
					<property name="username" value="root"/>
					<property name="password" value="root"/>
				</bean>
				<bean id="dataSource" class="plugins.datasource.readwrite.ReadWriteDataSource">
					<property name="writeDataSource" value="readWriteDataSource"/>
					<property name="readDataSources">
						<list>
							<value>readOnlyDataSource</value>
							<value>……</value>
							<value>……</value>
							……
						</list>
					</property>
				</bean>
				
	java
		需要切面编程
		建一个切面类{A}，继承plugins.datasource.readwrite.ReadWriteProcessor,重写切面方法，将其环绕对应的事务层包
				@Aspect
				@Component
				public class A extends ReadWriteProcessor{
					@Around(value="execution(public * yourpackage.*.*(*))")
					public Object readWriteProcess(ProceedingJoinPoint point){
						super.readWriteProcess(ProceedingJoinPoint point);
					}
				}
				
		xml方式自行配置