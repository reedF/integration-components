package com.reed.integration.prometheus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Springboot version > 2.0时，
 * 无需配置@EnablePrometheusEndpoint，@EnableSpringBootMetricsCollector
 */
@SpringBootApplication
//以下两注解仅适用于springboot-1.X，使用2.X时，无需此配置
//@EnablePrometheusEndpoint
//@EnableSpringBootMetricsCollector
public class PrometheusApplication {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		SpringApplication.run(PrometheusApplication.class, args);
		/*
		 * SpringApplicationBuilder builder = new
		 * SpringApplicationBuilder(PrometheusApplication.class);
		 * //修改Banner的模式为OFF builder.bannerMode(Banner.Mode.OFF).run(args);
		 */
	}
}
