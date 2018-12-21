package com.reed.integration.reactor.disruptor;

import com.lmax.disruptor.EventFactory;
import com.reed.integration.reactor.client.model.ReactorMsg;

/**
 * 事件生产工厂
 *
 */
public class DisruptorEventFactory<T> implements EventFactory<ReactorMsg<T>> {

	@Override
	public ReactorMsg<T> newInstance() {
		return new ReactorMsg<T>();
	}

}
