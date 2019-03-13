package com.reed.integration.prometheus.mvc;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reed.integration.prometheus.common.PrometheusMetrics;
import com.reed.integration.prometheus.config.NacosTestConfig;

@RestController
@RequestMapping("/apps")
public class HelloController {

	@Autowired
	private NacosTestConfig nacos;

	/**
	 * 获取全部应用
	 * @return
	 */
	@RequestMapping(value = "/list")
	public ResponseEntity<Page<String>> getAppsByPage(@RequestParam(required = false) String appName,
			@PageableDefault(size = 20, sort = { "id", "update_time" }, direction = Direction.ASC) Pageable pageable) {
		ResponseEntity<Page<String>> r = new ResponseEntity<>(null, HttpStatus.OK);
		Page<String> page = new PageImpl(Arrays.asList(appName), pageable, 10);
		if (StringUtils.isBlank(appName)) {
			r = ResponseEntity.badRequest().body(page);
		} else {
			r = ResponseEntity.ok(page);
		}
		return r;
	}

	@RequestMapping(value = "/edit")
	public ResponseEntity<ResponseEntity<String>> saveOrUpdateApp(@RequestBody String t) {
		ResponseEntity<String> data = new ResponseEntity<>(t, HttpStatus.OK);
		ResponseEntity<ResponseEntity<String>> r = new ResponseEntity<>(data, HttpStatus.OK);
		if (StringUtils.isBlank(t)) {
			r = ResponseEntity.badRequest().body(data);
		}

		return r;
	}

	@PrometheusMetrics
	@RequestMapping(value = "/view")
	public ResponseEntity<ResponseEntity<String>> getAppById(@RequestParam(name = "id", required = true) String id) {
		String t = "hello:" + id;
		ResponseEntity<String> data = new ResponseEntity<String>(t, HttpStatus.OK);
		ResponseEntity<ResponseEntity<String>> r = new ResponseEntity<>(data, HttpStatus.OK);
		if (id != null) {
			r = ResponseEntity.ok(data);
		} else {
			r = ResponseEntity.badRequest().build();
		}
		return r;
	}

	@RequestMapping(value = "/nacos")
	public ResponseEntity<ResponseEntity<String>> getNacos() {
		ResponseEntity<String> data = new ResponseEntity<String>("Nacos:" + nacos.toString(), HttpStatus.OK);
		ResponseEntity<ResponseEntity<String>> r = new ResponseEntity<>(data, HttpStatus.OK);

		r = ResponseEntity.ok(data);

		return r;
	}
}
