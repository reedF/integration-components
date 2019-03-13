package com.reed.integration.prometheus.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;

import lombok.Data;

/**
 * 测试nacos配置中心
 * 1.未使用@ConfigurationProperties时，使用@Value("${xxx:defaultValue}")方式注解属性；
 * 	   使用@ConfigurationProperties后，当配置项key值与java字段一致时，无需再使用@Value("${xxx}")注解属性,
 *   如此时使用@Value("keyName")(注：不带${}占位符)后，其作用为设置默认值
 * 2.@NacosValue无法识别@ConfigurationProperties配置的前缀，在使用@ConfigurationProperties配置前缀后：
 *   A.当配置项key值与java字段名不相同时，需使用@NacosValue配置属性key的全路径,参考test1
 *   B.当配置项key值与java字段一致时，不使用@NacosValue时，无法自动刷新，参考test3
 * @author reed
 *
 *refer to:
 *https://github.com/nacos-group/nacos-spring-boot-project
 *https://github.com/nacos-group/nacos-spring-project
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "test.nacos")
//@NacosPropertySource(dataId = "test", groupId = "g1", autoRefreshed = true)
// @EnableNacosConfig(globalProperties = @NacosProperties(namespace ="${nacos.namespace}"))
@NacosConfigurationProperties(dataId = "test", groupId = "g1", autoRefreshed= true,ignoreNestedProperties=true)
public class NacosTestConfig {
	@NacosValue(value = "${test.nacos.item1:0}", autoRefreshed = true)
	// @Value("0")
	public int test1;
	@NacosValue(value = "${test.nacos.item2}", autoRefreshed = true)
	@Value("default-value")
	public String test2;

	// @NacosIgnore
	public Boolean test3;

	@NacosValue(value = "${test.nacos.test4}", autoRefreshed = true)
	public List<String> test4;
}
