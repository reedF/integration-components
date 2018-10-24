package com.reed.integration.prometheus.config;

//import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;

/**
 * 仅适用于springboot-1.X
 * @author reed
 *
 */
@Profile("v1.x")
@Configuration
//以下两注解等同于MonitoringConfig注释的两个Bean，仅适用于springboot-1.X，使用2.X时，无需此配置
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
class MonitoringConfig {
//	@Bean
//	SpringBootMetricsCollector springBootMetricsCollector(Collection<PublicMetrics> publicMetrics) {
//
//		SpringBootMetricsCollector springBootMetricsCollector = new SpringBootMetricsCollector(publicMetrics);
//		springBootMetricsCollector.register();
//		return springBootMetricsCollector;
//	}

	@Bean
	ServletRegistrationBean servletRegistrationBean() {
		//加载jvm metric监控
		DefaultExports.initialize();
		return new ServletRegistrationBean(new MetricsServlet(), "/prometheus");
	}
}