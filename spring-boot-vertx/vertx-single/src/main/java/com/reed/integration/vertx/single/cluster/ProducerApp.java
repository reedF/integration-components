package com.reed.integration.vertx.single.cluster;

import java.util.Arrays;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.StringUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

/**
 * 
 */
public class ProducerApp extends AbstractVerticle {

	public static final String EVENT_ADDRESS = "news";

	public static void main(String[] args) {
		// Force to use slf4j
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		// default config:classpath/vertx-ignite.jar:default-ignite.xml
		// ClusterManager clusterManager = new IgniteClusterManager();
		// change config
		ClusterManager clusterManager = new IgniteClusterManager(IgniteConfig.igniteConfiguration());
		Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(clusterManager), ar -> {
			if (ar.failed()) {
				System.err.println("Cannot create vert.x instance : " + ar.cause());
			} else {
				Vertx vertx = ar.result();
				vertx.deployVerticle(ProducerApp.class.getName());
			}
		});
	}

	@Override
	public void start() throws Exception {
		vertx.setPeriodic(3000, x -> {
			System.out.println("Sending data to " + EVENT_ADDRESS);
			vertx.eventBus().send(EVENT_ADDRESS, "hello vert.x");
		});
	}

}