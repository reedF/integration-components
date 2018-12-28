package com.reed.integration.reactor.functions;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * RouterFunction模式定义reactor路由映射， 相当于@RequestMapping
 * 用来判断什么样的url映射到那个具体的HandlerFunction，输入为请求路由地址，输出为装在Mono里边的处理该路由的Handlerfunction
 * @author reed
 * 
 *@EnableWebFlux使用：it not enables the webflux function but means to disable all auto-configuration
 * refer to https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-webflux-auto-configuration
 * 
 * 1.If you want to keep Spring Boot WebFlux features and you want to add additional WebFlux configuration, 
 * you can add your own @Configuration class of type WebFluxConfigurer but without @EnableWebFlux.
 * refer to https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/web-reactive.html#webflux-config
 * 
 * 2.If you want to take complete control of Spring WebFlux, 
 * you can add your own @Configuration annotated with @EnableWebFlux.
 * 
 * 注：spring-boot已自动提供了多webflux的自动化配置支持，对大部分应用已满足使用，只有以上情况2下，才需要使用@EnableWebFlux
 */
@Configuration
// @EnableWebFlux
public class RouterConfig {
	@SuppressWarnings("rawtypes")
	@Autowired
	private EventHandler eventHandler;

	@SuppressWarnings("unchecked")
	@Bean
	public RouterFunction<ServerResponse> eventRouter() {
		return route(GET("/sse"), req -> eventHandler.getSse(req))
				//
				.andRoute(RequestPredicates.POST("/loadevents"), eventHandler::loadEvents)
				//
				.andRoute(GET("/getevents"), eventHandler::getEvents);
	}
}
