package com.weather.data.tests.functionaltests;

import com.weather.data.tests.entities.WeatherData;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertEquals;

/**
 * Tests historical weather api.
 */
public class HistoricalWeatherTest extends BaseFunctionTest {

    /**
     * Test happy path scenario.
     */
    @Test
    public void testHappyPath() {
        final String responseBody = given()
                .when()
                    .get("historical-weather/Jersey")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().body().asString();
        assertEquals(responseBody, "{}");
    }

    /**
     * DataProvider for happy path scenarios.
     *
     * @return An array of different happy path scenarios.
     */
    @DataProvider
    public Object[][] happyPathScenariosDataProvider() {
        final String cityName = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ENGLISH);
        final WeatherData data = new WeatherData();
        data.setCity(cityName);
        data.setMinimumTemperature(49.84);
        data.setMaximumTemperature(51.91);
        data.setTimeCreated(Instant.parse("2025-01-01T10:15:30Z"));
        getWeatherDataRepository().save(data);

        final String expectedDailyResponse = "{\"2025-01-01\":{\"minimumTemperature\":\"49.84\",\"maximumTemperature\":\"51.91\"}}";
        final String expectedWeeklyResponse = "{\"2025-Week1\":{\"minimumTemperature\":\"49.84\",\"maximumTemperature\":\"51.91\"}}";
        final String expectedMonthlyResponse = "{\"2025-1\":{\"minimumTemperature\":\"49.84\",\"maximumTemperature\":\"51.91\"}}";

        // query parameters, field name in the body, value in the body, error message
        return new Object[][] {
                {String.format("historical-weather/%s?startDate=2025-01-01", cityName), expectedDailyResponse},
                {String.format("historical-weather/%s?endDate=2025-01-02", cityName), expectedDailyResponse},
                {String.format("historical-weather/%s?startDate=2025-01-01&endDate=2025-01-02", cityName), expectedDailyResponse},
                {String.format("historical-weather/%s?startDate=2025-01-01&endDate=2025-01-02&interval=daily", cityName), expectedDailyResponse},
                {String.format("historical-weather/%s?startDate=2025-01-01&interval=weekly", cityName), expectedWeeklyResponse},
                {String.format("historical-weather/%s?endDate=2025-01-02&interval=monthly", cityName), expectedMonthlyResponse},
        };
    }

    /**
     * Test happy path scenario with different query parameters.
     */
    @Test(dataProvider = "happyPathScenariosDataProvider")
    public void testHappyPathWithData(final String uri, final String expectedResponse) {
        final String responseBody = given()
                .when()
                .get(uri)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        assertEquals(responseBody, expectedResponse);
    }

    /**
     * DataProvider for validation.
     *
     * @return An array different validation scenarios.
     */
    @DataProvider
    public Object[][] errorScenariosDataProvider() {
        final LocalDate today = LocalDate.now();
        final String tomorrow = today.plusDays(1).toString();

        // query parameters, field name in the body, value in the body, error message
        return new Object[][] {
                {String.format("startDate=%s", tomorrow),
                        "startDate", tomorrow, "Start date cannot be in future"},
                {String.format("endDate=%s", tomorrow),
                        "endDate", tomorrow, "End date cannot be in future"},
                {"startDate=2025-04-01&endDate=2025-03-31",
                        "startDate", "2025-04-01", "Start date cannot be after end date"},
                {"interval=invalid",
                        "interval", "invalid", "Interval invalid not supported. Allowed interval types are [DAILY, WEEKLY, MONTHLY]"},
        };
    }

    /**
     * Test with invalid query parameters.
     *
     * @param parameters        query parameter to be sent in the URI
     * @param fieldName         expected field name in the error body
     * @param value             expected value in the error body
     * @param expectedMessage   expected error message
     */
    @Test(dataProvider = "errorScenariosDataProvider")
    public void testInvalidParameters(final String parameters, final String fieldName, final String value, final String expectedMessage) {
        given()
            .when()
                .get("historical-weather/Baltimore?" + parameters)
            .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("field", equalTo(fieldName))
                .body("value", equalTo(value))
                .body("message", equalTo(expectedMessage))
                .body("localizedMessage", equalTo(expectedMessage));
    }
}
