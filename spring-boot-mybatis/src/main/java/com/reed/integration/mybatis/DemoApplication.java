package com.reed.integration.mybatis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
//使用MapperScan后无需对所有Mapper类分别添加@Mapper
@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.reed.integration.mybatis.mapper")
public class DemoApplication {
	public static Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
		Environment environment = ctx.getEnvironment();

		logger.info("<==========App started in port:{}===========>", environment.getProperty("local.server.port"));
	}

}