package com.reed.integration.reactor.client;

import java.util.List;

import com.reed.integration.reactor.client.model.ReactorMsg;

/**
 * reactor client客户端SDK接口，具体使用方式参见：BaseReactorClient
 * @author reed
 *
 */
public interface ReactorClient {

	/**
	 * 发送事件
	 * @param events
	 * @return
	 */
	public <T> boolean sendEvent(List<ReactorMsg<T>> events);

	/**
	 * 接收事件后的业务回调方法
	 * @param event
	 */
	public <T> void consumeEventForBiz(ReactorMsg<T> event);
}
