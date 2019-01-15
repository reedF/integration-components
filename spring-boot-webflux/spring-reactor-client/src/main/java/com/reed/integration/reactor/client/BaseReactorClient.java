package com.reed.integration.reactor.client;

import org.springframework.stereotype.Service;

import com.reed.integration.reactor.client.model.ReactorMsg;

import lombok.extern.slf4j.Slf4j;

/**
 * just for business test
 * @author reed
 *
 */
@Slf4j
@Service
public class BaseReactorClient extends AbstractReactorClient implements ReactorClient {

	@Override
	public <T> void consumeEventForBiz(ReactorMsg<T> event) {
		log.info("======do business:{}======", event);
	}
}
