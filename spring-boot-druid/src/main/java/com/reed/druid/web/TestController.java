package com.reed.druid.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.pool.DruidDataSource;
import com.reed.druid.dao.QueryDao;

/**
 * 
 * @author reed
 *
 */
@RestController
public class TestController {

	@Autowired
	private QueryDao dao;

	@Autowired
	private DruidDataSource ds;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntity<String>> test(HttpServletRequest request) {
		String s = Thread.currentThread().getName();
		dao.testQuery(s);
		ResponseEntity<String> data = new ResponseEntity<>(s, HttpStatus.OK);
		ResponseEntity<ResponseEntity<String>> r = new ResponseEntity<>(data, HttpStatus.OK);
		return r;
	}

	@RequestMapping(value = "/slow", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntity<String>> slow(HttpServletRequest request) {
		String s = Thread.currentThread().getName();
		dao.testQuerySlow(s);
		ResponseEntity<String> data = new ResponseEntity<>(s, HttpStatus.OK);
		ResponseEntity<ResponseEntity<String>> r = new ResponseEntity<>(data, HttpStatus.OK);
		return r;
	}

	@RequestMapping(value = "/ds", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntity<String>> ds() {

		ResponseEntity<String> data = new ResponseEntity<>(ds.getConnectProperties().getProperty("isFailfast"),
				HttpStatus.OK);
		ResponseEntity<ResponseEntity<String>> r = new ResponseEntity<>(data, HttpStatus.OK);
		return r;
	}

}