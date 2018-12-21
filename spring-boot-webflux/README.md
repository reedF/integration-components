## 基于spring5 + spring-boot2.0 + spring-webflux实现响应式编程
参考：  
https://blog.csdn.net/get_set/article/details/79480233  

# reactor介绍
https://projectreactor.io/docs/core/release/reference/#getting-started-introducing-reactor  
中文Doc:https://htmlpreview.github.io/?https://github.com/get-set/reactor-core/blob/master-zh/src/docs/index.html#threading  

# spring-webflux使用
https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/web-reactive.html#spring-webflux  

spring-webflux上层支持两种开发方式实现响应式编程：  
1.注解模式：类似于Spring WebMVC的基于注解（@Controller、@RequestMapping）的开发模式  
2.RouterFunction模式：类似Java 8 lambda 风格的函数式开发模式  



# 使用注意
1.400 BAD_REQUEST "Request body is missing:"问题：  
controller中使用@RequestBody修饰 Mono<T>或Flux<T>传参时，应该使用传入参的then()方法定义返回值，来指明传输完成信号并返回，
不要创建新的Mono或直接方法签名内定义返回void，这时会导致方法直接返回完成，而没等待客户端请求上传数据，从而引发“Request body is missing”  
参见：https://jira.spring.io/browse/SPR-17463  
