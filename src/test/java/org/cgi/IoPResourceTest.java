package org.cgi;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.when;

@QuarkusTest
public class IoPResourceTest {

    @Test
    public void testGetOrders() {
        when().get("http://localhost:8080/api/orders")
                .then()
                .statusCode(200);
    }

}
