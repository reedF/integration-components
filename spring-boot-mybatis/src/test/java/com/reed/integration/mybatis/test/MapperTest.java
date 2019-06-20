package com.reed.integration.mybatis.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.reed.integration.mybatis.DemoApplication;
import com.reed.integration.mybatis.mapper.TbUserInfoMapper;
import com.reed.integration.mybatis.model.TbUserInfo;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MapperTest {

	@Autowired
	private TbUserInfoMapper mapper;

	@Before
	public void setUp() {
		int r = mapper.selectCount(null);
		if (r == 0) {
			List<TbUserInfo> list = new ArrayList<>();
			TbUserInfo record1 = new TbUserInfo();
			TbUserInfo record2 = new TbUserInfo();
			record1.setName("1");
			record2.setName("2");
			list.add(record1);
			list.add(record2);
			mapper.insertList(list);
		}
	}

	@Test
	// 配置回滚测试数据
	@Transactional
	public void testSave() {
		TbUserInfo record = new TbUserInfo();
		record.setName("test");
		// insert后实体会自动赋值ID
		mapper.insertSelective(record);
		Assert.assertNotNull(record.getId());
	}

	@Test
	public void test() {
		TbUserInfo record = new TbUserInfo();
		record.setId(0);
		int r = mapper.selectCount(record);
		Assert.assertEquals(0, r);
	}

	/**
	 * 通用Example查询用法，使用Weekend自动转换对应的列名
	 * 参考：https://github.com/abel533/Mapper/wiki/6.example
	 */
	@Test
	public void testByExample() {
		int r = mapper.selectCountByExample(new Example.Builder(TbUserInfo.class).where(WeekendSqls.<TbUserInfo>custom()
				.andLike(TbUserInfo::getName, "%1%").andGreaterThan(TbUserInfo::getId, "0")).build());
		Assert.assertTrue(r > 0);
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