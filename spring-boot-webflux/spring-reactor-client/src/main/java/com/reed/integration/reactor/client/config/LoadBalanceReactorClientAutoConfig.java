package com.reed.integration.reactor.client.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * 负载均衡模式：使用服务发现自动发现reactor server
 * @author reed
 *
 */
@ConditionalOnClass({ LoadBalancerClient.class })
@EnableDiscoveryClient
@Configuration
public class LoadBalanceReactorClientAutoConfig extends AbstractReactorClientConfig {

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}

}
