package com.reed.integration.ignite.repository;

import java.util.List;

import javax.cache.Cache;

import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.config.Query;
import org.apache.ignite.springdata.repository.config.RepositoryConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.reed.integration.ignite.cache.IgniteCacheConfig;
import com.reed.integration.ignite.model.Person;

@RepositoryConfig(cacheName = IgniteCacheConfig.IGNITE_CACHE_NAME)
public interface PersonRepository extends IgniteRepository<Person, Long> {

	/**
	 * Find a person with given name in Ignite DB.
	 * @param name Person name.
	 * @return The person whose name is the given name.
	 */
	public Person findByName(String name);

	/**
	 * Returns top Person with the specified name.
	 * 注：使用Cache.Entry做返回类型时，不能依赖spring-boot-devtools，否则会有类型转换错误
	 * @param name Person name.
	 * @return Person that satisfy the query.
	 */
	public Cache.Entry<Long, Person> findTopByNameLike(String name);

	public List<Person> findTop2ByNameLike(String name);

	public List<Person> findByMale(Boolean male, Pageable pageable);

	@Query("SELECT * FROM person")
	public List<Person> findAllByPage(Pageable pageable);

	@Query("SELECT id FROM person WHERE male = ?")
	public List<Long> getIdsByMale(Boolean male);

}
