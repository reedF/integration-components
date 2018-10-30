package com.reed.integration.vertx.single.cluster;

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
		ClusterManager clusterManager = new IgniteClusterManager();
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
			System.out.println("Sending data to 'news'");
			vertx.eventBus().send(EVENT_ADDRESS, "hello vert.x");
		});
	}

}