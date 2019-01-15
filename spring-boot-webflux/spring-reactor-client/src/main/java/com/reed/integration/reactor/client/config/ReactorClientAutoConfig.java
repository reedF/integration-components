package com.reed.integration.reactor.client.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 直连模式:直接配置reactor.client.url=reactor-server
 * @author reed
 *
 */
@Configuration
public class ReactorClientAutoConfig extends AbstractReactorClientConfig {

	@Bean
	@ConditionalOnProperty(prefix = "reactor.client", name = "url", matchIfMissing = false)
	public WebClient.Builder webClientBuilder() {
		WebClient.Builder b = null;
		if (StringUtils.isNotBlank(url)) {
			b = WebClient.builder().baseUrl(url);
		}
		return b;
	}
}
