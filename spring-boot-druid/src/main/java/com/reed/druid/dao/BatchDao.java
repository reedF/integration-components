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
	public static final String SQL_INSERT_TB_DB =
			//
			"REPLACE INTO tb_user_info(id,name,createTime,updateTime) "
					//
					+ " VALUES(:id,CONCAT('${PREFIX}',:name),:createTime,:updateTime) ";

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
	 * 批量添加或更新
	 * @param list
	 * @return
	 */
	@Transactional
	public int batchDbInsert(List<Map<String, Object>> list) {
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
}
