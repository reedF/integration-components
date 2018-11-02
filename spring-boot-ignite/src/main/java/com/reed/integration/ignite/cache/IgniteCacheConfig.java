package com.reed.integration.ignite.cache;

import java.util.concurrent.TimeUnit;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.support.IgniteRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class IgniteCacheConfig {

	public static final String CFG_BEAN_NAME = "spring-data-ignite";
	public static final String CACHE_BEAN_NAME = "spring-data-cache";
	public static final String IGNITE_NODE_NAME = "ignite-spring-node";
	public static final String IGNITE_CACHE_NAME = "PersonCache";

	/**
	 * Creating Apache Ignite instance bean. A bean will be passed to {@link IgniteRepositoryFactoryBean} to initialize
	 * all Ignite based Spring Data repositories and connect to a cluster.
	 */
	@Bean
	public Ignite igniteInstance(@Qualifier(CFG_BEAN_NAME) IgniteConfiguration cfg) {
		Ignite ignite = Ignition.start(cfg);
		return ignite;
	}

	@Bean(name = CFG_BEAN_NAME)
	public IgniteConfiguration igniteConfiguration(CacheConfiguration<Long, Person> ccfg) {
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
		// 过期时间
		ccfg.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(getExpire()));
		// Setting SQL schema for the cache.
		ccfg.setIndexedTypes(Long.class, Person.class);

		return ccfg;
	}

	@Bean
	public IgniteCache<Long, Person> igniteCache(Ignite ignite,
			@Qualifier(CACHE_BEAN_NAME) CacheConfiguration<Long, Person> cfg) {
		return ignite.getOrCreateCache(cfg).withExpiryPolicy(new CreatedExpiryPolicy(getExpire()));
	}

	public Duration getExpire() {
		return new Duration(TimeUnit.MINUTES, 30);
	}
}