package com.reed.integration.ignite.cache;

import java.util.concurrent.TimeUnit;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.support.IgniteRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.reed.integration.ignite.model.Person;

/**
 * Every {@link IgniteRepository} is bound to a specific Apache Ignite that it communicates to in order to mutate and
 * read data via Spring Data API. To pass an instance of Apache Ignite cache to an {@link IgniteRepository} it's
 * required to initialize {@link IgniteRepositoryFactoryBean} with on of the following:
 * <ul>
 * <li>{@link Ignite} instance bean named "igniteInstance"</li>
 * <li>{@link IgniteConfiguration} bean named "igniteCfg"</li>
 * <li>A path to Ignite's Spring XML configuration named "igniteSpringCfgPath"</li>
 * <ul/>
 * In this example the first approach is utilized.
 */
@Configuration
// @AutoConfigureAfter(IgniteStoreCacheConfig.class)
public class IgniteCacheConfig {

	public static final String CFG_BEAN_NAME = "spring-data-ignite";
	public static final String CACHE_BEAN_NAME = "spring-data-cache";
	public static final String IGNITE_NODE_NAME = "ignite-spring-node";
	public static final String IGNITE_CACHE_NAME = "PersonCache";
	public static final String DEFAULT_IGNITE_INSTANCE = "igniteInstance";

	@Autowired
	private ApplicationContext ctx;

	/**
	 * ignite启动时，会同时在浏览器开启H2控制台
	 * 注：需要点击控制台中的刷新按钮，因为有可能控制台在数据库对象初始化之前打开
	 */
	public static void startH2Webconsole() {
		// start ignite h2 web console
		System.setProperty("IGNITE_H2_DEBUG_CONSOLE", "true");
	}

	/**
	 * Creating Apache Ignite instance bean. A bean will be passed to {@link IgniteRepositoryFactoryBean} to initialize
	 * all Ignite based Spring Data repositories and connect to a cluster.
	 * @throws IgniteCheckedException 
	 */
	@Bean(DEFAULT_IGNITE_INSTANCE)
	// @Primary
	public Ignite igniteInstance(@Qualifier(CFG_BEAN_NAME) IgniteConfiguration cfg) throws IgniteCheckedException {
		startH2Webconsole();
		// Ignite ignite = Ignition.start(cfg);
		//注：使用IgniteSpring启动ignite，否则会报“Spring application context resource is not injected”
		Ignite ignite = IgniteSpring.start(cfg, ctx);
		return ignite;
	}

	@Bean(name = CFG_BEAN_NAME)
	public IgniteConfiguration igniteConfiguration(@Qualifier(CACHE_BEAN_NAME) CacheConfiguration<Long, Person> ccfg) {
		IgniteConfiguration cfg = new IgniteConfiguration();

		// Setting some custom name for the node.
		cfg.setIgniteInstanceName(IGNITE_NODE_NAME);

		// Enabling peer-class loading feature.
		cfg.setPeerClassLoadingEnabled(true);

		cfg.setCacheConfiguration(ccfg);

		return cfg;
	}

	@Bean(name = CACHE_BEAN_NAME)
	public CacheConfiguration<Long, Person> cacheConfiguration() {
		// Defining and creating a new cache to be used by Ignite Spring Data
		// repository.
		CacheConfiguration<Long, Person> ccfg = new CacheConfiguration<>(IGNITE_CACHE_NAME);
		// 注：默认是分区模式，此时使用H2 debug webconsole查询数据时，每个ignite节点存储的数据不是全部；
		// 配置为副本模式后，则为全部
		ccfg.setCacheMode(CacheMode.REPLICATED);
		ccfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
		// 过期时间
		ccfg.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(getExpire()));
		// Setting SQL schema for the cache.
		ccfg.setIndexedTypes(Long.class, Person.class);

		return ccfg;
	}

	// default name is igniteCache for IgniteRepository to autowire
	@Bean
	public IgniteCache<Long, Person> igniteCache(@Qualifier(DEFAULT_IGNITE_INSTANCE) Ignite ignite,
			@Qualifier(CACHE_BEAN_NAME) CacheConfiguration<Long, Person> cfg) {
		return ignite.getOrCreateCache(cfg).withExpiryPolicy(new CreatedExpiryPolicy(getExpire()));
	}

	public static Duration getExpire() {
		return new Duration(TimeUnit.MINUTES, 30);
	}
}