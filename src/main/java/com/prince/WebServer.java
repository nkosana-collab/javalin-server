package com.prince;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebServer {
    private static final String PAGES_DIR = "/public";
    private final Javalin appServer;

    public WebServer() {
        appServer = Javalin.create(config -> {
            config.addStaticFiles(PAGES_DIR, Location.CLASSPATH);

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
