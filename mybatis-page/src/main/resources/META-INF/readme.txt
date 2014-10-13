软件说明
  本插件针对mybatis分页查询而编写，在mybatis3.2.7版本测试通过，其它版本未经测试。
  优点：
	实现原理为mybatis的拦截器，但是比网上目前流行的修行sql方式优化，只是第一次调用查询时需要处理，以后不需要再额外处理。
	生成的sql为自动化的最优(基于数据绑定方式的sql)。
  缺点：
	扩展需要对mybatis源码较为熟悉。


使用说明：

1、插件配置方式见META-INF/myBatis-configuration.xml

2、使用限制，仅对xml编写的MapperStatement有效，对注解方式的未作处理。

3、仅提供了oracle和mysql的方言支持，其它数据引擎需要自行实现Dialect