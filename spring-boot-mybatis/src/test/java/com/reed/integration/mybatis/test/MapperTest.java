package com.reed.integration.mybatis.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.reed.integration.mybatis.DemoApplication;
import com.reed.integration.mybatis.mapper.TbUserInfoMapper;
import com.reed.integration.mybatis.model.TbUserInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MapperTest {

	@Autowired
	private TbUserInfoMapper mapper;

	@Test
	public void test() {
		TbUserInfo record = new TbUserInfo();
		record.setId(0);
		int r = mapper.selectCount(record);
		Assert.assertEquals(0, r);
	}

	@Test
	public void testByPage() {
		// 获取第1页，1条内容，默认查询总数count
		PageHelper.startPage(1, 1);
		// 紧跟着的第一个select方法会被分页
		List<TbUserInfo> list = mapper.selectAll();
		Assert.assertEquals(1, list.size());
		// 分页时，实际返回的结果list类型是Page<E>，如果想取出分页信息，需要强制转换为Page<E>
		Assert.assertEquals(2, ((Page<TbUserInfo>) list).getTotal());
	}

	/**
	 * 使用PageInfo
	 */
	@Test
	public void testByPageInfo() {
		// 获取第2页，1条内容，默认查询总数count
		PageHelper.startPage(2, 1);
		// 紧跟着的第一个select方法会被分页
		List<TbUserInfo> list = mapper.selectAll();
		PageInfo<TbUserInfo> page = new PageInfo<>(list);
		Assert.assertEquals(2, page.getPageNum());
		// 分页时，实际返回的结果list类型是Page<E>，如果想取出分页信息，需要强制转换为Page<E>
		Assert.assertEquals(2, page.getTotal());
	}
}