package com.reed.druid.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class QueryDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void testQuery(String s) {
		jdbcTemplate.execute("select sleep(2)");
		log.info("========={} query done==========",s);
	}
	
	public void testQuerySlow(String s) {
		jdbcTemplate.execute("select sleep(60)");
		log.info("========={} slow query done==========",s);
	}
}
