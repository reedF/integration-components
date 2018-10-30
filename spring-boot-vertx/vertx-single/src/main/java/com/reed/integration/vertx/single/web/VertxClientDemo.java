package com.reed.integration.vertx.single.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

/**
 * 
 */
public class VertxClientDemo extends AbstractVerticle {

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		try {
			vertx.deployVerticle(VertxClientDemo.class.newInstance(), res -> {
				if (res.succeeded()) {
					System.out.println("Deployment id is: " + res.result());
				} else {
					System.out.println("Deployment failed!");
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public static class User {
		public String firstName;
		public String lastName;
		public boolean male;
	}

	@Override
	public void start() throws Exception {

		WebClient client = WebClient.create(vertx);
		sendJson(client);
		sendJsonByObj(client);
		sendForm(client);
		sendQuery(client);
	}

	public User getUser() {
		User user = new User();
		user.firstName = "Dale";
		user.lastName = "Cooper";
		user.male = true;
		return user;
	}

	public void sendJson(WebClient client) {
		client.get(8080, "localhost", "/json").sendJson(getUser(), ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				System.out.println("Got HTTP response body:" + response.bodyAsString());
				System.out.println("get json:" + response.body().toJsonObject().encodePrettily());

			} else {
				ar.cause().printStackTrace();
			}
		});
	}

	public void sendJsonByObj(WebClient client) {
		Buffer bf = Buffer.buffer(Json.encode(getUser()));
		client.get(8080, "localhost", "/jsonobj").putHeader("Content-type", "application/json")
				.as(BodyCodec.json(User.class)).sendBuffer(bf, ar -> {
					if (ar.succeeded()) {
						HttpResponse<User> response = ar.result();
						System.out.println("get json By obj:" + response.body().firstName);

					} else {
						ar.cause().printStackTrace();
					}
				});
	}

	public void sendForm(WebClient client) {
		MultiMap form = MultiMap.caseInsensitiveMultiMap();
		form.add("firstName", "Dale");
		form.add("lastName", "Cooper");
		form.add("male", "true");

		client.post(8080, "localhost", "/form").sendForm(form, ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				System.out.println("Got HTTP response with status:" + response.statusCode());
				System.out.println("Got HTTP response body: " + response.bodyAsString());
			} else {
				ar.cause().printStackTrace();
			}
		});
	}

	public void sendQuery(WebClient client) {
		MultiMap form = MultiMap.caseInsensitiveMultiMap();
		form.add("firstName", "Dale");
		form.add("lastName", "Cooper");
		form.add("male", "true");

		HttpRequest<Buffer> request = client.post(8080, "localhost", "/query");
		request.queryParams().addAll(form);
		request.send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> response = ar.result();
				System.out.println("Got HTTP response with status:" + response.statusCode());
				System.out.println("Got HTTP response body: " + response.bodyAsString());
			} else {
				ar.cause().printStackTrace();
			}
		});
	}
}