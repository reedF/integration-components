package com.reed.integration.prometheus.common;

import java.lang.annotation.*;
/**
 * 自定义Prometheus注解，用于自定义metric监控点
 * @author reed
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrometheusMetrics {
	/**
	 *  默认为空,程序使用method signature作为Metric name
	 *  如果name有设置值,使用name作为Metric name
	 * @return
	 */
	String name() default "";
}