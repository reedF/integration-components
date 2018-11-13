package com.reed.integration.ignite.cache;

import java.sql.Types;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.sql.DataSource;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.JdbcType;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.dialect.H2Dialect;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.reed.integration.ignite.model.Person;

/**
 * Ingite持久化缓存
 * 注：
 * 启动ignite两种方式：
 * (1)xml:Ignition.start("ignite/ignite-store.xml");
 * (2)注解：使用@Configuration与@Bean时，需使用IgniteSpring启动ignite，否则会报“Spring application context resource is not injected”
 * @author reed
 *
 */
// @Profile("ignite-store")
@Configuration
public class IgniteStoreCacheConfig {

	public static final String IGNITE_STORE_CACHE_NAME = "PersonStoreCache";
	public static final String STORE_CACHE_BEAN_NAME = "ignite-store-cache";
	public static final String STORE_NODE_NAME = "ignite-spring-store-node";
	public static final String CFG_STORE_CACHE_BEAN_NAME = "cfg-store-cache";
	public static final String STORE_FACTORY_BEAN_NAME = "store-cache-factory";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ApplicationContext ctx;

	/**
	 * start from xml
	 * @return
	 */
	// @Bean(STORE_NODE_NAME)
	public Ignite igniteInstance() {
		IgniteCacheConfig.startH2Webconsole();
		return Ignition.start("ignite/ignite-store.xml");
	}

	@Bean(STORE_NODE_NAME)
	public Ignite igniteInstance(@Qualifier(CFG_STORE_CACHE_BEAN_NAME) CacheConfiguration<Long, Person> cacheCfg)
			throws IgniteCheckedException {
		IgniteCacheConfig.startH2Webconsole();
		IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
		igniteConfiguration.setIgniteInstanceName(STORE_NODE_NAME);
		// 对等类加载，default is false.适用于分布式部署，
		// 实现多个ignite节点间字节码交换，不需要在每个节点都重新部署，适用于分布式计算任务的部署
		// 集群内如某个节点开启此项，则集群内其他节点也同样必须开启，生成环境不建议开启.
		igniteConfiguration.setPeerClassLoadingEnabled(true);
		igniteConfiguration.setCacheConfiguration(cacheCfg);
		// return Ignition.start(igniteConfiguration);
		return IgniteSpring.start(igniteConfiguration, ctx);
	}

	@Bean(name = CFG_STORE_CACHE_BEAN_NAME)
	public CacheConfiguration<Long, Person> cacheConfiguration() {
		CacheConfiguration<Long, Person> cacheConfig = new CacheConfiguration<>(IGNITE_STORE_CACHE_NAME);
		cacheConfig.setIndexedTypes(Long.class, Person.class);
		cacheConfig.setWriteBehindEnabled(true);
		// 通读,当缓存无效时会从底层的持久化存储中读取
		cacheConfig.setReadThrough(true);
		// 通写,当缓存更新时会自动地进行持久化
		cacheConfig.setWriteThrough(true);
		cacheConfig.setCacheMode(CacheMode.REPLICATED);
		cacheConfig.setCacheStoreFactory(storeFactory());

		return cacheConfig;
	}

	@Bean(name = STORE_CACHE_BEAN_NAME)
	public IgniteCache<Long, Person> igniteCache(@Qualifier(STORE_NODE_NAME) Ignite ignite,
			@Qualifier(CFG_STORE_CACHE_BEAN_NAME) CacheConfiguration<Long, Person> cacheCfg) {

		return ignite.getOrCreateCache(cacheCfg)
				.withExpiryPolicy(new CreatedExpiryPolicy(IgniteCacheConfig.getExpire()));
	}

	// @Bean(STORE_FACTORY_BEAN_NAME)
	public CacheJdbcPojoStoreFactory<Long, Person> storeFactory() {

		CacheJdbcPojoStoreFactory<Long, Person> factory1 = new CacheJdbcPojoStoreFactory<>();

		// factory1.setDataSource(dataSource);
		factory1.setDataSourceBean("dataSource");
		// factory1.setDialect(new MySQLDialect());
		factory1.setDialect(new H2Dialect());
		JdbcType jdbcType = new JdbcType();
		jdbcType.setCacheName(IGNITE_STORE_CACHE_NAME);
		jdbcType.setKeyType(Long.class);
		jdbcType.setValueType(Person.class);
		jdbcType.setDatabaseTable("person");
		jdbcType.setDatabaseSchema("ignite");
		jdbcType.setKeyFields(new JdbcTypeField(Types.BIGINT, "id", Long.class, "id"));
		jdbcType.setValueFields(new JdbcTypeField(Types.VARCHAR, "name", String.class, "name"),
				new JdbcTypeField(Types.BOOLEAN, "male", Boolean.class, "male"));

		factory1.setTypes(jdbcType);
		return factory1;
	}
}