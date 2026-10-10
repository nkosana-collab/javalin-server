package com.prince;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WebServer {
    private static final String PAGES_DIR = "/public";
    private static final String SECOND_DIR = "/public/html";
    private final Javalin appServer;


    private final AtomicInteger nextId = new AtomicInteger(3);   // ids 1 and 2 are already used
    private final List<Item> items = new CopyOnWriteArrayList<>(List.of(
            new Item(1, "Milk"),
            new Item(2, "Bread")));

    public WebServer() {
        appServer = Javalin.create(config -> {
            config.staticFiles.add(PAGES_DIR, Location.CLASSPATH);

            config.routes.get("/items",ctx -> ctx.json(items));
//            config.routes.post("/items",ctx -> ctx.result("POST: create an item"));



            config.routes.get("/items/{id}", ctx -> {

                try {
                    int id = Integer.parseInt(ctx.pathParam("id"));            // read the id from the path
                    Item found = items.stream()
                            .filter(item -> item.id() == id)
                            .findFirst()
                            .orElse(null);
                    if (found == null) {
                        ctx.status(404).json(Map.of("error", "Item " + id + " not found"));
                        return;
                    }
                    ctx.json(found);

                } catch (NumberFormatException e) {
                    ctx.status(400);
                    badRequest(ctx, "Item id must be a whole number, got '" + ctx.pathParam("id") + "'");
                }
            });


            config.routes.post("/items", ctx -> {
                NewItem body = ctx.bodyAsClass(NewItem.class);               // read the JSON body (ctx.bodyAsClass) sent by client and converts it to a NewItem class/record

                if(body.name() == null){
                    ctx.status(400);
                    return;
                }

                Item created = new Item(nextId.getAndIncrement(), body.name());
                items.add(created);                                           // store it
                ctx.status(201);                                              // 201 Created
                ctx.header("Location", "/items/" + created.id());             // where the new item lives
                ctx.json(created);                                            // send it back
            });



            config.routes.delete("/items/{id}", ctx -> {
                int id;
                try {
                    id = Integer.parseInt(ctx.pathParam("id"));
                } catch (NumberFormatException e) {
                    ctx.status(400);
                    badRequest(ctx, "Item id must be a whole number, got '" + ctx.pathParam("id") + "'");
                    return;
                }
                boolean removed = items.removeIf(item -> item.id() == id);   // true if something was removed
                if (!removed) {
                    ctx.status(404).json(Map.of("error", "Item " + id + " not found"));
                    return;
                }
                ctx.status(204);   // 204 No Content: it worked, and there is nothing to send back
            });

//            config.routes.get("/greet",  ctx -> ctx.result("GET: hello " + ctx.queryParam("name")));
//            config.routes.post("/greet", ctx -> ctx.result("POST: hello " + ctx.formParam("name")));
        });
    }

    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start(5000);
    }

    public void start(int port) {
        this.appServer.start(port);
    }

    public Javalin app() {
        return appServer;
    }

    private static void badRequest(Context ctx, String message) {
        ctx.status(400).json(Map.of("error", message));
    }

}

