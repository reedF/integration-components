package test.integration.reactor;

import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import com.reed.integration.reactor.ReactorApplication;
import com.reed.integration.reactor.client.ReactorClient;
import com.reed.integration.reactor.client.model.ReactorMsg;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RunWith(SpringRunner.class)
// @WebFluxTest(controllers = { EventController.class })
@SpringBootTest(classes = { ReactorApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestReactorApp {

	@LocalServerPort
	private int port;

	public String url;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private WebTestClient testClient;

	@Autowired
	@Qualifier("baseReactorClient")
	private ReactorClient testReactorClient;

	@Before
	public void prepareClient() {
		url = "http://localhost:" + port;
		// testClient = WebTestClient.bindToApplicationContext(context).build();
		// return testClient;
	}

	@Test
	public void testSse() throws InterruptedException {
		WebClient webClient = WebClient.create(url);
		Flux<String> resp = webClient.get().uri("/events/sse").accept(MediaType.TEXT_EVENT_STREAM).retrieve()
				.bodyToFlux(String.class)
				// .log()
				.take(5);
		resp.subscribe(System.out::println);
		resp.blockLast();
		// TimeUnit.SECONDS.sleep(1);
	}

	/**
	 * 测试函数模式定义的reactor
	 * @throws InterruptedException
	 */
	@Test
	public void testFunctionSse() throws InterruptedException {
		WebClient webClient = WebClient.create(url);
		Flux<String> resp = webClient.get().uri("/sse").accept(MediaType.TEXT_EVENT_STREAM).retrieve()
				.bodyToFlux(String.class)
				// .log()
				.take(5);
		resp.parallel().runOn(Schedulers.parallel())
				.subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
		resp.blockLast();
		// TimeUnit.SECONDS.sleep(1);
	}

	@Test
	public void testTimes() throws Exception {
		String r = this.testClient.get().uri("/events/times").exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON_UTF8).expectBody(String.class).returnResult().getResponseBody();
		Assert.assertTrue(r.startsWith("Now"));
	}

	@Test
	public void testLoadEvent() throws InterruptedException {
		ParameterizedTypeReference<ReactorMsg<String>> type = new ParameterizedTypeReference<ReactorMsg<String>>() {
		};
		List<ReactorMsg<String>> list = new ArrayList<>();
		list.add(makeMsg(1l));
		// 1s一个event,take表示发送5次
		Flux<ReactorMsg<String>> eventFlux = Flux.interval(Duration.ofSeconds(1)).map(l -> makeMsg(l)).take(5);
		WebClient webClient = WebClient.create(url);
		webClient.post().uri("/events/load").contentType(MediaType.APPLICATION_STREAM_JSON)
				//
				// .body(BodyInserters.fromPublisher(eventFlux,
				// GenericsUtils.getSuperClassGenricType(makeMsg(1l).getClass())))
				// .body(BodyInserters.fromPublisher(Flux.just("t1",
				// "t2"),String.class))
				// .body(BodyInserters.fromObject(list))
				.body(eventFlux, type)
				//
				.retrieve().bodyToMono(Void.class).log()
				//
				.block();
		// TimeUnit.SECONDS.sleep(1);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testGetEvents() throws InterruptedException {
		testLoadEvent();
		WebClient webClient = WebClient.create(url);
		Flux<ReactorMsg> resp = webClient.get().uri("/events/get").accept(MediaType.APPLICATION_STREAM_JSON).retrieve()
				.bodyToFlux(ReactorMsg.class).take(Duration.ofSeconds(5l)).log();
		resp.subscribe(System.out::println);
		resp.blockLast();

	}

	@Test
	public void testFunctionLoadEvent() throws InterruptedException {
		ParameterizedTypeReference<ReactorMsg<String>> type = new ParameterizedTypeReference<ReactorMsg<String>>() {
		};
		List<ReactorMsg<String>> list = new ArrayList<>();
		list.add(makeMsg(1l));
		// 1s一个event,take表示发送5次
		Flux<ReactorMsg<String>> eventFlux = Flux.interval(Duration.ofSeconds(1)).map(l -> makeMsg(l)).take(5);
		WebClient webClient = WebClient.create(url);
		webClient.post().uri("/loadevents").contentType(MediaType.APPLICATION_STREAM_JSON).body(eventFlux, type)
				//
				.retrieve().bodyToMono(Void.class).log()
				//
				.block();
		// TimeUnit.SECONDS.sleep(1);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testFunctionGetEvents() throws InterruptedException {
		testFunctionLoadEvent();
		WebClient webClient = WebClient.create(url);
		Flux<ReactorMsg> resp = webClient.get().uri("/getevents").accept(MediaType.APPLICATION_STREAM_JSON).retrieve()
				.bodyToFlux(ReactorMsg.class).take(Duration.ofSeconds(5l))
		// .log()
		;
		resp.subscribe(System.out::println);
		resp.blockLast();

	}

	/**
	 * need to run server first
	 * @throws InterruptedException
	 */
	@Ignore
	@Test
	public void testReactorClient() throws InterruptedException {
		List<ReactorMsg<String>> list = new ArrayList<>();
		list.add(makeMsg(1l));
		list.add(makeMsg(2l));
		boolean r = testReactorClient.sendEvent(list);
		Assert.assertEquals(true, r);
		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void testConcurrent() {
		try {
			int num = 1;
			CountDownLatch cdl = new CountDownLatch(num);
			ExecutorService es = Executors.newFixedThreadPool(num * 2);
			for (int i = 0; i < num; i++) {
				es.execute(new CountDownLatchTask(cdl, 1));
				es.execute(new CountDownLatchTask(cdl, 2));
			}

			System.out.println("主线程:" + Thread.currentThread().getName() + "countdown自减开始。。。");
			for (int j = 0; j < num; j++) {
				cdl.countDown();
			}
			System.out.println("主线程:" + Thread.currentThread().getName() + "countdown自减完成。。。");
			TimeUnit.SECONDS.sleep(10);
			es.shutdown();
			System.out.println("============主线程退出=================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同时并发执行
	 * @author reed
	 *
	 */
	public class CountDownLatchTask implements Runnable {
		CountDownLatch latch;
		int tag;

		public CountDownLatchTask(CountDownLatch latch, int tag) {
			super();
			this.latch = latch;
			this.tag = tag;
		}

		@Override
		public void run() {
			String s = "job-" + tag;
			Thread.currentThread().setName(s);
			try {
				latch.await();

			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.out.println("当前线程:" + s + "等待结束, 开始模拟请求， 开始时间:" + System.currentTimeMillis());
			}
			sendOrlistenEvents(tag, url);
			System.out.println("当前线程:" + s + " Done");
		}
	}

	@SuppressWarnings("rawtypes")
	private static void sendOrlistenEvents(int tag, String url) {
		WebClient webClient = WebClient.create(url);
		if (tag == 1) {
			ParameterizedTypeReference<ReactorMsg<String>> type = new ParameterizedTypeReference<ReactorMsg<String>>() {
			};
			List<ReactorMsg<String>> list = new ArrayList<>();
			list.add(makeMsg(1l));
			// 无限发送
			Flux<ReactorMsg<String>> eventFlux = Flux.interval(Duration.ofSeconds(1)).map(l -> makeMsg(l));
			webClient.post().uri("/events/load").contentType(MediaType.APPLICATION_STREAM_JSON).body(eventFlux, type)
					//
					.retrieve().bodyToMono(Void.class)
					//
					.block()
			//
			;
		}
		if (tag == 2) {
			try {
				TimeUnit.SECONDS.sleep(3);
				// ConnectableFlux<ReactorMsg> resp =
				Flux<ReactorMsg> resp =
						// webClient一旦创建不可变，因此要mutate复制一个新的client
						webClient.mutate().build().get().uri("/events/get").accept(MediaType.APPLICATION_STREAM_JSON)
								//
								.retrieve().bodyToFlux(ReactorMsg.class)
				//
				// .log()
				//
				// .publish();
				// resp
				//
				// .doOnNext(t -> log.info("====onNext:{}====", t))
				//
				// .doOnComplete(() ->
				// log.info("=====DONE====")).doOnError(e ->
				// log.error("====ex:{}=====", e))
				// .doOnTerminate(() ->
				// log.info("=====Terminate===="))
				//
				// .doFinally(type -> fluxFinally(type))
				//
				// .publishOn(Schedulers.newSingle("pub-thread"))
				//
				// .subscribe(System.out::println)
				//
				// .blockLast()
				//
				;
				// resp.connect();
				Disposable disposable = resp.subscribe(t -> log.info("=====sub:{}====", t));
				// resp.blockLast();
				TimeUnit.SECONDS.sleep(8);
				// 取消订阅
				disposable.dispose();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void fluxFinally(SignalType type) {
		if (type == SignalType.ON_COMPLETE || type == SignalType.ON_ERROR) {

		}
		log.info("=====Finally====");
	}

	public static ReactorMsg<String> makeMsg(Long l) {
		ReactorMsg<String> msg = new ReactorMsg<>();
		msg.setData("test-" + l);
		msg.setTimestamp(new Date());
		return msg;
	}

	public static void main(String[] args) {
		String url = "http://localhost:8080";
		new Thread(() -> sendOrlistenEvents(1, url)).start();
		new Thread(() -> sendOrlistenEvents(2, url)).start();
	}
}
