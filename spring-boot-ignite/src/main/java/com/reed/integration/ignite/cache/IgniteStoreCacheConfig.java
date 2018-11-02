package com.reed.integration.ignite.cache;

import java.sql.Types;

import javax.sql.DataSource;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.JdbcType;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.dialect.MySQLDialect;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.reed.integration.ignite.model.Person;

/**
 * Ingite持久化缓存
 * @author reed
 *
 */
@Profile("ignite-store")
@Configuration
public class IgniteStoreCacheConfig {

	public static final String IGNITE_STORE_CACHE_NAME = "PersonCacheStore";

	@Autowired
	private DataSource dataSource;

	@Bean
	public Ignite igniteInstance() {
		IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
		igniteConfiguration.setIgniteInstanceName("ignite-spring-store");
		igniteConfiguration.setPeerClassLoadingEnabled(true);

		CacheConfiguration<Long, Person> cacheConfig = new CacheConfiguration<>(IGNITE_STORE_CACHE_NAME);
		cacheConfig.setIndexedTypes(Long.class, Person.class);
		
		if (dataSource != null) {
			//setStore(cacheConfig);
		}
		
		igniteConfiguration.setCacheConfiguration(cacheConfig);
		return Ignition.start(igniteConfiguration);

	}

	private void setStore(CacheConfiguration<Long, Person> cacheConfig) {
		cacheConfig.setWriteBehindEnabled(true);
		cacheConfig.setReadThrough(true);

		CacheJdbcPojoStoreFactory<Long, Person> factory1 = new CacheJdbcPojoStoreFactory<>();
		factory1.setDataSource(dataSource);
		factory1.setDialect(new MySQLDialect());
		JdbcType jdbcType = new JdbcType();
		jdbcType.setCacheName(IGNITE_STORE_CACHE_NAME);
		jdbcType.setKeyType(Long.class);
		jdbcType.setValueType(Person.class);
		jdbcType.setDatabaseTable("person");
		jdbcType.setDatabaseSchema("ignitedemo");
		jdbcType.setKeyFields(new JdbcTypeField(Types.INTEGER, "id", Long.class, "id"));
		jdbcType.setValueFields(new JdbcTypeField(Types.VARCHAR, "name", String.class, "name"),
				new JdbcTypeField(Types.BOOLEAN, "male", Boolean.class, "male"));

		factory1.setTypes(jdbcType);
		cacheConfig.setCacheStoreFactory(factory1);
	}
}