package com.reed.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.reed.druid.DruidApplication;
import com.reed.druid.dao.QueryDao;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DruidApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DaoTest {

	@Autowired
	private QueryDao dao;

	@Before
	public void setUp() {

	}

	@Test
	public void test() {
		dao.testQuery(Thread.currentThread().getName());
	}

	/**
	 * 多线程并发
	 */
	@Test
	public void testMore() {
		int num = 10;
		CountDownLatch cdl = new CountDownLatch(num);
		ExecutorService es = Executors.newFixedThreadPool(num);
		for (int i = 0; i < num; i++) {
			es.execute(new CountDownLatchTask(cdl, dao));
		}

		System.out.println("主线程:" + Thread.currentThread().getName() + "countdown自减开始。。。");
		for (int j = 0; j < num; j++) {
			cdl.countDown();
		}
		System.out.println("主线程:" + Thread.currentThread().getName() + "countdown自减完成。。。");
		es.shutdown();

		try {
			Thread.sleep(num * 2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("============主线程退出=================");
	}

	/**
	 * 同时并发执行
	 * @author reed
	 *
	 */
	public class CountDownLatchTask implements Runnable {
		CountDownLatch latch;
		QueryDao dao;

		public CountDownLatchTask(CountDownLatch latch, QueryDao dao) {
			super();
			this.latch = latch;
			this.dao = dao;
		}

		@Override
		public void run() {
			String s = Thread.currentThread().getName();
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.out.println("当前线程:" + s + "等待结束, 开始模拟请求， 开始时间:" + System.currentTimeMillis());
			}
			dao.testQuery(s);

		}

	}
}