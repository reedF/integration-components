package com.reed.integration.vertx.single;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * mvn compile vertx:run
 * @author reed
 *
 */
public class VerticleServerDemo extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) {
		vertx.createHttpServer().requestHandler(r -> {
			System.out.println("======uri is : " + r.path() + "====Params is:[" + r.params() + "]");
			r.response().putHeader("Content-type", "application/json");
			r.bodyHandler(buff -> {
				if (r.getHeader("content-type") != null && r.getHeader("content-type").toLowerCase().contains("json")) {
					String bf = buff.toString();
					System.out.println("Receiving body is:[ " + bf + "]");
					r.response().end(bf);
				} else {
					r.response()
							.end(new JsonObject().put("res", "How world!=====Query is:[" + r.params() + "]").encode());
				}
			});

		}).listen(8080, result -> {
			if (result.succeeded()) {
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
		System.out.println("=========server started!============");
	}

	public static void main(final String... args) {
		Vertx vertx = Vertx.vertx();
		try {
			vertx.deployVerticle(VerticleServerDemo.class.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}