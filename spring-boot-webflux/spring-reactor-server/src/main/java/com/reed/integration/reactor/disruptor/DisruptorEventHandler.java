package com.reed.integration.reactor.disruptor;

import java.time.LocalDateTime;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.reed.integration.reactor.client.model.ReactorMsg;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Slf4j
public class DisruptorEventHandler<T> implements EventHandler<ReactorMsg<T>>, WorkHandler<ReactorMsg<T>> {

	public Flux<ReactorMsg<T>> flux = Flux.<ReactorMsg<T>>empty();

	@Override
	public void onEvent(ReactorMsg<T> event, long sequence, boolean endOfBatch) throws Exception {
		consumeEvent(event);
	}

	@Override
	public void onEvent(ReactorMsg<T> event) throws Exception {
		consumeEvent(event);
	}

	private void consumeEvent(ReactorMsg<T> event) {
		printLog(event);
		// do business
		flux = Flux.<ReactorMsg<T>>create(fluxSink -> {
			fluxSink.next(event);
		}, FluxSink.OverflowStrategy.BUFFER);
	}

	private void printLog(ReactorMsg<T> event) {
		log.info("======Disruptor consumer get msg:{}, get time is:{}=========", event, LocalDateTime.now());
	}
}
