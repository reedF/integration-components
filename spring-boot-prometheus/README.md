# 快速集成spring-boot1.X或2.X与prometheus
参考：  
springboot-1.X:https://blog.csdn.net/fly910905/article/details/78618969  
springboot-2.X:https://blog.csdn.net/MyHerux/article/details/80667524  
https://www.jianshu.com/p/afc3759e75b9


# 集成prometheus及其组件  
1.Spring-boot-Actuator整合prometheus  
配置pom parent为  springboot2.0.1  
适用于springboot2.0以后版本，Actuator本身可集成prometheus  

		<!-- https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-prometheus -->
		<dependency>
			<groupId>io.micrometer</groupId>
			<artifactId>micrometer-registry-prometheus</artifactId>
			<version>1.0.6</version>
		</dependency>
		
		
2.自定义prometheus注解，定制metric监控项  
配置pom parent为  springboot1.5.4  
适用于springboot2.0之前版本，使用自定义注解，加注在需监控的方法签名上  
需启动时多使用两个注解：  
@EnablePrometheusEndpoint  
@EnableSpringBootMetricsCollector  

		<!-- https://mvnrepository.com/artifact/io.prometheus/simpleclient_spring_boot -->
		<dependency>
			<groupId>io.prometheus</groupId>
			<artifactId>simpleclient_spring_boot</artifactId>
			<version>0.5.0</version>
		</dependency>


# prometheus docker image使用
1.docker pull prom/prometheus

2.配置prometheus文件prometheus.yml  
3.启动  
docker run -d --name=prometheus -p 9090:9090 -v /c/Users/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus --config.file=/etc/prometheus/prometheus.yml


4.访问：http://192.168.59.103:9090/targets  


# Nacos使用
refer to:
https://github.com/nacos-group    
https://github.com/nacos-group/nacos-spring-boot-project  
https://github.com/nacos-group/nacos-spring-project  
Nacos-server:  
https://nacos.io/zh-cn/docs/deployment.html  
https://github.com/alibaba/nacos   


