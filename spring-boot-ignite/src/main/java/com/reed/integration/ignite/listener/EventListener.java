package com.reed.integration.ignite.listener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.ignite.Ignite;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.reed.integration.ignite.cache.IgniteStoreCacheConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * ignite events listen
 * @author reed
 *
 */
@Slf4j
@Component
public class EventListener {
	@Autowired
	@Qualifier(IgniteStoreCacheConfig.STORE_NODE_NAME)
	private Ignite ignite;

	public static int batchSize = 2;

	@PostConstruct
	public void init() {
		// event typs
		int[] types = getEventTypes();
		IgnitePredicate<CacheEvent> locLsnr = localListen();
		IgnitePredicate<CacheEvent> remotesnr = remoteListen();
		// Subscribe to specified cache events occuring on local node.
		ignite.events().localListen(locLsnr, types);
		// Subscribe to specified cache events on all nodes that have cache
		// running.
		ignite.events().remoteListen(null, remotesnr, types);
		// batch listen
		ignite.events(ignite.cluster().forCacheNodes(IgniteStoreCacheConfig.IGNITE_STORE_CACHE_NAME))
				.remoteListen(batchSize, 0, false, null, batchFilterListen(), getEventTypes());
	}

	@PreDestroy
	public void stop() {
		ignite.events().stopLocalListen(localListen(), getEventTypes());
	}

	public int[] getEventTypes() {
		int[] types = { EventType.EVT_CACHE_OBJECT_PUT, EventType.EVT_CACHE_OBJECT_READ,
				EventType.EVT_CACHE_OBJECT_REMOVED };
		return types;
	}

	public IgnitePredicate<CacheEvent> remoteListen() {
		// remote listener that listenes to re events.
		IgnitePredicate<CacheEvent> locLsnr = evt -> {
			log(evt, "Remote===" + evt.cacheName());
			// Continue listening.
			return true;
		};
		return locLsnr;
	}

	public IgnitePredicate<CacheEvent> batchFilterListen() {
		// remote listener that listenes to re events.
		IgnitePredicate<CacheEvent> locLsnr = evt -> {
			boolean r = evt.cacheName().equals(IgniteStoreCacheConfig.IGNITE_STORE_CACHE_NAME)
					&& evt.<Long>key() % batchSize == 0;
			if (r) {
				log(evt, "Batch===" + evt.cacheName());
			}
			// Continue listening.
			return r;
		};
		return locLsnr;
	}

	public IgnitePredicate<CacheEvent> localListen() {
		// Local listener that listenes to local events.
		IgnitePredicate<CacheEvent> locLsnr = evt -> {
			log(evt, "Local===" + evt.cacheName());
			// Continue listening.
			return true;
		};
		return locLsnr;
	}

	public void log(CacheEvent evt, String tag) {
		log.info("========{} listen:Received event [evt={},key={},oldVal={},newVal={}]========", tag, evt.name(),
				evt.key(), evt.oldValue(), evt.newValue());
	}
}
