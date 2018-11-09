## 验证MHA切换后导致应用无可用连接的问题
问题描述：  
数据源连接池在DB vip切换后无可用连接，应用一直等待问题  
注：此问题不限于使用Druid数据连接池，其他连接池也有类似问题，同样使用域名模式配置jdbc url时也会有此问题：  
如在数据库实例迁移时，域名指向的真实DB IP发生变化，但连接池还是无法感知，  
# 建议对所有应用的jdbc url配置都加入socketTimeout

参考：
https://www.dozer.cc/2015/07/mha.html 
https://blog.csdn.net/zero__007/article/details/51523297  
https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html  
 
## 重现步骤
1.jdbc url使用vip连接数据库，同时不配置socketTimeout，默认0，不断开  

2.配置Druid连接池，maxActive为1，方便发送一个请求测试连接池打满（实际情况可配置为更多，通过并发请求打满）  

3.发送慢请求，执行sql查询，打满连接池  

4.在慢请求响应返回前，执行DB集群vip漂移  

5.观察请求状态，在主库不停止的状态，connection应该保持，请求还可正常返回；原库如停止，则connection显示timeout 

6.此时vip已漂移到备库，主库已宕机  

7.此时观察备库，无任何新连接；观察主库，如未停机，则连接数保持，原有请求可继续处理；

8.再次发送请求，执行sql，显示异常"Failed to obtain JDBC Connection; nested exception is com.alibaba.druid.pool.GetConnectionTimeoutException: wait millis 10000, active 0, maxActive 1, creating 1"  
原因：
此时由于之前的请求打满连接池，ip漂移时连接池无感知，继续保持连接占满，虽然db已迁移到备库，但连接池内的connection依然保持未释放，导致无法获取到新连接，观察备库，无任何新连接，应用一直等待    

9.Vip漂移后，此时后续分两种情况：
（1）主库停机：原有请求无法获取响应，报错；同时由于vip已漂移到备库，连接池无感知，则再发新请求出现步骤8情况  
（2）主库不停机：原有请求可继续获取响应，正常返回，此时如重复步骤8时，应用可以获取连接，但此时的连接已建立在备库上（此时如连接池maxActive配的多时，会存在同时有主备连接的情况，即主备都可读写）

10.附加验证：如在主库未重启的情况下，将vip漂移回原库，情况与步骤9（2）的现象相同    

注：  
1.重现的关键点：  
在连接池打满的时候发生vip漂移即可重现问题  
2.vip连续漂移：  
在发生vip漂移，指向备库并接收正常请求后，此时如主库未重启就手动又漂移回主库时，连接池内会同时存在主备库的连接，出现主备同时提供链接的问题  
应避免此情况，即vip漂移应是主库自动切换备，但备库不应在主库恢复后自动再次触发vip漂移，而是监控探活自己，仅在自身宕机时才再次发生vip漂移  


## 解决方案
#需满足以下两要素：  
1.应用端：为jdbc url内配置socketTimeout=60000，设置TCP socket连接的超时时间为60s(注：此配置到期后会自动断开connection，有可能会导致sql查询耗时超出此值的请求失败)  
此情况下，vip漂移后，原连接池内的connection会在socketTimeout到期后，自动断开释放，保持连接池可重新获取连接  
2.DB集群：需配置vip路由IP策略为转发模式（不能是vip保持连接模式，必须为转发，否则socketTimeout无效）
参见：https://www.cnblogs.com/sfnz/p/6555723.html



