package com.reed.druid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@ImportResource(locations = { "classpath:ds-druid.xml" })
public class DruidApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(DruidApplication.class, args);
		Environment environment = ctx.getEnvironment();
		DruidDataSource ds = (DruidDataSource) ctx.getBean("dataSource");
		log.info("===========DS is:{},properties is:{}===========", ds.getUrl(), ds.getConnectProperties());
	}

}