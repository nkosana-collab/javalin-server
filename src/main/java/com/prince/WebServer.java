package com.prince;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class WebServer {
    private static final String PAGES_DIR = "/public";
    private final Javalin appServer;

    public WebServer() {
        appServer = Javalin.create(config -> {
            // static files (CSS, JS, images later) are served straight from /public
            config.staticFiles.add(PAGES_DIR, Location.CLASSPATH);

            // ROUTE: when the browser asks GET /home, send back index.html
            config.routes.get("/home", ctx -> {
                try (InputStream page = WebServer.class.getResourceAsStream(PAGES_DIR + "/index.html")) {
                    if (page == null) {
                        ctx.status(404).result("Page not found");
                        return;
                    }
                    ctx.html(new String(page.readAllBytes(), StandardCharsets.UTF_8));
                }
            });
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