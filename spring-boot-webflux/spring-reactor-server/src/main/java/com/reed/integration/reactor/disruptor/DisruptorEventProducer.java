package com.reed.integration.reactor.disruptor;

import org.springframework.beans.BeanUtils;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.reed.integration.reactor.client.model.ReactorMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DisruptorEventProducer<T> {
	private final RingBuffer<ReactorMsg<T>> ringBuffer;

	public DisruptorEventProducer(RingBuffer<ReactorMsg<T>> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	/**
	 * translateTo方法将ringbuffer中的消息，转换成java对象格式,提供给后续消费者EventHandler处理器，直接操作event对象
	 */
	private EventTranslator<ReactorMsg<T>> TRANSLATOR = new EventTranslator<ReactorMsg<T>>() {
		public void translateTo(ReactorMsg<T> event, long sequence) {
			log.info("===={}====", event);
		}
	};

	private EventTranslatorOneArg<ReactorMsg<T>, ReactorMsg<T>> TRANSLATOR_WITHONEARG = new EventTranslatorOneArg<ReactorMsg<T>, ReactorMsg<T>>() {
		/**
		 * event 由EventFactory(即DisruptorEventFactory)创建的对象
		 * source 生产者产生的对象
		 */
		public void translateTo(ReactorMsg<T> event, long sequence, ReactorMsg<T> source) {
			BeanUtils.copyProperties(source, event);
		}

	};

	/**
	 * 生产者发送消息
	 * @param event 要发送的数据
	 */
	public void product(ReactorMsg<T> event) {
		// product4OldApi();
		ringBuffer.publishEvent(TRANSLATOR_WITHONEARG, event);
	}

	/**
	 * Disruptor version 3.0之前的调用方式
	 */
	private void product4OldApi(ReactorMsg<T> source) {
		// 请求下一个事件序号
		long sequence = ringBuffer.next();
		try {
			// 获取该序号对应的事件对象，由EventFactory创建的
			ReactorMsg<T> event = ringBuffer.get(sequence);
			// 获取要通过事件传递的业务数据
			BeanUtils.copyProperties(source, event);
		} finally {
			// 发布事件
			// 注:最后的 ringBuffer.publish 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的
			// sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer
			ringBuffer.publish(sequence);
		}
	}
}