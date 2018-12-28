package com.reed.integration.reactor.functions;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reed.integration.reactor.client.model.ReactorMsg;
import com.reed.integration.reactor.disruptor.DisruptorEventHandler;
import com.reed.integration.reactor.disruptor.DisruptorEventProducer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * HandlerFunction模式定义reactor,相当于Controller中的具体处理方法
 * 输入为请求，输出为装在Mono中的响应
 * @author reed
 *
 * @param <T>
 */
@Component
public class EventHandler<T> {

	@Autowired
	private DisruptorEventProducer<T> producer;

	@Autowired
	private DisruptorEventHandler<T> consumer;

	/**
	 * SSE
	 * @param serverRequest
	 * @return
	 */
	public Mono<ServerResponse> getSse(ServerRequest serverRequest) {
		return ok().contentType(MediaType.TEXT_EVENT_STREAM).body(Flux.interval(Duration.ofSeconds(1))
				.map(l -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())), String.class);
	}

	/**
	 * 接收事件
	 * @param serverRequest
	 * @return
	 */
	public Mono<ServerResponse> loadEvents(ServerRequest serverRequest) {
		ParameterizedTypeReference<ReactorMsg<T>> type = new ParameterizedTypeReference<ReactorMsg<T>>() {
		};
		return ServerResponse.ok()
				.build(serverRequest.bodyToFlux(type).doOnNext(event -> producer.product(event)).then());
	}

	/**
	 * 查询事件
	 * @param serverRequest
	 * @return
	 */
	public Mono<ServerResponse> getEvents(ServerRequest serverRequest) {
		ParameterizedTypeReference<ReactorMsg<T>> type = new ParameterizedTypeReference<ReactorMsg<T>>() {
		};
		return ok().contentType(MediaType.APPLICATION_STREAM_JSON)
				.body(Flux.from(consumer.hotSource.publish().autoConnect()).share(), type)
				//无数据返回404 Not Found
				.switchIfEmpty(ServerResponse.notFound().build());
	}
}
