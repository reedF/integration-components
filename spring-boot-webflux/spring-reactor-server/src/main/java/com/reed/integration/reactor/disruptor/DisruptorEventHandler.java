package com.reed.integration.reactor.disruptor;

import java.time.LocalDateTime;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.reed.integration.reactor.client.model.ReactorMsg;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.UnicastProcessor;

@Slf4j
public class DisruptorEventHandler<T> implements EventHandler<ReactorMsg<T>>, WorkHandler<ReactorMsg<T>> {

	// public Flux<ReactorMsg<T>> flux = Flux.<ReactorMsg<T>>empty();
	// Hot流，持续不断地产生消息，订阅者只能获取到在其订阅之后产生的消息
	//EmitterProcessor可向多个订阅者发送数据，UnicastProcessor则最多只能有一个订阅者
	public EmitterProcessor<ReactorMsg<T>> hotSource = EmitterProcessor.create();

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
		// this.flux = Flux.<ReactorMsg<T>>push(fluxSink -> {
		// fluxSink.next(event);
		// }, FluxSink.OverflowStrategy.BUFFER);
		// flux = Flux.<ReactorMsg<T>>from(Flux.just(event));
		this.hotSource.onNext(event);
	}

	private void printLog(ReactorMsg<T> event) {
		log.info("======Disruptor consumer get msg:{}, get time is:{}=========", event, LocalDateTime.now());
	}
}
