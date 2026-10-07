package com.prince;

import io.javalin.Javalin;

public class WebServer {


    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        }).start(7070);
    }
}
