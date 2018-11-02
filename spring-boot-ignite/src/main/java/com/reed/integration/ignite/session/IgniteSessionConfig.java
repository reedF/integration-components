package com.reed.integration.ignite.session;

import java.util.Arrays;

import org.apache.ignite.cache.websession.WebSessionFilter;
import org.apache.ignite.startup.servlet.ServletContextListenerStartup;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 加载ignite session filter
 * @author reed
 *
 */
@Configuration
public class IgniteSessionConfig {

	@Bean
	public ServletContextListenerStartup servletContextListenerStartup() {
		ServletContextListenerStartup servletContextListenerStartup = new ServletContextListenerStartup();
		return servletContextListenerStartup;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		WebSessionFilter webSessionFilter = new WebSessionFilter();
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(webSessionFilter);
		registrationBean.setUrlPatterns(Arrays.asList("/*"));

		return registrationBean;
	}
}
