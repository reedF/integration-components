package com.reed.integration.ignite.listener;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.scheduler.SchedulerFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.reed.integration.ignite.cache.IgniteCacheConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * ignite msg
 * 注：
 * 1.使用schedule定时发送消息，需依赖ignite-schedule
 * 2.ignite-schedule集成的是cron4j，只精确到分，不支持秒，cron表达式与linux crontab及quartz的表达式格式不一致，
 *   cron4j最多只允许五部分，每部分用空格分隔开来，这五部分从左到右依次表示分、时、天、月、周
 * 3.ignite-schedule对cron有个扩展，可配置任务启动延迟与总共执行的次数：{2, 5}:启动延迟2秒，共执行5次
 * @author reed
 *
 */

@Slf4j
@Component
public class MsgListener {

	public static final String topic = "ignite-msg-topic";
	// https://liyuj.gitee.io/doc/java/ComputeGrid.html#_7-12-3-语法扩展
	// {2,5}指：延迟2秒，共运行5次
	// 由于ignite-schedule集成的是cron4j，只精确到分，cron表达式最多只允许五部分，每部分用空格分隔开来，这五部分从左到右依次表示分、时、天、月、周
	// "{2, *} * * * * *"表示，延迟启动2秒，不限执行次数，每分钟执行
	public static final String cron = "{10, *} */2 * * * *";

	@Autowired
	@Qualifier(IgniteCacheConfig.DEFAULT_IGNITE_INSTANCE)
	private Ignite ignite;

	@PostConstruct
	public void init() {
		ignite.message().localListen(topic, getListener(true));
		ignite.message(ignite.cluster().forRemotes()).remoteListen(topic, getListener(false));
		// send msg in schedule task,refer to
		// https://liyuj.gitee.io/doc/java/ComputeGrid.html#_7-12-3-语法扩展
		// {2,5}指：延迟2秒，共运行5次
		SchedulerFuture<?> fut = ignite.scheduler().scheduleLocal(new Runnable() {
			@Override
			public void run() {
				sendMsg(true);
			}
		}, cron);

		log.info("===========The task will be next executed on{}==============",
				DateFormat.getDateTimeInstance().format(new Date(fut.nextExecutionTime())));
	}

	@PreDestroy
	public void stop() {
		ignite.message().stopLocalListen(topic, getListener(true));
	}

	public IgniteBiPredicate<UUID, String> getListener(boolean isLocal) {
		IgniteBiPredicate<UUID, String> listener = null;
		if (isLocal) {
			listener = new IgniteBiPredicate<UUID, String>() {
				@Override
				public boolean apply(UUID nodeId, String msg) {
					log.info("Local msg listen:nodeID:{},msg is {}", nodeId, msg);
					return true;
				}
			};
		} else {
			listener = (nodeId, msg) -> {
				log.info("Remote msg listen:nodeID:{},msg is {}", nodeId, msg);
				return true;
			};
		}
		return listener;
	}

	/**
	 * 发送消息
	 * @param isOrder 是否有序
	 */
	public void sendMsg(boolean isOrder) {
		IgniteMessaging rmtMsg = ignite.message();
		String msg = "msg on " + DateFormat.getDateTimeInstance().format(new Date());
		if (isOrder) {
			rmtMsg.sendOrdered(topic, msg, 0);
		} else {
			rmtMsg.send(topic, msg);
		}
		log.info("=======sending msg:{}========", msg);
	}
}
