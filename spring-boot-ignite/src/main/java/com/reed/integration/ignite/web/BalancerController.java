package com.reed.integration.ignite.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.ignite.resources.LoadBalancerResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author reed
 *
 */
@RestController
public class BalancerController {

	@LoadBalancerResource
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntity<String>> login(HttpServletRequest request) {
		String sessionId = request.getSession(true).getId();
		System.out.println("Current port is:" + request.getLocalPort() + "/n");
		ResponseEntity<String> data = new ResponseEntity<>(sessionId, HttpStatus.OK);
		ResponseEntity<ResponseEntity<String>> r = new ResponseEntity<>(data, HttpStatus.OK);
		return r;
	}

	@LoadBalancerResource
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntity<String>> logout(HttpServletRequest request) {
		request.getSession().invalidate();
		String sessionId = request.getSession().getId();
		ResponseEntity<String> data = new ResponseEntity<>(sessionId, HttpStatus.OK);
		ResponseEntity<ResponseEntity<String>> r = new ResponseEntity<>(data, HttpStatus.OK);
		return r;
	}

}