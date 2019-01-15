package com.reed.integration.reactor.client.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 负载均衡模式：使用服务发现自动发现reactor server
 * refer to https://piotrminkowski.wordpress.com/2018/05/04/reactive-microservices-with-spring-webflux-and-spring-cloud/
 * 使用步骤：
 * 1.reactor server端与client端需集成服务发现模块如eureka,consul等
 * 2.在client端本地配置reactor.client.discovery.serviceName,其值即为reactor server在注册中心中注册的serviceName：
 * @author reed
 *
 */
@ConditionalOnClass({ LoadBalancerClient.class })
@EnableDiscoveryClient
@Configuration
@ConditionalOnProperty(prefix = "reactor.client.discovery", name = "serviceName", matchIfMissing = false)
public class LoadBalanceReactorClientAutoConfig extends AbstractReactorClientConfig {

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder().baseUrl("http://" + serviceName);
	}

}
