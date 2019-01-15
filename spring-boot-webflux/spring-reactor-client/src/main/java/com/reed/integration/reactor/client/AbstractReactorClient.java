package com.reed.integration.reactor.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.reed.integration.reactor.client.model.ReactorMsg;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * default implment for ReactorClient,business service just need to extends this and implement consumeEventForBiz
 * @author reed
 *
 */
@Slf4j
public abstract class AbstractReactorClient implements ReactorClient {

	public static final String LOAD_EVENT_URL = "/events/load";
	public static final String GET_EVENT_URL = "/events/get";

	@Value("${reactor.client.enabled}")
	public boolean enabled;

	@Autowired
	private WebClient.Builder webClientBuilder;

	/**
	 * 向服务端发送事件
	 * @param events
	 * @return
	 */
	public <T> List<Throwable> loadEvent2Server(List<ReactorMsg<T>> events) {
		List<Throwable> errors = new ArrayList<>();
		ParameterizedTypeReference<ReactorMsg<T>> type = new ParameterizedTypeReference<ReactorMsg<T>>() {
		};
		webClientBuilder.build().post().uri(LOAD_EVENT_URL).contentType(MediaType.APPLICATION_STREAM_JSON)
				.body(Flux.fromIterable(events), type)
				//
				.retrieve()
				// handle error
				.onStatus(HttpStatus::isError,
						clientResponse -> Mono.error(new Throwable("Send event error:" + clientResponse.statusCode())))
				.bodyToMono(Void.class)
				//
				.doOnSuccessOrError((msg, ex) -> {
					if (ex != null) {
						log.error("======send event error:{}=======", ex);
						errors.add(ex);
					} else {
						log.info("======send event success:{}=======", events);
					}
				})
				//
				.block();
		return errors;
	}

	/**
	 * client启动即监听event，从服务端接收事件
	 */
	@PostConstruct
	public <T> void getEventFromServer() {
		if (enabled) {
			ParameterizedTypeReference<ReactorMsg<T>> type = new ParameterizedTypeReference<ReactorMsg<T>>() {
			};
			webClientBuilder.build().get().uri(GET_EVENT_URL).accept(MediaType.APPLICATION_STREAM_JSON).retrieve()
					.bodyToFlux(type).doOnError(e -> log.error("========get event from server error:========", e))
					.subscribe(event -> consumeEventForBiz(event));
		}
	}

	@Override
	public <T> boolean sendEvent(List<ReactorMsg<T>> events) {
		List<Throwable> errors = null;
		if (events != null && !events.isEmpty()) {
			errors = loadEvent2Server(events);
		}
		return errors == null || errors.isEmpty();
	}

}
