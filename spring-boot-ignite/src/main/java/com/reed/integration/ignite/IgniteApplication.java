package com.reed.integration.ignite;

import org.apache.ignite.springdata.repository.config.EnableIgniteRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableIgniteRepositories(basePackages = { "com.reed.integration.ignite.repository" })
@ImportResource(locations = { "classpath:ignite/ignite-igfs.xml" })
public class IgniteApplication {
	public static Logger logger = LoggerFactory.getLogger(IgniteApplication.class);

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(IgniteApplication.class, args);
		Environment environment = ctx.getEnvironment();

		logger.info("<==========App started in port:{}===========>", environment.getProperty("local.server.port"));
	}

}