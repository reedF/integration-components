package com.reed.integration.reactor.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import lombok.Data;

/**
 * reactor client端配置基类
 * @author reed
 *
 */
@Data
@ComponentScan(basePackages = { "com.reed.integration.reactor.client" })
@ConfigurationProperties(prefix = "reactor.client")
@ConditionalOnProperty(prefix = "reactor.client", name = "enabled", havingValue = "true", matchIfMissing = false)
public abstract class AbstractReactorClientConfig {

	@Value("url")
	public String url;

}
