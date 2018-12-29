package com.reed.integration.reactor.controller;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reed.integration.reactor.client.model.ReactorMsg;
import com.reed.integration.reactor.disruptor.DisruptorEventHandler;
import com.reed.integration.reactor.disruptor.DisruptorEventProducer;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 使用springMVC controller模式定义reactor
 * @author reed
 *
 * @param <T>
 */
@Slf4j
@RestController
@RequestMapping("/events")
// CORS
@CrossOrigin(value = { "http://allowed-origin.com" }, allowedHeaders = { "Baeldung-Allowed" }, maxAge = 900)
public class EventController<T> {
	@Autowired
	private DisruptorEventProducer<T> producer;

	@Autowired
	private DisruptorEventHandler<T> consumer;

	/**
	 * 接收数据
	 * @param events
	 * @return
	 */
	@PostMapping(path = "/load", consumes = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Mono<Void> loadEvents(@RequestBody Flux<ReactorMsg<T>> events) {
		// 接收Flux数据
		// 务必使用传入的Flux或Mono的then方法定义返回
		return events.doOnNext(t -> producer.product(t)).then();

		// 接收List数据
		// if (events != null) {
		// events.forEach(t -> producer.product(t));
		// }
		// return Mono.<Void>empty().then();
	}

	/**
	 * 查询数据
	 * @return
	 */
	@GetMapping(path = "/get", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<ReactorMsg<T>> getEvents() {
		// test
		// return Flux.interval(Duration.ofSeconds(1L)).map((oneSecond) -> new
		// ReactorMsg<>());

		// return Flux.<ReactorMsg<T>>from(consumer.flux);
		// 构造hot流
		Flux<ReactorMsg<T>> flux = Flux.<ReactorMsg<T>>from(consumer.hotSource.publish().autoConnect());
		// flux.subscribe(System.out::println);
		//无数据返回空
		return flux.share().switchIfEmpty(Flux.empty());
	}

	@GetMapping(path = "/times", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Mono<String> times() {
		return Mono.just("Now is " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

	/**
	 * 服务端推送SSE
	 * 注：SSE事件流的对应MIME格式必须为text/event-stream
	 * @return
	 */
	@GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> sse() {
		return Flux.interval(Duration.ofSeconds(1))
				.map(l -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

	private void logError(String owner, Throwable e) {
		if (e != null) {
			log.error("{} error:{}", owner, e.getMessage());
		}
	}
}
