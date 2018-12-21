package com.reed.integration.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ReactorApplication {
	public static Logger logger = LoggerFactory.getLogger(ReactorApplication.class);

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(ReactorApplication.class, args);
		Environment environment = ctx.getEnvironment();

		logger.info("<==========App started in port:{}===========>", environment.getProperty("local.server.port"));
	}

}