package com.reed.integration.reactor.functions;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * RouterFunction模式定义reactor路由映射， 相当于@RequestMapping
 * 用来判断什么样的url映射到那个具体的HandlerFunction，输入为请求路由地址，输出为装在Mono里边的处理该路由的Handlerfunction
 * @author reed
 *
 */
@Configuration
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
