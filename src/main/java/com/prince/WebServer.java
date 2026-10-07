package com.prince;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebServer {
    private static final String PAGES_DIR = "/public";
    private final Javalin appServer;

    public WebServer() {
        appServer = Javalin.create(config -> {
            config.staticFiles.add(PAGES_DIR, Location.CLASSPATH);

            config.routes.get("/items",ctx -> ctx.result("GET: read items"));
            config.routes.post("/items",ctx -> ctx.result("POST: create an item"));
            config.routes.get("/greet",  ctx -> ctx.result("GET: hello " + ctx.queryParam("name")));
            config.routes.post("/greet", ctx -> ctx.result("POST: hello " + ctx.formParam("name")));
        });
    }

    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start(5000);
    }

    public void start(int port) {
        this.appServer.start(port);
    }

}

