package com.reed.integration.vertx.single.cluster;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

/**
 * 
 */
public class ConsumerApp extends AbstractVerticle {

	public static void main(String[] args) {
		ClusterManager clusterManager = new IgniteClusterManager();
		Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(clusterManager), ar -> {
			if (ar.failed()) {
				System.err.println("Cannot create vert.x instance : " + ar.cause());
			} else {
				Vertx vertx = ar.result();
				vertx.deployVerticle(ConsumerApp.class.getName());
			}
		});
	}

	@Override
	public void start() throws Exception {
		vertx.eventBus().consumer(ProducerApp.EVENT_ADDRESS, message -> {
			System.out.println(">> " + message.body());
		});
	}
}