package com.reed.integration.reactor.client.model;

import java.util.Date;

import org.springframework.http.MediaType;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据传输对象，使用泛型T封装业务数据
 * @author reed
 *
 * @param <T>
 */
@Getter
@Setter
public class ReactorMsg<T> extends BaseReactorObj {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2642974888814565828L;
	// 源应用
	public String sourceApp;
	// 目标应用
	public String sinkApp;
	// 编码格式
	public MediaType contentType;
	// 业务数据
	public T data;
	// 消息创建时间戳
	public Date timestamp = new Date();

}
