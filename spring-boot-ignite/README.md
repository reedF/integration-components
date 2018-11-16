# 快速集成spring-boot and ignite
参考：  
https://www.javacodegeeks.com/2017/07/apache-ignite-spring-data.html#  

# ignite使用
中文Doc:https://liyuj.gitee.io/  
官方样例：  
https://github.com/apache/ignite/tree/master/examples/src/main/java/org/apache/ignite/examples  
1.集群监控：  
可使用监控脚本：ignitevisorcmd.bat，下载地址：https://ignite.apache.org/download.cgi  
注：下载的apache-ignite-fabric-X.x.x-bin.zip版本需与应用程序使用的ignite版本一致，否则无法使用  

# springboot2.X与1.x调整
https://blog.csdn.net/yalishadaa/article/details/79400916


# ignite-websession
使用ignite保存tomcat session，分布式部署下，多个tomcat实例间的session共享  
https://apacheignite-mix.readme.io/docs/web-session-clustering  
https://github.com/reedF/ignite-components  
https://github.com/ltlayx/SpringBoot-Ignite     
注：  
1.springboot-1.X与2.X对server的配置项命名不同  
(1)1.x:使用server.X  
(2)2.x:更新为server.servlet.X  

2.server.servlet.context-parameters.IgniteConfigurationFilePath指定的配置文件，路径必须在META-INF路径下  


# ignite-cache
https://apacheignite-mix.readme.io/docs/spring-caching


# ignite-spring-data
https://github.com/pavankjadda/ApacheIgnite-SpringBoot
https://apacheignite-mix.readme.io/docs/spring-data  
注：  
1.版本兼容问题：  
ignite-spring-data官方提供的2.X版本默认仅支持spring-data-1.X,使用spring-data-commons-2.X时会报异常：   
Parameter 0 of constructor in org.apache.ignite.springdata.repository.support.IgniteRepositoryImpl required a bean of type 'org.apache.ignite.IgniteCache' that could not be found.  
具体issue：
https://issues.apache.org/jira/browse/IGNITE-8382  
https://issues.apache.org/jira/browse/IGNITE-6879  
官方说明ignite在2.7版本会修复
临时解决方案：
(1).降级springboot版本为1.5.10
(2).使用修复jar：ignite-spring-data_2.0，
参见：https://github.com/apache/ignite/tree/master/modules/spring-data-2.0  

2.Spring context无法注入问题：  
启动ignite两种方式：  
 * (1)使用xml启动:  
 Ignition.start("ignite/ignite-store.xml");  此时ignite会交由spring容器管理  
 * (2)使用注解模式：  
 使用@Configuration与@Bean时，需使用IgniteSpring启动ignite，否则会报“Spring application context resource is not injected”  


# ignite-igfs
ignite文件系统  
https://github.com/apache/ignite/blob/master/examples/src/main/java/org/apache/ignite/examples/igfs/IgfsExample.java

# ignite计算网格  
https://liyuj.gitee.io/doc/java/ComputeGrid.html 
demo:https://github.com/srecon/the-apache-ignite-book/tree/master/chapters/chapter-7  
 
# ignite服务网格  
https://liyuj.gitee.io/doc/java/ServiceGrid.html    

# ignite流处理  
https://github.com/srecon/the-apache-ignite-book/tree/master/chapters/chapter-8  
https://github.com/samaitra/streamers  





