package com.weather.data.tests.functionaltests;

import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests current weather api.
 */
public class CurrentWeatherTest extends BaseFunctionTest {

    /**
     * Test happy path scenario.
     */
    @Test
    public void testHappyPath() {
        given()
            .when()
                .get("current-weather/erode")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("minimumTemperature", notNullValue())
                .body("maximumTemperature", notNullValue());
    }

    /**
     * Test 404 NOT_FOUND with an invalid city name.
     */
    @Test
    public void test404NotFound() {
        given()
                .when()
                .get("current-weather/invalidCity")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
