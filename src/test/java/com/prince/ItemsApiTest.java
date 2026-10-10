package com.prince;

import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemsApiTest {

    @Test
    void listReturnsTheStarterItems() {
        JavalinTest.test(new WebServer().app(), (server, client) -> {
            var response = client.get("/items");
            assertEquals(200, response.code());
            assertEquals("[{\"id\":1,\"name\":\"Milk\"},{\"id\":2,\"name\":\"Bread\"}]",
                    response.body().string());
        });
    }

    @Test
    void missingItemReturns404() {
        JavalinTest.test(new WebServer().app(), (server, client) -> {
            assertEquals(404, client.get("/items/99").code());
        });
    }

    @Test
    void nonNumericIdReturns400() {
        JavalinTest.test(new WebServer().app(), (server, client) -> {
            assertEquals(400, client.get("/items/abc").code());
        });
    }

    @Test
    void postCreatesAnItem() {
        JavalinTest.test(new WebServer().app(), (server, client) -> {
            var created = client.post("/items", new NewItem("Eggs"));
            assertEquals(201, created.code());
            assertEquals(200, client.get("/items/3").code());
        });
    }

    @Test
    void postWithoutNameReturns400() {
        JavalinTest.test(new WebServer().app(), (server, client) -> {
            assertEquals(400, client.post("/items", new NewItem(null)).code());
        });
    }

    @Test
    void deleteRemovesTheItem() {
        JavalinTest.test(new WebServer().app(), (server, client) -> {
            assertEquals(204, client.delete("/items/1").code());
            assertEquals(404, client.get("/items/1").code());
        });
    }
}