package com.reed.druid.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 使用jdbc template批量处理数据
 * @author reed
 *
 */
@Repository
public class BatchDao {
	public static final String TB_DB = "tb_user_info";

	// 使用REPLACE INTO根据主键执行insert or update
	// REPLACE INTO是对重复键先delete再insert，故对于存在多个唯一索引的表，在并发时易出现死锁,
	// https://siyong.sinaapp.com/2018/08/27/%E5%B9%B6%E5%8F%91replace-into%E5%AF%BC%E8%87%B4mysql%E6%AD%BB%E9%94%81/
	public static final String SQL_INSERT_TB_DB =
			//
			"REPLACE INTO tb_user_info(id,name,createTime,updateTime) "
					//
					+ " VALUES(:id,CONCAT('${PREFIX}',:name),:createTime,:updateTime) ";
	// INSERT INTO ON DUPLICATE KEY UPDATE
	// 如果一个表定义有多个唯一键或者主键时，并发时也易出现死锁
	// https://www.2cto.com/database/201711/695662.html
	public static final String SQL_INSERT_ONDUPLICATEKEY_TB_DB =
			//
			"INSERT INTO tb_user_info(id,name,createTime,updateTime) "
					//
					+ " VALUES(:id,CONCAT('${PREFIX}',:name),:createTime,:updateTime) "
					//
					+ " ON DUPLICATE KEY UPDATE "
					+ " id=:id,name=CONCAT('${PREFIX}',:name),createTime=:createTime,updateTime=:updateTime ";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("namedTemplate")
	private NamedParameterJdbcTemplate namedTemplate;

	/**
	 * 查询数据
	 * @param ts
	 * @return
	 */
	public List<Map<String, Object>> getDbsByTs(Long ts) {
		ts = ts == null ? 0 : ts;
		StringBuffer sql = new StringBuffer("select * from " + TB_DB);
		sql.append(" where updateTime > " + ts);
		sql.append(" order by updateTime");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
		return list;
	}

	/**
	 * 使用REPLACE INTO批量添加或更新
	 * @param list
	 * @return
	 */
	@Transactional
	public int batchDbReplace(List<Map<String, Object>> list) {
		int r = 0;
		if (list != null && !list.isEmpty()) {
			String sql = SQL_INSERT_TB_DB.replace("${PREFIX}", "test-");
			SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(list);
			int[] results = namedTemplate.batchUpdate(sql, params);
			for (int i : results) {
				if (i > 0) {
					r++;
				}
			}
		}
		return r;
	}

	/**
	 *  使用INSERT INTO ON DUPLICATE KEY UPDATE批量添加或更新
	 * @param list
	 * @return
	 */
	@Transactional
	public int batchDbInsert(List<Map<String, Object>> list) {
		int r = 0;
		if (list != null && !list.isEmpty()) {
			String sql = SQL_INSERT_ONDUPLICATEKEY_TB_DB.replace("${PREFIX}", "test-");
			SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(list);
			int[] results = namedTemplate.batchUpdate(sql, params);
			for (int i : results) {
				if (i > 0) {
					r++;
				}
			}
		}
		return r;
	}
}
