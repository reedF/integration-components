package com.reed.integration.reactor.disruptor;

import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.reed.integration.reactor.client.model.ReactorMsg;
import com.reed.integration.reactor.common.NamedThreadFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DisruptorStarter<T> {

	// private ExecutorService executorService =
	// Executors.newFixedThreadPool(2);

	private ThreadFactory threadFactory = new NamedThreadFactory("DisruptorStarter");

	private Disruptor<ReactorMsg<T>> disruptor;

	private DisruptorEventHandler<T> handler = new DisruptorEventHandler<>();

	@PostConstruct
	public void init() {
		// 指定 ring buffer字节大小, must be power of 2.
		int bufferSize = 1024;
		disruptor = new Disruptor<ReactorMsg<T>>(new DisruptorEventFactory<>(), bufferSize, threadFactory,
				ProducerType.SINGLE, new BlockingWaitStrategy());
		disruptor.setDefaultExceptionHandler(new IgnoreExceptionHandler());
		disruptor.handleEventsWith(handler);
		disruptor.start();
		log.info("========Disruptor started!=======");
	}

	@PreDestroy
	public void destroy() {
		disruptor.shutdown();
		log.info("========Disruptor stop!=======");
	}

	@Bean
	public DisruptorEventProducer<T> disruptorEventProducer() {
		return new DisruptorEventProducer<>(disruptor.getRingBuffer());
	}

	@Bean
	public DisruptorEventHandler<T> disruptorEventHandler() {
		return handler;
	}
}
